// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(ClasspathDependency.ANDROID_GRADLE)
        classpath(ClasspathDependency.KOTLIN)
        classpath(ClasspathDependency.GMS)
        classpath(ClasspathDependency.NAVIGATION)
        classpath(ClasspathDependency.SERIALIZATION)
    }

    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    androidApp()
    androidLib()
    serialization()
    ksp()
    kotlinJvm()
    hilt()
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}