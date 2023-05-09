import AppDependency.activityKtx
import AppDependency.androidAnnotation
import AppDependency.androidConstraintLayout
import AppDependency.androidCoreKtx
import AppDependency.androidTest
import AppDependency.appcompat
import AppDependency.coroutines
import AppDependency.dagger
import AppDependency.fragment
import AppDependency.jvmAnnotation
import AppDependency.kotlinStdLib
import AppDependency.lifecycle
import AppDependency.material
import AppDependency.room
import AppDependency.junit4
import AppDependency.timberAndroid
import modules.DemoModuleConfig

plugins {
    id(Plugin.APPLICATION)
    id(Plugin.KOTLIN_KAPT)
    id(Plugin.KSP)
    id(Plugin.GMS)
    id(Plugin.NAV_SAFEARGS)
    id(Plugin.KOTLIN_ANDROID)
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
