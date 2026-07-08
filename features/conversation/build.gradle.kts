plugins {
    id("com.android.library")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())
}

android {
    namespace = "com.rikkeisoft.awesome.conversation"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(project(":libraries:core"))
    implementation(project(":libraries:permission"))

    //appcompat
    implementation(libs.bundles.common)

    kapt(libs.hiltSupport)

    //navigation
    implementation(libs.bundles.navigation)

    // Loading Image
    implementation(libs.glide)
    ksp(libs.glideCompiler)
    // Network
    implementation(libs.bundles.network)

    //firestore
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseFirestore)

    //fireauth
    implementation(libs.firebaseAuth)

    implementation(libs.shimmer)

}
