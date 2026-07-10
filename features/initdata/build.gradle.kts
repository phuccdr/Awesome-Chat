plugins {
    kotlin("jvm")
}

repositories {
    google()
    mavenCentral()
}
kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())
}


dependencies {
    implementation("com.google.firebase:firebase-admin:9.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.slf4j:slf4j-simple:2.0.9")
}
