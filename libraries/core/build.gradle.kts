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
    namespace = "com.project.core"
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.bundles.common)
    implementation(project(":libraries:permission"))
    kapt(libs.hiltSupport)

    //navigation
    implementation(libs.bundles.navigation)

    //network
    implementation(libs.bundles.network)

    implementation(libs.glide)
    ksp(libs.glideCompiler)

    //firestore
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseFirestore)

    //fireauth
    implementation(libs.firebaseAuth)

}
