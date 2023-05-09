import AppDependency.activityKtx
import AppDependency.androidAnnotation
import AppDependency.androidTest
import AppDependency.coroutines
import AppDependency.fragment
import AppDependency.jvmAnnotation
import AppDependency.kotlinStdLib
import AppDependency.lifecycle
import AppDependency.material
import AppDependency.junit4
import AppDependency.timberAndroid
import modules.WidgetsModuleConfig

plugins {
    id(Plugin.APPLICATION)
    id(Plugin.KOTLIN_ANDROID)
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
