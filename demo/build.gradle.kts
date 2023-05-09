import AppDependencies.activityKtx
import AppDependencies.androidAnnotation
import AppDependencies.androidConstraintLayout
import AppDependencies.androidCoreKtx
import AppDependencies.androidTest
import AppDependencies.appcompat
import AppDependencies.coroutines
import AppDependencies.dagger
import AppDependencies.fragment
import AppDependencies.jvmAnnotation
import AppDependencies.kotlinStdLib
import AppDependencies.lifecycle
import AppDependencies.material
import AppDependencies.room
import AppDependencies.junit4
import AppDependencies.timberAndroid
import modules.DemoModuleConfig

plugins {
    id(Plugins.APPLICATION)
    id(Plugins.KOTLIN_KAPT)
    id(Plugins.KSP)
    id(Plugins.GMS)
    id(Plugins.NAV_SAFEARGS)
    id(Plugins.KOTLIN_ANDROID)
}

android {
    val moduleConfig = DemoModuleConfig
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
    implementation(project(mapOf("path" to ":common")))
    implementation(project(mapOf("path" to ":widgets")))

    kotlinStdLib()
    dagger()
    androidCoreKtx()
    appcompat()
    androidConstraintLayout()
    lifecycle()
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

    val navVersion = "2.5.3"
    val workVersion = "2.8.1"

    //navigation
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    api("androidx.navigation:navigation-fragment-ktx:$navVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")

    //workManager
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    androidTestImplementation("androidx.work:work-testing:$workVersion")

    implementation("com.google.code.gson:gson:2.10.1")

}
