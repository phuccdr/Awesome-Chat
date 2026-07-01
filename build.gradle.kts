// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    extra.apply {
        set("versionJava", JavaVersion.VERSION_1_8)
    }
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.androidGradle)
        classpath(libs.kotlinGradle)
        classpath(libs.hiltGradle)
        classpath(libs.gms)
    }
}
plugins {
    id("org.sonarqube") version "4.2.1.3168"
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
}

sonar {
    properties {
        property("sonar.projectKey", "D1:2023:Android_Base_MVVM")
        property("sonar.projectName", "D1:2023:Android_Base_MVVM")
        property("sonar.host.url", "https://sonar.rikkei.org/")
        property("sonar.token", "sqp_23a28ba816325cb5dfa4863bf7e59ca479eecec9")
        property("sonar.projectVersion", "2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task("clean") {
    delete(project.buildDir)
}

task<Exec>("buildAppDebugToFirebase") {
    commandLine("git", "tag", createTagDistribute("firebase", "debug"))
    doLast {
        exec {
            commandLine("git", "push", "--tags")
        }
    }
}

task<Exec>("buildAppReleaseToFirebase") {
    commandLine("git", "tag", createTagDistribute("firebase", "release"))

    doLast {
        exec {
            commandLine("git", "push", "--tags")
        }
    }
}

task<Exec>("runSonar") {
    commandLine("./gradlew", "sonar")
}

fun createTagDistribute(targetDeploy: String, buildType: String) =
    targetDeploy + "_" + buildType + "_" +
            libs.versions.versionName + "+" +
            libs.versions.versionCode