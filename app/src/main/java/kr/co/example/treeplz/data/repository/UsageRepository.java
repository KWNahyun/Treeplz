package kr.co.example.treeplz.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import kr.co.example.treeplz.data.viewmodel.UsageViewModel;

public class UsageRepository {

    private final MutableLiveData<UsageViewModel.UsageSummary> usageSummary = new MutableLiveData<>();
    private final MutableLiveData<Float> treeProgress = new MutableLiveData<>();

    public UsageRepository() {
        // TODO: Replace with actual data fetching from a local database or remote API
        fetchMockUsageData();
    }

    public LiveData<UsageViewModel.UsageSummary> getUsageSummary() {
        return usageSummary;
    }

    public LiveData<Float> getTreeProgress() {
        return treeProgress;
    }

    // TODO: This method would be called by a background service or a periodic worker
    public void updateWithUsage(int tokensUsed, int requestCount, long timeSpent) {
        // Placeholder for a real API hook to update usage data
        // In a real app, this data would be persisted and aggregated.

        // --- Placeholder mapping logic ---
        // This is where you would map your AI usage metrics to the tree's state.
        // The mapping can be as simple or as complex as needed.
        // Example: Normalize token usage against a daily threshold.
        final float dailyTokenThreshold = 10000.0f; // Example threshold

        // tokenUsageNormalized = (todayTokens / dailyThreshold) -> clamp 0..1
        float tokenUsageNormalized = Math.min(tokensUsed / dailyTokenThreshold, 1.0f);

        // treeProgress = smoothStep(tokenUsageNormalized)
        // Using a simple linear mapping here, but a non-linear function (like smoothstep)
        // would provide a more nuanced visual transition.
        float progress = tokenUsageNormalized;

        // Post the new progress to the LiveData
        treeProgress.postValue(progress);

        // Update the summary as well
        // TODO: Fetch real data instead of using the new data directly
        fetchMockUsageData(); // For now, just refetch mock data
    }


    private void fetchMockUsageData() {
        // TODO: This is a placeholder. Implement a real data source (e.g., Room database, Retrofit API call).
        // The call would involve fetching data for today and the last 7 days.
        // This could also be where you get a server auth code from Google Sign-In and exchange it for your own app tokens.

        // Mock data for demonstration
        int dailyPrompts = 45;
        int dailyTokens = 8500;
        int weeklyPrompts = 315;
        int weeklyTokens = 59500;

        // Example mapping: 1g CO2 per 500 tokens (this is a fictional value)
        // TODO: Replace with scientifically-backed energy coefficients for specific models.
        int dailyCo2 = dailyTokens / 500;
        int weeklyCo2 = weeklyTokens / 500;

        UsageViewModel.UsageSummary summary = new UsageViewModel.UsageSummary(dailyPrompts, dailyCo2, weeklyPrompts, weeklyCo2);
        usageSummary.setValue(summary);

        // Also update the tree progress based on this mock data
        updateWithUsage(dailyTokens, dailyPrompts, 0);
    }

    /**
     * Placeholder for fetching daily AI usage data.
     * In a real implementation, this would query a local database or a remote server.
     */
    public void getDailyUsage() {
        // TODO: Implement API call to get daily usage data.
        // This method could take user credentials or an auth token.
    }

    /**
     * Placeholder for fetching weekly AI usage data.
     */
    public void getWeeklyUsage() {
        // TODO: Implement API call to get weekly usage data.
    }
}