package kr.co.example.treeplz.ui.usage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import kr.co.example.treeplz.R;
import kr.co.example.treeplz.data.viewmodel.UsageViewModel;

public class UsageFragment extends Fragment {

    private UsageViewModel usageViewModel;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_usage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usageViewModel = new ViewModelProvider(requireActivity()).get(UsageViewModel.class);

        final TextView dailyUsageText = view.findViewById(R.id.daily_usage_text);
        final TextView weeklyUsageText = view.findViewById(R.id.weekly_usage_text);

        usageViewModel.getUsageSummary().observe(getViewLifecycleOwner(), usageSummary -> {
            if (usageSummary != null) {
                dailyUsageText.setText(String.format("Daily Usage: %d prompts, %dg CO₂", usageSummary.getDailyPrompts(), usageSummary.getDailyCo2()));
                weeklyUsageText.setText(String.format("Weekly Usage: %d prompts, %dg CO₂", usageSummary.getWeeklyPrompts(), usageSummary.getWeeklyCo2()));
            }
        });

        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = event.getX();
                    float deltaX = x2 - x1;
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // Right swipe
                        if (x2 > x1) {
                            NavHostFragment.findNavController(this).navigateUp();
                        }
                    }
                    break;
            }
            return true; // Consume the touch event
        });
    }
}