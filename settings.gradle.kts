pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://repo1.maven.org/maven2")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        jcenter()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "CATMan"
include(":app")
include(":demo")
include(":widgets")
include(":common")
include(":widget-samples")
include(":common-test")
