import AppDependencies.activityKtx
import AppDependencies.androidAnnotation
import AppDependencies.androidConstraintLayout
import AppDependencies.androidCoreKtx
import AppDependencies.appcompat
import AppDependencies.coroutines
import AppDependencies.fragment
import AppDependencies.gson
import AppDependencies.kotlinStdLib
import AppDependencies.lifecycle
import AppDependencies.material
import AppDependencies.moduleAndroidTestImplementation
import AppDependencies.moduleImplementation
import AppDependencies.navigation
import AppDependencies.timberAndroid
import AppDependencies.workManager
import modules.Modules
import modules.UiCommonModuleConfig

plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
}

android {
    val moduleConfig = UiCommonModuleConfig
    namespace = moduleConfig.namespace
    compileSdk = moduleConfig.compileSdk

    defaultConfig {
        minSdk = moduleConfig.minSdk
        version = moduleConfig.versionCode

        testInstrumentationRunner = moduleConfig.testInstrumentationRunner

        testProguardFiles(
            moduleConfig.testProguardRules
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                moduleConfig.proguardRules
            )
        }

        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = moduleConfig.sourceJdk
        targetCompatibility = moduleConfig.targetJdk
    }
    kotlinOptions {
        jvmTarget = moduleConfig.targetJdk.majorVersion
    }
}

dependencies {
    moduleImplementation(Modules.COMMON)
    moduleAndroidTestImplementation(Modules.COMMON_TEST)

    kotlinStdLib()
    androidCoreKtx()
    appcompat()
    androidConstraintLayout()
    lifecycle()
    activityKtx()
    fragment()
    material()
    coroutines()
    timberAndroid()
    androidAnnotation()
    navigation()
    workManager()
    gson()
}
