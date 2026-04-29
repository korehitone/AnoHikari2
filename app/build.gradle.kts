import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.room")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.syntxr.anohikari3"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.syntxr.anohikari3"
        minSdk = 24
        targetSdk = 35
        versionCode = 6
        versionName = "1.4"

        androidResources {
            localeFilters += listOf("in", "en")
        }


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val keyStoreFile = project.rootProject.file("local.properties")
        val properties = Properties()
        properties.load(keyStoreFile.inputStream())

        buildConfigField("String", "AUDIO_URL", "\"${properties.getProperty("AUDIO_URL")}\"")
        buildConfigField("String", "ADZAN_URL", "\"${properties.getProperty("ADZAN_URL")}\"")
        buildConfigField("String", "QIBLA_URL", "\"${properties.getProperty("QIBLA_URL")}\"")
        buildConfigField("String", "QIBLA_URL_ID", "\"${properties.getProperty("QIBLA_URL_ID")}\"")
        buildConfigField("String", "QIBLA_URL_EN", "\"${properties.getProperty("QIBLA_URL_EN")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation(platform("androidx.compose:compose-bom:2026.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    implementation("androidx.appcompat:appcompat:1.7.1")
//    draggable
    implementation("androidx.compose.foundation:foundation:1.11.0")
//
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")
    implementation("androidx.compose.material:material-icons-core-android:1.7.8")
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.11.0")

    //    kotlin ext and coroutine support for room
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    //    Destination
    implementation("io.github.raamcosta.compose-destinations:core:2.3.0")
    ksp("io.github.raamcosta.compose-destinations:ksp:2.3.0")

//    dagger - hilt
    implementation("com.google.dagger:hilt-android:2.59.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")
    ksp("androidx.hilt:hilt-compiler:1.3.0")

//    snow player
    implementation("com.github.jrfeng.snow:player:1.2.13")

//    kotpref
    implementation("com.chibatching.kotpref:kotpref:2.13.2")
    implementation("com.chibatching.kotpref:initializer:2.13.2")
    implementation("com.chibatching.kotpref:enum-support:2.13.2")

//    retofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")

    //Location
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

//    agent web
    implementation("com.github.Justson.AgentWeb:agentweb-core:v4.1.9-androidx")

//    serialization

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

}