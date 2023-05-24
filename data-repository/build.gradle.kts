import AppDependencies.coroutines
import AppDependencies.hilt
import AppDependencies.kotlinStdLib
import AppDependencies.moduleImplementation
import AppDependencies.timberAndroid
import modules.DataRepositoryModuleConfig
import modules.Modules

plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KSP)
    id(Plugins.KOTLIN_KAPT)
    id(Plugins.HILT)
}

android {
    val moduleConfig = DataRepositoryModuleConfig
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
    moduleImplementation(Modules.DOMAIN_REPOSITORY_API)
    moduleImplementation(Modules.DOMAIN_MODEL)
    moduleImplementation(Modules.DATA_SOURCE_LOCAL)
    moduleImplementation(Modules.COMMON)

    kotlinStdLib()
    timberAndroid()
    coroutines()
    hilt()
}
