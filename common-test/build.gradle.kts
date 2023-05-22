import AppDependencies.activityKtx
import AppDependencies.androidAnnotation
import AppDependencies.androidTest
import AppDependencies.coroutines
import AppDependencies.fragment
import AppDependencies.junit4
import AppDependencies.jvmAnnotation
import AppDependencies.kotlinStdLib
import AppDependencies.lifecycle
import AppDependencies.material
import AppDependencies.timberAndroid
import AppDependencies.workManager
import modules.CommonTestModuleConfig

plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
}

android {
    val moduleConfig = CommonTestModuleConfig
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
    kotlinStdLib()
    activityKtx()
    fragment()
    lifecycle()
    coroutines()
    timberAndroid()
    material()
    androidAnnotation()
    jvmAnnotation()
    workManager()

    junit4(AppDependencies.ConfigurationName.IMPLEMENTATION)
    androidTest(AppDependencies.ConfigurationName.IMPLEMENTATION)
}