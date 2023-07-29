import AppDependencies.moduleImplementation
import modules.DataRepositoryModuleConfig
import modules.Modules

plugins {
    alias(libs.plugins.android.lib)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
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
    moduleImplementation(Modules.DOMAIN_REPOSITORY)
    moduleImplementation(Modules.DOMAIN_MODEL)
    moduleImplementation(Modules.DATA_SOURCE_LOCAL)
    moduleImplementation(Modules.COMMON)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)
    kapt(libs.hilt.ext.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)

    testImplementation(libs.junit)
}
