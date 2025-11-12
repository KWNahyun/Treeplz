# Treeplz - Grow Your Digital Habits

Treeplz is a mobile application designed to visualize the environmental impact of your AI usage. By connecting to your AI service accounts, the app tracks your activity and represents its ecological footprint through a virtual tree that flourishes or withers based on your usage patterns.

## How to Run

1.  **Clone the repository.**
2.  **Open the project in Android Studio.**
3.  **Configure Google Sign-In:**
    *   Obtain a `google-services.json` file from your Firebase project console.
    *   Place the `google-services.json` file in the `app/` directory.
    *   In `LoginFragment.java`, uncomment the `.requestIdToken()` line and add your web client ID to enable server-side authentication.
4.  **Build and run the app on an emulator or a physical device.**

## Plugging in AI Usage Data

The core of this app is driven by AI usage data. The integration point is in `UsageRepository.java`.

1.  **`updateWithUsage(int tokensUsed, int requestCount, long timeSpent)`:**
    *   This method is the primary entry point for feeding data into the app.
    *   You should call this method from a background service, a periodic `WorkManager` task, or any other mechanism that can fetch data from your AI service provider's API.

2.  **`fetchMockUsageData()`:**
    *   Currently, this method provides mock data for demonstration purposes.
    *   **TODO:** Replace the contents of this method with your actual data-fetching logic (e.g., using Retrofit to call your backend or a third-party API).

3.  **Data-to-Visual Mapping:**
    *   Inside `updateWithUsage`, you'll find placeholder logic that maps token counts to the tree's progress (a `float` from 0.0 to 1.0).
    *   You can customize the `dailyTokenThreshold` and the mapping function to fit your desired sensitivity and visual response.
    *   For CO₂ calculations, replace the fictional conversion factor with scientifically-backed data for the specific AI models you are tracking.

## Design Goals & Visuals

The app aims for an emotional, artistic aesthetic, not a photorealistic one.

*   **Inspiration:** The visual style of the tree is inspired by the provided reference image, `ref_tree.png`. The goal was to create a more cohesive and organic version of it.
*   **Palette:** The color scheme uses a toned-down green primary color with muted earth tones to create a calm and natural feel.
*   **Animations:** All visual changes are animated smoothly to provide a sense of gradual change and impact.

### Design Fixes Implemented

This version of the app addresses several design issues from earlier concepts:

1.  **Unified Tree Structure:** The trunk, branches, and roots are drawn as a single, continuous path to look more organic.
2.  **Natural Leaf Placement:** Leaves are attached along the branches instead of floating in the air.
3.  **Coherent State Changes:** Environmental elements like animals and the weather are now tied to the tree's health, fading and changing in sync with the tree's state.
