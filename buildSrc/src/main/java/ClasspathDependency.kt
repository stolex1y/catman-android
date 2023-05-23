object ClasspathDependency {
    private const val androidGradle = "8.2.0-alpha03"
    private const val kotlin = "1.8.20-RC2"
    private const val gms = "4.3.15"
    private const val serialization = "1.8.10"
    private const val navigation = "2.5.3"
    
    const val ANDROID_GRADLE = "com.android.tools.build:gradle:$androidGradle"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin"
    const val GMS = "com.google.gms:google-services:$gms"
    const val SERIALIZATION = "org.jetbrains.kotlin:kotlin-serialization:$serialization"
    const val NAVIGATION = "androidx.navigation:navigation-safe-args-gradle-plugin:$navigation"
}