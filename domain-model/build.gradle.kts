import AppDependencies.moduleImplementation
import modules.DomainModelModuleConfig
import modules.Modules

plugins {
    alias(libs.plugins.kotlin.jvm)
}

val moduleConfig = DomainModelModuleConfig
group = moduleConfig.namespace
version = moduleConfig.versionCode

kotlin {
    jvmToolchain(moduleConfig.targetJdk.majorVersion.toInt())
}

dependencies {
    moduleImplementation(Modules.COMMON)

    implementation(libs.androidx.annotation)
}
