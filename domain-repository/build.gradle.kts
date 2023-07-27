import AppDependencies.coroutines
import AppDependencies.dagger
import AppDependencies.junit4
import AppDependencies.jvmAnnotation
import AppDependencies.kotlinStdLib
import AppDependencies.moduleImplementation
import modules.DomainRepositoryModuleConfig
import modules.Modules

plugins {
    id(Plugins.JAVA_LIBRARY)
    id(Plugins.KOTLIN_JVM)
    id(Plugins.JAVA)
    id(Plugins.KOTLIN_KAPT)
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

    dagger()
    jvmAnnotation()
    junit4()
    kotlinStdLib()
    coroutines()
}
