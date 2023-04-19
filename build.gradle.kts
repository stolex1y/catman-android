// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20-RC2")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.8.10")
    }

    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    id("com.android.application") version "7.4.1" apply false
    id("com.android.library") version "7.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.0" apply false
    id("checkstyle")
}