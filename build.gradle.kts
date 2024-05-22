// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}

plugins {
    id("com.android.application") version "8.4.1" apply false
    id("com.android.library")version "8.4.1" apply false
    id("org.jetbrains.kotlin.android")version "1.9.22" apply false
    id("com.google.devtools.ksp")version "1.9.23-1.0.19" apply false
}
