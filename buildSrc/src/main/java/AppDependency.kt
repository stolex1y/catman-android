import org.gradle.api.artifacts.dsl.DependencyHandler

object AppDependency {
    //std lib
    val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${DependencyVersion.kotlin}"

    fun DependencyHandler.kotlinStdLib() {
        add(ConfigurationName.IMPLEMENTATION.configName, kotlinStdLib)
    }

    //android core
    val coreKtx = "androidx.core:core-ktx:${DependencyVersion.coreKtx}"

    fun DependencyHandler.androidCoreKtx() {
        add(ConfigurationName.IMPLEMENTATION.configName, coreKtx)
    }

    //android ui
    val appcompat = "androidx.appcompat:appcompat:${DependencyVersion.appCompat}"

    fun DependencyHandler.appcompat() {
        add(ConfigurationName.IMPLEMENTATION.configName, appcompat)
    }

    val activityKtx = "androidx.activity:activity-ktx:${DependencyVersion.activityKtx}"

    fun DependencyHandler.activityKtx() {
        add(ConfigurationName.IMPLEMENTATION.configName, activityKtx)
    }

    val fragmentKtx = "androidx.fragment:fragment-ktx:${DependencyVersion.fragmentKtx}"
    val fragmentTesting = "androidx.fragment:fragment-testing:${DependencyVersion.fragmentTesting}"

    fun DependencyHandler.fragment() {
        add(ConfigurationName.IMPLEMENTATION.configName, fragmentKtx)
        add(ConfigurationName.TEST_IMPLEMENTATION.configName, fragmentTesting)
    }

    val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${DependencyVersion.constraintLayout}"

    fun DependencyHandler.androidConstraintLayout() {
        add(ConfigurationName.IMPLEMENTATION.configName, constraintLayout)
    }

    //android lifecycle
    val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${DependencyVersion.lifecycle}"
    val lifecycleLivedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${DependencyVersion.lifecycle}"
    val lifecycleViewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${DependencyVersion.lifecycle}"

    fun DependencyHandler.lifecycle() {
        add(ConfigurationName.IMPLEMENTATION.configName, lifecycleRuntimeKtx)
        add(ConfigurationName.IMPLEMENTATION.configName, lifecycleLivedataKtx)
        add(ConfigurationName.IMPLEMENTATION.configName, lifecycleViewmodelKtx)
    }

    //dagger
    val dagger = "com.google.dagger:dagger:${DependencyVersion.dagger}"
    val daggerCompiler = "com.google.dagger:dagger-compiler:${DependencyVersion.dagger}"

    fun DependencyHandler.dagger() {
        add(ConfigurationName.KAPT.configName, daggerCompiler)
        add(ConfigurationName.IMPLEMENTATION.configName, dagger)
    }

    //room
    val roomRuntime = "androidx.room:room-runtime:${DependencyVersion.room}"
    val roomKtx = "androidx.room:room-ktx:${DependencyVersion.room}"
    val roomCompiler = "androidx.room:room-compiler:${DependencyVersion.room}"
    val roomTesting = "androidx.room:room-testing:${DependencyVersion.room}"

    fun DependencyHandler.room() {
        add(ConfigurationName.IMPLEMENTATION.configName, roomRuntime)
        add(ConfigurationName.IMPLEMENTATION.configName, roomKtx)
        add(ConfigurationName.ANNOTATION_PROCESSOR.configName, roomCompiler)
        add(ConfigurationName.KSP.configName, roomCompiler)
        add(ConfigurationName.TEST_IMPLEMENTATION.configName, roomTesting)
    }

    //firebase
    val firebaseBom = "com.google.firebase:firebase-bom:${DependencyVersion.firebaseBom}"
    val firebaseAnalyticsKtx =
        "com.google.firebase:firebase-analytics-ktx:${DependencyVersion.firebaseAnalyticsKtx}"

    fun DependencyHandler.firebase() {
        platform(firebaseBom)
        add(ConfigurationName.IMPLEMENTATION.configName, firebaseAnalyticsKtx)
    }

    //test libs
    private val junit = "junit:junit:${DependencyVersion.junit}"
    private val testRunner = "androidx.test:runner:${DependencyVersion.testRunner}"
    private val testRules = "androidx.test:rules:${DependencyVersion.testRules}"
    private val extJUnit = "androidx.test.ext:junit:${DependencyVersion.extJunit}"
    private val extJUnitKtx = "androidx.test.ext:junit-ktx:${DependencyVersion.extJunit}"
    private val espressoCore = "androidx.test.espresso:espresso-core:${DependencyVersion.espresso}"

    fun DependencyHandler.androidTest() {
        add(ConfigurationName.ANDROID_TEST_IMPLEMENTATION.configName, extJUnit)
        add(ConfigurationName.ANDROID_TEST_IMPLEMENTATION.configName, extJUnitKtx)
        add(ConfigurationName.ANDROID_TEST_IMPLEMENTATION.configName, espressoCore)
        add(ConfigurationName.ANDROID_TEST_IMPLEMENTATION.configName, testRules)
        add(ConfigurationName.ANDROID_TEST_IMPLEMENTATION.configName, testRunner)
    }

    fun DependencyHandler.test() {
        add(ConfigurationName.TEST_IMPLEMENTATION.configName, junit)
    }

    enum class ConfigurationName(val configName: String) {
        KAPT("kapt"),
        IMPLEMENTATION("implementation"),
        ANDROID_TEST_IMPLEMENTATION("androidTestImplementation"),
        TEST_IMPLEMENTATION("testImplementation"),
        ANNOTATION_PROCESSOR("annotationProcessor"),
        KSP("ksp"),
    }

    //util functions for adding the different type dependencies from build.gradle file
    fun DependencyHandler.kapt(list: List<String>) {
        list.forEach { dependency ->
            add("kapt", dependency)
        }
    }

    fun DependencyHandler.implementation(list: List<String>) {
        list.forEach { dependency ->
            add("implementation", dependency)
        }
    }

    fun DependencyHandler.androidTestImplementation(list: List<String>) {
        list.forEach { dependency ->
            add("androidTestImplementation", dependency)
        }
    }

    fun DependencyHandler.testImplementation(list: List<String>) {
        list.forEach { dependency ->
            add("testImplementation", dependency)
        }
    }
}
