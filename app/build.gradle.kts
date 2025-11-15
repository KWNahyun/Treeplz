plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "kr.co.example.treeplz"
    compileSdk = 36

    defaultConfig {
        applicationId = "kr.co.example.treeplz"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // AndroidX Navigation
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}