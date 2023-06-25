import AppDependencies.coroutines
import AppDependencies.kotlinStdLib
import AppDependencies.moduleImplementation
import modules.DomainRepositoryApiModuleConfig
import modules.Modules

plugins {
    id(Plugins.JAVA_LIBRARY)
    id(Plugins.KOTLIN_JVM)
    id(Plugins.JAVA)
}

val moduleConfig = DomainRepositoryApiModuleConfig
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

    kotlinStdLib()
    coroutines()
}
