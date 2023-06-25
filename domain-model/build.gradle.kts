import AppDependencies.androidAnnotation
import AppDependencies.coroutines
import AppDependencies.kotlinStdLib
import AppDependencies.moduleImplementation
import modules.DomainModelModuleConfig
import modules.Modules

plugins {
    id(Plugins.JAVA_LIBRARY)
    id(Plugins.KOTLIN_JVM)
    id(Plugins.JAVA)
}

val moduleConfig = DomainModelModuleConfig
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
    moduleImplementation(Modules.COMMON)

    androidAnnotation()
}
