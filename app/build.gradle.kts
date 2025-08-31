plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" // Required with Kotlin 2.1+
    id("kotlin-kapt")
    id("kotlin-parcelize")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.basalbody.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.basalbody.app"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            android.buildFeatures.buildConfig = true
            manifestPlaceholders["crashlyticsEnabled"] = true
            buildConfigField("Boolean", "ENABLE_LOG", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            android.buildFeatures.buildConfig = true
            manifestPlaceholders["crashlyticsEnabled"] = true
            buildConfigField("Boolean", "ENABLE_LOG", "true")
        }
    }

    secrets {
        propertiesFileName = "secrets.properties"
        defaultPropertiesFileName = "local.defaults.properties"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    flavorDimensions += listOf("MXB")

    productFlavors {
        create("development") {
            dimension = "MXB"
            buildConfigField("String", "BASE_URL", "\"https://dev.rbt.sa/api/driver/\"")
            buildConfigField("String", "SOCKET_BASE_URL", "\"https://dev.socket.rbt.sa\"")
        }
        create("production") {
            dimension = "MXB"
            buildConfigField("String", "BASE_URL", "\"https://dev.rbt.sa/api/driver/\"")
            buildConfigField("String", "SOCKET_BASE_URL", "\"https://dev.socket.rbt.sa\"")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Swipe to Refresh
    implementation(libs.androidx.swiperefreshlayout)

    // Jetpack Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit and GSON
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Glide
    implementation(libs.glide)
    ksp(libs.ksp.glide)

    implementation(fileTree(mapOf(
        "dir" to "src/main/libs",
        "include" to listOf("*.aar")
    )))

    // Firebase
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics)

    // Socket.IO
    implementation(libs.socket.io.client)

    // Location and Maps
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.places)
    implementation(libs.places)
    implementation(libs.maps.utils.ktx)

    // Permissions + Lottie + DataStore
    implementation(libs.dexter)
    implementation(libs.lottie)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.datastore.preferences)
}