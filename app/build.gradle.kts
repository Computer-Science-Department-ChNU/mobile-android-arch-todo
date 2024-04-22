import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.grgit)
    alias(libs.plugins.detekt)
}

val debugKeystorePropertiesFile = rootProject.file("keystore/debug_keystore.properties")
val debugKeystoreProperties = Properties()
debugKeystoreProperties.load(FileInputStream(debugKeystorePropertiesFile))

val releaseKeystorePropertiesFile = rootProject.file("keystore/release_keystore.properties")
val releaseKeystoreProperties = Properties()
releaseKeystoreProperties.load(FileInputStream(releaseKeystorePropertiesFile))

android {
    namespace = "ua.edu.chnu.kkn.archtodo"
    compileSdk = 34

    defaultConfig {
        applicationId = "ua.edu.chnu.kkn.archtodo"
        minSdk = 31
        targetSdk = 34
        versionCode = grgit.tag.list().size
        versionName = grgit.describe {
            tags = true
            always = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("ownDebug") {
            keyAlias = debugKeystoreProperties["keyAlias"] as String
            keyPassword = debugKeystoreProperties["keyPassword"] as String
            storeFile = file(debugKeystoreProperties["storeFile"] as String)
            storePassword = debugKeystoreProperties["storePassword"] as String
        }
        create("release") {
            keyAlias = releaseKeystoreProperties["keyAlias"] as String
            keyPassword = releaseKeystoreProperties["keyPassword"] as String
            storeFile = file(releaseKeystoreProperties["storeFile"] as String)
            storePassword = releaseKeystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("ownDebug")
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}