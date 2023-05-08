import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.version

object Plugin {
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
}

object PluginVersion {
    const val KSP = "1.8.21-1.0.11"
    const val APPLICATION = "8.2.0-alpha01"
    const val ANDROID_LIBRARY = "8.2.0-alpha01"
    const val KOTLIN_ANDROID = "1.8.0"
    const val SERIALIZATION = "1.4.0"
    const val KOTLIN_JVM = "1.8.0"
}

fun PluginDependenciesSpecScope.ksp() {
    id(Plugin.KSP) version PluginVersion.KSP apply false
}

fun PluginDependenciesSpecScope.androidApp() {
    id(Plugin.APPLICATION) version PluginVersion.APPLICATION apply false
}

fun PluginDependenciesSpecScope.androidLib() {
    id(Plugin.ANDROID_LIBRARY) version PluginVersion.ANDROID_LIBRARY apply false
}

fun PluginDependenciesSpecScope.serialization() {
    id(Plugin.SERIALIZATION) version PluginVersion.SERIALIZATION apply false
}

fun PluginDependenciesSpecScope.kotlinJvm() {
    id(Plugin.KOTLIN_JVM) version PluginVersion.KOTLIN_JVM apply false
}