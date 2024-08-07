import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.room")
}

room {
    schemaDirectory("$projectDir/schemas")
}


android {
    namespace = "com.syntxr.anohikari3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.syntxr.anohikari3"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = "1.3"

        resourceConfigurations += listOf("in", "en")


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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
//    draggable
    implementation("androidx.compose.foundation:foundation:1.6.1")
//
    implementation("androidx.compose.material:material-icons-extended-android:1.6.1")
    implementation("androidx.compose.material:material-icons-core-android:1.6.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.1")

    //    kotlin ext and coroutine support for room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    //    Destination
    implementation("io.github.raamcosta.compose-destinations:animations-core:1.9.54")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.9.54")

//    dagger - hilt
    implementation("com.google.dagger:hilt-android:2.49")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    ksp("com.google.dagger:hilt-android-compiler:2.49")
    ksp("androidx.hilt:hilt-compiler:1.1.0")

//    snow player
    implementation("com.github.jrfeng.snow:player:1.2.13")

//    kotpref
    implementation("com.chibatching.kotpref:kotpref:2.13.2")
    implementation("com.chibatching.kotpref:initializer:2.13.2")
    implementation("com.chibatching.kotpref:enum-support:2.13.2")

//    retofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.5")

    //Location
    implementation ("com.google.android.gms:play-services-location:21.1.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.23.1")

//    agent web
    implementation ("com.github.Justson.AgentWeb:agentweb-core:v4.1.9-androidx")

}