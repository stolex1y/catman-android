import AppDependencies.activityKtx
import AppDependencies.androidAnnotation
import AppDependencies.androidTest
import AppDependencies.coroutines
import AppDependencies.fragment
import AppDependencies.junit4
import AppDependencies.kotlinStdLib
import AppDependencies.lifecycle
import AppDependencies.material
import AppDependencies.moduleImplementation
import AppDependencies.timberAndroid
import modules.Modules
import modules.WidgetsModuleConfig

plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
}

android {
    val moduleConfig = WidgetsModuleConfig
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
    moduleImplementation(Modules.UI_COMMON)

    kotlinStdLib()
    activityKtx()
    fragment()
    lifecycle()
    coroutines()
    timberAndroid()
    material()
    androidAnnotation()

    //testing
    junit4()
    androidTest()
}
