import AppDependencies.androidTest
import AppDependencies.hilt
import AppDependencies.junit4
import AppDependencies.kotlinStdLib
import AppDependencies.moduleImplementation
import AppDependencies.room
import AppDependencies.timberAndroid
import modules.DataSourceLocalModuleConfig
import modules.Modules

plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KSP)
    id(Plugins.KOTLIN_KAPT)
    id(Plugins.HILT)
}

android {
    val moduleConfig = DataSourceLocalModuleConfig
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
    moduleImplementation(Modules.DOMAIN_MODEL)
    moduleImplementation(Modules.COMMON)

    kotlinStdLib()
    timberAndroid()
    room()
    hilt()
    junit4()
    androidTest()
}
