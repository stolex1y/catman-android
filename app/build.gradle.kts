import AppDependencies.activityKtx
import AppDependencies.androidAnnotation
import AppDependencies.androidConstraintLayout
import AppDependencies.androidCoreKtx
import AppDependencies.androidTest
import AppDependencies.appcompat
import AppDependencies.coroutines
import AppDependencies.dagger
import AppDependencies.firebase
import AppDependencies.fragment
import AppDependencies.gson
import AppDependencies.hilt
import AppDependencies.hiltNavigation
import AppDependencies.hiltTest
import AppDependencies.hiltWorkManager
import AppDependencies.junit4
import AppDependencies.jvmAnnotation
import AppDependencies.kotlinStdLib
import AppDependencies.lifecycle
import AppDependencies.material
import AppDependencies.moduleAndroidTestImplementation
import AppDependencies.moduleImplementation
import AppDependencies.navigation
import AppDependencies.room
import AppDependencies.timberAndroid
import AppDependencies.workManager
import modules.AppModuleConfig

plugins {
    id(Plugins.APPLICATION)
    id(Plugins.KOTLIN_KAPT)
    id(Plugins.KSP)
    id(Plugins.GMS)
    id(Plugins.NAV_SAFEARGS)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.HILT)
}

android {
    val moduleConfig = AppModuleConfig
    namespace = moduleConfig.namespace
    compileSdk = moduleConfig.compileSdk

    defaultConfig {
        applicationId = moduleConfig.namespace
        minSdk = moduleConfig.minSdk
        targetSdk = moduleConfig.targetSdk
        versionCode = moduleConfig.versionCode
        versionName = moduleConfig.versionName

        testInstrumentationRunner = moduleConfig.testInstrumentationRunner
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = moduleConfig.targetJdk
        targetCompatibility = moduleConfig.targetJdk
    }

    kotlinOptions {
        jvmTarget = moduleConfig.targetJdk.majorVersion
    }
}

dependencies {
    moduleImplementation("widgets")
    moduleImplementation("common")
    moduleAndroidTestImplementation("common-test")

    kotlinStdLib()
    dagger()
    androidCoreKtx()
    appcompat()
    androidConstraintLayout()
    lifecycle()
    firebase()
    room()
    activityKtx()
    fragment()
    androidTest()
    junit4()
    material()
    coroutines()
    timberAndroid()
    androidAnnotation()
    jvmAnnotation()
    hilt()
    hiltNavigation()
    hiltWorkManager()
    hiltTest()
    navigation()
    workManager()
    gson()
}
