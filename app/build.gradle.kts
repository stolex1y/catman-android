plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "ru.stolexiy.catman"
    compileSdk = 33

    defaultConfig {
        applicationId = "ru.stolexiy.catman"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    val navVersion = "2.5.3"
    val roomVersion = "2.5.0"
    val lifecycleVersion = "2.6.0"
    val workVersion = "2.8.0"
    val daggerVersion = "2.44"

    // dagger
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    // firebase
    platform("com.google.firebase:firebase-bom:31.5.0")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // androidx room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    //lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    //androidx
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.7.0")
    implementation("androidx.fragment:fragment-ktx:1.5.6")
    testImplementation("androidx.fragment:fragment-testing:1.5.6")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //material
    implementation("com.google.android.material:material:1.8.0")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    implementation("com.jakewharton.timber:timber:5.0.1")

    //navigation
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    api("androidx.navigation:navigation-fragment-ktx:$navVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")

    //workManager
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    androidTestImplementation("androidx.work:work-testing:$workVersion")

    testImplementation("junit:junit:4.13.2")

    implementation("com.google.code.gson:gson:2.10.1")

}