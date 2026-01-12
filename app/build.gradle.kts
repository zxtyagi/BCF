plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.eagsoftware.basiccashflow"
    compileSdk = 36

    defaultConfig {
        // Major.Patch - Need to keep this here for F-Droid
        versionName = "1.0"
        versionCode = 1
        applicationId = "org.eagsoftware.basiccashflow"
        minSdk = 26
        targetSdk = 36

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
        debug {
            // Necessario per eseguire su dispositivo senza disinstallare versione release esistente
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures{
        dataBinding = true
    }

    // Necessary for F-Droid
    dependenciesInfo{
        includeInApk = false
        includeInBundle = false
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