package kr.co.example.treeplz.data.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import kr.co.example.treeplz.data.repository.UsageRepository;

public class UsageViewModel extends ViewModel {

    private final UsageRepository usageRepository;
    private final LiveData<UsageSummary> usageSummary;
    private final LiveData<Float> treeProgress;

    public UsageViewModel() {
        usageRepository = new UsageRepository();
        usageSummary = usageRepository.getUsageSummary();
        treeProgress = usageRepository.getTreeProgress();
    }

    public LiveData<UsageSummary> getUsageSummary() {
        return usageSummary;
    }

    public LiveData<Float> getTreeProgress() {
        return treeProgress;
    }

    public static class UsageSummary {
        private final int dailyPrompts;
        private final int dailyCo2;
        private final int weeklyPrompts;
        private final int weeklyCo2;

        public UsageSummary(int dailyPrompts, int dailyCo2, int weeklyPrompts, int weeklyCo2) {
            this.dailyPrompts = dailyPrompts;
            this.dailyCo2 = dailyCo2;
            this.weeklyPrompts = weeklyPrompts;
            this.weeklyCo2 = weeklyCo2;
        }

        public int getDailyPrompts() {
            return dailyPrompts;
        }

        public int getDailyCo2() {
            return dailyCo2;
        }

        public int getWeeklyPrompts() {
            return weeklyPrompts;
        }

        public int getWeeklyCo2() {
            return weeklyCo2;
        }
    }
}