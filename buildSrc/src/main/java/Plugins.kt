import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.version

object Plugins {
    const val APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"
    const val KOTLIN_ANDROID = "kotlin-android"
    const val KOTLIN_KAPT = "kotlin-kapt"
    const val KOTLIN_JVM = "org.jetbrains.kotlin.jvm"
    const val SERIALIZATION = "org.jetbrains.kotlin.plugin.serialization"
    const val NAV_SAFEARGS = "androidx.navigation.safeargs"
    const val GMS = "com.google.gms.google-services"
    const val KSP = "com.google.devtools.ksp"
    const val JAVA_LIBRARY = "java-library"
    const val JAVA = "org.gradle.java"
    const val HILT = "com.google.dagger.hilt.android"
}

object PluginVersions {
    const val KSP = "1.8.21-1.0.11"
    const val APPLICATION = "8.0.1"
    const val ANDROID_LIBRARY = "8.0.1"
    const val KOTLIN_ANDROID = "1.8.0"
    const val SERIALIZATION = "1.4.0"
    const val KOTLIN_JVM = "1.8.0"
    const val HILT = "2.44"
}

fun PluginDependenciesSpecScope.ksp() {
    id(Plugins.KSP) version PluginVersions.KSP apply false
}

fun PluginDependenciesSpecScope.androidApp() {
    id(Plugins.APPLICATION) version PluginVersions.APPLICATION apply false
}

fun PluginDependenciesSpecScope.androidLib() {
    id(Plugins.ANDROID_LIBRARY) version PluginVersions.ANDROID_LIBRARY apply false
}

fun PluginDependenciesSpecScope.serialization() {
    id(Plugins.SERIALIZATION) version PluginVersions.SERIALIZATION apply false
}

fun PluginDependenciesSpecScope.kotlinJvm() {
    id(Plugins.KOTLIN_JVM) version PluginVersions.KOTLIN_JVM apply false
}

fun PluginDependenciesSpecScope.hilt() {
    id(Plugins.HILT) version PluginVersions.HILT apply false
}