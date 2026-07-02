import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())
}

android {
    namespace = "com.project.baseproject"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.rikkeisoft.awesome"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            storeFile = file("../keystore/base.jks")
            storePassword = "sdfg\$#sdfgFVGVR54"
            keyAlias = "baseProject"
            keyPassword = "sdfg\$#sdfgFVGVR54"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    applicationVariants.configureEach {
        outputs.configureEach {
            val out = this as BaseVariantOutputImpl
            val flavor = name
            val versionName = versionName.replace(".", "_")
            val extension = out.outputFileName.split(".").last()
            out.outputFileName = "${flavor}_${versionName}.${extension}"
        }
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(project(":libraries:core"))
    implementation(project(":libraries:permission"))
    implementation(project(":features:Demo"))
    implementation(project(":features:auth"))

    //appcompat
    implementation(libs.bundles.common)

    kapt(libs.hiltSupport)

    //navigation
    implementation(libs.bundles.navigation)

    //network
    implementation(libs.bundles.network)

    // Loading Image
    implementation(libs.glide)
    ksp(libs.glideCompiler)

    debugImplementation(libs.leak)

    //fireauth
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseAuth)
}