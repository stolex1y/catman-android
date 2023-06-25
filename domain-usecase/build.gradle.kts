import AppDependencies.coroutines
import AppDependencies.dagger
import AppDependencies.junit4
import AppDependencies.jvmAnnotation
import AppDependencies.kotlinStdLib
import AppDependencies.moduleImplementation
import modules.DomainModuleConfig
import modules.Modules

plugins {
    id(Plugins.JAVA_LIBRARY)
    id(Plugins.KOTLIN_JVM)
    id(Plugins.JAVA)
    id(Plugins.KOTLIN_KAPT)
}

val moduleConfig = DomainModuleConfig
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

tasks.named<Test>("test") {
    useJUnit()
}

dependencies {
    moduleImplementation(Modules.COMMON)
    moduleImplementation(Modules.DOMAIN_REPOSITORY_API)
    moduleImplementation(Modules.DOMAIN_MODEL)

    kotlinStdLib()
    dagger()
    coroutines()
    jvmAnnotation()
    junit4()
}
