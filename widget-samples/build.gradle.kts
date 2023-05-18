
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
import AppDependencies.moduleImplementation
import AppDependencies.timberAndroid
import modules.WidgetSamplesModuleConfig

plugins {
    id(Plugins.APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
}

android {
    val moduleConfig = WidgetSamplesModuleConfig
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
//        dataBinding = true
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
    moduleImplementation("widgets")
    moduleImplementation("common")

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