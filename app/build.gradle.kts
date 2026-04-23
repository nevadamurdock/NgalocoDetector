import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
}


val properties = Properties()
properties.load(rootProject.file("local.properties").inputStream())

val verCode: Int by rootProject.extra
val verName: String by rootProject.extra

val minSdker: Int by rootProject.extra
val targetSdker: Int by rootProject.extra
val compileSdker: Int by rootProject.extra
val ndker: String by rootProject.extra
val javaer: JavaVersion by rootProject.extra

android {
    compileSdk = compileSdker
    ndkersion = ndker

    buildFeatures {
        compose = true
        prefab = true
    }

    defaultConfig {
        applicationId = "com.nevadamurdock.ngalocodetector"
        minSdk = minSdker
        targetSdk = targetSdker
        versionCode = verCode
        versionName = verName
    }

    val config = properties.getProperty("fileDir")?.let {
        signingConfigs.create("config") {
            storeFile = file(it)
            storePassword = properties.getProperty("storePassword")
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
        }
    }

    buildTypes {
        all {
            signingConfig = config ?: signingConfigs["debug"]
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    composeOptions {
        kotlinCompilerExtensionersion = "1.2.0"
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
    implementation(project(":library"))

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.compose.material:material-icons-extended:1.2.1")
    implementation("androidx.compose.material3:material3:1.0.0-alpha16")
    implementation("androidx.compose.ui:ui:1.2.1")
    implementation("androidx.compose.ui:ui-tooling:1.2.1")
    implementation("com.google.android.material:material:1.6.1")

    implementation("com.github.Tencent.soter:soter-wrapper:2.1.8")
}
