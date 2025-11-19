package kr.co.example.treeplz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EfficientPromptingActivity extends AppCompatActivity {

    private LinearLayout tipContainer;
    private TextView tvTotalTokens, tvTitle, tvSubtitle, tvBannerTitle, tvBannerSubtitle, tvCtaTitle, tvCtaDesc;
    private Switch switchLanguage;

    static class PromptingTip {
        String id;
        String categoryKey;
        String titleKey;
        String descriptionKey;
        String badExampleKey;
        String goodExampleKey;
        String tokensSaved;
        String icon;
        PromptingTip(String id, String cat, String title, String desc, String bad, String good, String tokens, String icon) {
            this.id=id; this.categoryKey=cat; this.titleKey=title; this.descriptionKey=desc; this.badExampleKey=bad; this.goodExampleKey=good; this.tokensSaved=tokens; this.icon=icon;
        }
    }

    private final List<PromptingTip> tips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efficient_prompting);

        // views
        tipContainer = findViewById(R.id.tipContainer);
        tvTotalTokens = findViewById(R.id.tvTotalTokens);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvBannerTitle = findViewById(R.id.tvBannerTitle);
        tvBannerSubtitle = findViewById(R.id.tvBannerSubtitle);
        tvCtaTitle = findViewById(R.id.tvCtaTitle);
        tvCtaDesc = findViewById(R.id.tvCtaDesc);
        switchLanguage = findViewById(R.id.switchLanguage);

        // back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // language switch
        switchLanguage.setChecked(LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO);
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) LanguageManager.getInstance().setLanguage(LanguageManager.Language.KO);
            else LanguageManager.getInstance().setLanguage(LanguageManager.Language.EN);
            refreshTexts();
        });

        initTips();
        renderTips();
        refreshTexts();
    }

    private void initTips() {
        // copy of TSX data
        tips.clear();
        tips.add(new PromptingTip("greetings","prompting.removeGreetings.title","prompting.removeGreetings.subtitle","prompting.removeGreetings.description","prompting.removeGreetings.bad","prompting.removeGreetings.good","~15","🎯"));
        tips.add(new PromptingTip("specific","prompting.beSpecific.title","prompting.beSpecific.subtitle","prompting.beSpecific.description","prompting.beSpecific.bad","prompting.beSpecific.good","~30","📝"));
        tips.add(new PromptingTip("format","prompting.defineFormat.title","prompting.defineFormat.subtitle","prompting.defineFormat.description","prompting.defineFormat.bad","prompting.defineFormat.good","~25","📋"));
        tips.add(new PromptingTip("constraints","prompting.setConstraints.title","prompting.setConstraints.subtitle","prompting.setConstraints.description","prompting.setConstraints.bad","prompting.setConstraints.good","~40","📏"));
        tips.add(new PromptingTip("templates","prompting.reusableTemplates.title","prompting.reusableTemplates.subtitle","prompting.reusableTemplates.description","prompting.reusableTemplates.bad","prompting.reusableTemplates.good","~20","🔄"));
        tips.add(new PromptingTip("context","prompting.minimalContext.title","prompting.minimalContext.subtitle","prompting.minimalContext.description","prompting.minimalContext.bad","prompting.minimalContext.good","~35","🎯"));
        tips.add(new PromptingTip("examples","prompting.useExamples.title","prompting.useExamples.subtitle","prompting.useExamples.description","prompting.useExamples.bad","prompting.useExamples.good","~20","💡"));
        tips.add(new PromptingTip("batch","prompting.batchRequests.title","prompting.batchRequests.subtitle","prompting.batchRequests.description","prompting.batchRequests.bad","prompting.batchRequests.good","~15","📦"));
    }

    private void renderTips() {
        tipContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (PromptingTip tip : tips) {
            View v = inflater.inflate(R.layout.tip_card, tipContainer, false);

            TextView tvIcon = v.findViewById(R.id.tvIcon);
            TextView tvCategory = v.findViewById(R.id.tvCategory);
            TextView tvSubtitle = v.findViewById(R.id.tvSubtitle);
            TextView tvTokensBadge = v.findViewById(R.id.tvTokensBadge);
            ImageView ivChevron = v.findViewById(R.id.ivChevron);
            LinearLayout layoutDetail = v.findViewById(R.id.layoutDetail);
            TextView tvDescription = v.findViewById(R.id.tvDescription);
            TextView tvBadExample = v.findViewById(R.id.tvBadExample);
            TextView tvGoodExample = v.findViewById(R.id.tvGoodExample);

            tvIcon.setText(tip.icon);
            tvTokensBadge.setText(tip.tokensSaved);

            // set texts using LanguageManager
            tvCategory.setText(LanguageManager.getInstance().t(tip.categoryKey));
            tvSubtitle.setText(LanguageManager.getInstance().t(tip.titleKey));
            tvDescription.setText(LanguageManager.getInstance().t(tip.descriptionKey));
            tvBadExample.setText(LanguageManager.getInstance().t(tip.badExampleKey));
            tvGoodExample.setText(LanguageManager.getInstance().t(tip.goodExampleKey));

            // expand/collapse
            v.setOnClickListener(view -> {
                if (layoutDetail.getVisibility() == View.VISIBLE) {
                    layoutDetail.setVisibility(View.GONE);
                    ivChevron.setRotation(0f);
                } else {
                    layoutDetail.setVisibility(View.VISIBLE);
                    ivChevron.setRotation(180f);
                }
            });

            tipContainer.addView(v);
        }
    }

    private void refreshTexts() {
        // header & banner & CTA
        tvTitle.setText(LanguageManager.getInstance().t("prompting.title"));
        tvSubtitle.setText(LanguageManager.getInstance().t("prompting.subtitle"));
        tvBannerTitle.setText(LanguageManager.getInstance().t("prompting.potentialSavings"));
        tvBannerSubtitle.setText(LanguageManager.getInstance().t("prompting.usingTechniques"));

        // compute total tokens saved
        int total = 0;
        Pattern p = Pattern.compile("\\d+");
        for (PromptingTip tip : tips) {
            Matcher m = p.matcher(tip.tokensSaved);
            if (m.find()) {
                try { total += Integer.parseInt(m.group()); } catch (Exception ignored) {}
            }
        }
        tvTotalTokens.setText("~" + total);

        // CTA texts
        tvCtaTitle.setText(LanguageManager.getInstance().t("prompting.makeEveryTokenCount"));
        tvCtaDesc.setText(LanguageManager.getInstance().t("prompting.description"));

        // update each inflated tip card texts
        // simple approach: re-render all tips
        renderTips();
    }
}
