plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.errorapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.errorapplication"
        minSdk = 24
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

    applicationVariants.all {
        this.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val variant = this.buildType.name
                val flavor = this.flavorName
                val appName = this.applicationId.substringAfterLast('.').replaceFirstChar { it.uppercase() }
                var apkName = if (flavor.isNotEmpty()) {
                    "${appName}_${flavor[0].uppercase()}${flavor.substring(1)}_${this.versionName}"
                } else {
                    "${appName}_${this.versionName}"
                }
                //if (variant.isNotEmpty()) apkName += "_$variant"
                apkName += ".apk"
                println("ApkName=$apkName $variant")
                output.outputFileName = apkName
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
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}