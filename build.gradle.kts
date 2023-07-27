// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
//        classpath(ClasspathDependency.ANDROID_GRADLE)
//        classpath(ClasspathDependency.KOTLIN)
//        classpath(ClasspathDependency.GMS)
//        classpath(ClasspathDependency.NAVIGATION)
//        classpath(ClasspathDependency.SERIALIZATION)
    }

    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.lib) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.gms) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.navigation.safeargs) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
