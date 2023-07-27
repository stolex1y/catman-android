import modules.CommonModuleConfig

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

val moduleConfig = CommonModuleConfig
group = moduleConfig.namespace
version = moduleConfig.versionCode

kotlin {
    jvmToolchain(moduleConfig.targetJdk.majorVersion.toInt())
}

tasks.named<Test>("test") {
    useJUnit()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.dagger)
    implementation(libs.gson)
    implementation(libs.junit)
}
