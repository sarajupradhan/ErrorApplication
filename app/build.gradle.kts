plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.errorapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.errorapplication"
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

    applicationVariants.all {
        this.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val variant = this.buildType.name
                val flavor = this.flavorName
                val appName = "ErrorGeneration"
                var apkName = if (flavor.isNotEmpty()) {
                    "${appName}_${flavor[0].uppercase()}${flavor.substring(1)}_${this.versionName}"
                } else {
                    "${appName}-${this.versionName}"
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
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation(project(":error"))
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation( "com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.1.0")
    implementation( "com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0")
    implementation ("com.squareup.okhttp3:okhttp:3.12.0")
    implementation ("com.squareup.okhttp3:okhttp-urlconnection:3.0.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation(libs.activity)
    implementation(libs.activity)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)
    implementation(libs.retrofit.gson)
    testImplementation(libs.okHttp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}