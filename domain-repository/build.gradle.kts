import AppDependencies.moduleImplementation
import modules.DomainRepositoryModuleConfig
import modules.Modules

plugins {
    alias(libs.plugins.kotlin.jvm)
}

val moduleConfig = DomainRepositoryModuleConfig
group = moduleConfig.namespace
version = moduleConfig.versionCode

java {
    toolchain {
        sourceCompatibility = moduleConfig.sourceJdk
        targetCompatibility = moduleConfig.targetJdk
    }
}

kotlin {
    jvmToolchain(moduleConfig.targetJdk.majorVersion.toInt())
}

dependencies {
    moduleImplementation(Modules.DOMAIN_MODEL)
    moduleImplementation(Modules.COMMON)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.dagger)
    testImplementation(libs.junit)
}
