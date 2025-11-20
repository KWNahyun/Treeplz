from fastapi import FastAPI, Header, HTTPException
from pydantic import BaseModel
import httpx

app = FastAPI()

class AiUsage(BaseModel):
    requests: int
    tokens: int
    timeSpent: float
    carbonFootprint: int

@app.get("/usage/me", response_model=AiUsage)
async def get_usage_me(authorization: str = Header(None)):
    if authorization is None:
        raise HTTPException(status_code=401, detail="Missing Authorization header")

    # "Bearer sk-xxxx" 형태에서 키만 추출
    if not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Invalid header")

    api_key = authorization.replace("Bearer ", "")

    # -------------------------------
    # 실제 OpenAI Usage API 호출
    # -------------------------------
    url = "https://api.openai.com/v1/usage"   # 예시 endpoint (변경될 수 있음)

    headers = {
        "Authorization": f"Bearer {api_key}"
    }

    async with httpx.AsyncClient() as client:
        response = await client.get(url, headers=headers)

    if response.status_code != 200:
        raise HTTPException(status_code=500, detail="OpenAI server error")

    data = response.json()

    # -------------------------------
    # 네가 원하는 형태로 데이터 변환
    # -------------------------------
    # 예: 일일 사용량만 사용한다고 가정
    requests = data.get("total_requests", 0)
    tokens = data.get("total_tokens", 0)
    time_spent = tokens / 200.0  # 임시 계산 예시
    carbon = int(tokens * 0.004)  # 임시 탄소 환산 예시

    return AiUsage(
        requests=requests,
        tokens=tokens,
        timeSpent=time_spent,
        carbonFootprint=carbon
    )
