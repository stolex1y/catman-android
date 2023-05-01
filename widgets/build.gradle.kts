import AppDependency.activityKtx
import AppDependency.androidTest
import AppDependency.fragment
import AppDependency.kotlinStdLib
import AppDependency.lifecycle
import AppDependency.test
import modules.WidgetsModuleConfig

plugins {
    id(Plugin.ANDROID_LIBRARY)
    id(Plugin.KOTLIN_ANDROID)
}

android {
    val moduleConfig = WidgetsModuleConfig
    namespace = moduleConfig.namespace
    compileSdk = moduleConfig.compileSdk

    defaultConfig {
        minSdk = moduleConfig.minSdk
        version = moduleConfig.versionCode

        testInstrumentationRunner = moduleConfig.testInstrumentationRunner
        consumerProguardFiles(moduleConfig.proguardConsumerRules)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = moduleConfig.sourceJdk
        targetCompatibility = moduleConfig.targetJdk
    }
    kotlinOptions {
        jvmTarget = moduleConfig.jvmTarget
    }
}

dependencies {
    kotlinStdLib()
    activityKtx()
    fragment()
    lifecycle()

    //material
    implementation("com.google.android.material:material:1.8.0")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    //timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    //testing
    test()
    androidTest()
}