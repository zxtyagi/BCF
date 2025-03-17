plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.eagsoftware.basiccashflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.eagsoftware.basiccashflow"
        minSdk = 26
        targetSdk = 35
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
    buildFeatures{
        dataBinding = true
    }
}

dependencies {
    // Implementazioni per Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Implementazione per ViewModel
    implementation(libs.lifecycle.viewmodel.ktx)

    // Implementazione per LiveData
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.extensions)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}