import AppDependency.coroutines
import AppDependency.junit4
import AppDependency.jvmAnnotation
import AppDependency.kotlinStdLib
import modules.CommonModuleConfig

plugins {
    id(Plugin.JAVA_LIBRARY)
    id(Plugin.KOTLIN_JVM)
    id(Plugin.JAVA)
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
    jvmAnnotation()
    junit4()
}