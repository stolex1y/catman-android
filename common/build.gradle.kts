import AppDependencies.coroutines
import AppDependencies.dagger
import AppDependencies.gson
import AppDependencies.junit4
import AppDependencies.jvmAnnotation
import AppDependencies.kotlinStdLib
import modules.CommonModuleConfig

plugins {
    id(Plugins.JAVA_LIBRARY)
    id(Plugins.KOTLIN_JVM)
    id(Plugins.JAVA)
    id(Plugins.KOTLIN_KAPT)
}

val moduleConfig = CommonModuleConfig
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
    kotlinStdLib()
    coroutines()
    dagger()
    jvmAnnotation()
    gson()
    junit4()
}
