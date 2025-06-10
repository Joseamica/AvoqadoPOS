plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.avoqado.pos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.avoqado.pos"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

//        ndk
//            abiFilters.add("armeabi-v7a")
//        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
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

    val roomVersion: String by project
    val composeVersion: String by project
    val kotlinCompilerExtension: String by project
    val googleMaterialVersion: String by project
    val composeRuntimeVersion: String by project
    val retrofitVersion: String by project
    val okhttpBomVersion: String by project
    val activityComposeVersion: String by project
    val accompanistPlaceholderVersion: String by project
    val navVersion: String by project
    val lifecycleVersion: String by project
    val camerax_version = "1.3.1" // O la versión estable más reciente

    implementation(libs.androidx.material)
    implementation(libs.androidx.material.navigation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)

    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.runner)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation(files("libs/content_core_service_release.aar"))

// CameraX
implementation ("androidx.camera:camera-core:$camerax_version")
implementation ("androidx.camera:camera-camera2:$camerax_version")
implementation ("androidx.camera:camera-lifecycle:$camerax_version")
implementation ("androidx.camera:camera-view:$camerax_version")


// ML Kit Barcode Scanning
implementation ("com.google.mlkit:barcode-scanning:17.2.0")

// Accompanist Permissions (si no la tienes ya)
implementation ("com.google.accompanist:accompanist-permissions:0.32.0") // O la versión estable más reciente
    // Compose and UI
    implementation("com.google.android.material:material:$googleMaterialVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:1.7.7")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.compose.runtime:runtime:$composeRuntimeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeRuntimeVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistPlaceholderVersion")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // okhttpBomVersion
    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttpBomVersion"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.69")
    implementation("commons-codec:commons-codec:1.16.1")

    // room database
    implementation("androidx.room:room-ktx:$roomVersion")

    implementation("androidx.compose.runtime:runtime-livedata:1.7.6")
    implementation("io.socket:socket.io-client:2.1.0") // Use latest version

    implementation("com.lightspark:compose-qr-code:1.0.1")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")

    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    implementation("com.google.protobuf:protobuf-kotlin:4.27.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("io.ktor:ktor-utils:2.3.0")

}
