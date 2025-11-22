from fastapi import FastAPI, Header, HTTPException
from pydantic import BaseModel
from datetime import date
from typing import Optional, Dict
import httpx
import logging
import os
import time
from collections import defaultdict

app = FastAPI()

# ----------------------
# 사용량 누적용 메모리 스토어
# key: "YYYY-MM-DD" -> dict
# ----------------------
usage_store: Dict[str, Dict[str, float]] = defaultdict(
    lambda: {"requests": 0, "tokens": 0, "time_ms": 0}
)

class AiUsage(BaseModel):
    requests: int
    tokens: int
    timeSpent: float          # 분 단위
    carbonFootprint: int      # g 단위

class ChatRequest(BaseModel):
    # 아주 단순하게: 유저 메시지 한 줄만 받는다고 가정
    message: str

class ChatResponse(BaseModel):
    reply: str


def get_api_key_from_header(authorization: Optional[str]) -> str:
    """
    헤더에서 Bearer 토큰 꺼내기.
    없으면 환경변수 OPENAI_API_KEY 사용.
    """
    if authorization and authorization.startswith("Bearer "):
        key = authorization.replace("Bearer ", "").strip()
        if key:
            return key

    env_key = os.getenv("OPENAI_API_KEY")
    if not env_key:
        raise HTTPException(status_code=401, detail="No API key provided")
    return env_key


# ==========================
# 1) 실제 OpenAI 호출 + 사용량 누적
# ==========================
@app.post("/chat", response_model=ChatResponse)
async def chat(
    req: ChatRequest,
    authorization: Optional[str] = Header(None),
):
    api_key = get_api_key_from_header(authorization)

    url = "https://api.openai.com/v1/chat/completions"
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json",
    }
    payload = {
        "model": "gpt-4o-mini",
        "messages": [
            {"role": "user", "content": req.message}
        ],
    }

    start = time.monotonic()
    async with httpx.AsyncClient() as client:
        res = await client.post(url, headers=headers, json=payload)
    elapsed_ms = int((time.monotonic() - start) * 1000)

    if res.status_code != 200:
        logging.error("OpenAI error: %s", res.text)
        raise HTTPException(status_code=500, detail="OpenAI error")

    data = res.json()
    # ↙↙↙ 여기! 진짜 사용량
    usage = data.get("usage", {}) or {}
    total_tokens = usage.get("total_tokens", 0)

    # 오늘 날짜 기준 누적
    today = date.today().isoformat()
    store = usage_store[today]
    store["requests"] += 1
    store["tokens"] += total_tokens
    store["time_ms"] += elapsed_ms

    # 첫 번째 assistant 메시지만 돌려주기
    reply = data["choices"][0]["message"]["content"]
    return ChatResponse(reply=reply)


# ==========================
# 2) 오늘 사용량 조회
# ==========================
@app.get("/usage/me", response_model=AiUsage)
async def get_usage_me(
    authorization: Optional[str] = Header(None),
    query_date: Optional[str] = None,
):
    # 여기서는 API 키를 실제로 쓰진 않아도 되고,
    # "키가 있어야만 조회 가능"하게 만들고 싶으면 아래 줄 유지
    _ = get_api_key_from_header(authorization)

    if query_date is None or query_date.strip() == "":
        query_date = date.today().isoformat()

    store = usage_store.get(query_date, {"requests": 0, "tokens": 0, "time_ms": 0})

    requests = int(store["requests"])
    tokens = int(store["tokens"])
    time_ms = int(store["time_ms"])

    minutes = time_ms / 1000.0 / 60.0
    carbon = int(tokens * 0.004)   # 네가 쓰던 탄소 계산 그대로

    return AiUsage(
        requests=requests,
        tokens=tokens,
        timeSpent=minutes,
        carbonFootprint=carbon,
    )
