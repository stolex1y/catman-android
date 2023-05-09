import AppDependencies.activityKtx
import AppDependencies.androidAnnotation
import AppDependencies.androidTest
import AppDependencies.coroutines
import AppDependencies.fragment
import AppDependencies.jvmAnnotation
import AppDependencies.kotlinStdLib
import AppDependencies.lifecycle
import AppDependencies.material
import AppDependencies.junit4
import AppDependencies.timberAndroid
import modules.WidgetsModuleConfig

plugins {
    id(Plugins.APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
}

android {
    val moduleConfig = WidgetsModuleConfig
    namespace = moduleConfig.namespace
    compileSdk = moduleConfig.compileSdk

    defaultConfig {
        applicationId = moduleConfig.namespace
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
            isDebuggable = true
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
    implementation(project(mapOf("path" to ":common")))

    kotlinStdLib()
    activityKtx()
    fragment()
    lifecycle()
    coroutines()
    timberAndroid()
    material()
    androidAnnotation()
    jvmAnnotation()

    //testing
    junit4()
    androidTest()
}
