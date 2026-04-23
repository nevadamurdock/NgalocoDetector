val minSdker: Int by rootProject.extra
val targetSdker: Int by rootProject.extra
val compileSdker: Int by rootProject.extra
val ndker: String by rootProject.extra
val javaer: Javaersion by rootProject.extra

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "icu.nullptr.applistdetector.library"
    compileSdk = compileSdker
    ndkersion = ndker

    buildFeatures {
        prefab = true
    }

    defaultConfig {
        minSdk = minSdker
        targetSdk = targetSdker

        externalNativeBuild.ndkBuild {
            arguments += "-j${Runtime.getRuntime().availableProcessors()}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            consumerProguardFiles("proguard-rules.pro")
        }
    }

    externalNativeBuild.ndkBuild {
        path("src/main/cpp/Android.mk")
    }

    compileOptions {
        sourceCompatibility = javaer
        targetCompatibility = javaer
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("com.android.tools.build:apkzlib:7.2.2")
    implementation("io.github.vvb2060.ndk:xposeddetector:2.2")
}
