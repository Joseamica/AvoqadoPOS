plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
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
                "proguard-rules.pro"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Menta dependencies
    implementation(files("libs/urovosdkLibs_v78.aar"))
    implementation(files("libs/com_menta_android_common_cross_common-cross_2.8.5_common-cross-2.8.5.aar"))
    implementation(files("libs/com_menta_android_core_core_payment_3.5.5_core_payment-3.5.5.aar"))
    implementation(files("libs/com_menta_android_emv_i9100_reader_reader_1.6.4_reader-1.6.4.aar"))
    implementation(files("libs/com_menta_android_keys_admin_core_core_1.5.2_core-1.5.2.aar"))
    implementation(files("libs/com_menta_android_printer_i9100_printer_2.0.0_printer-2.0.0.aar"))
    implementation(files("libs/com_menta_android_restclient_core_2.2.8_core-2.2.8.aar"))

    // Compose and UI
    implementation("com.google.android.material:material:$googleMaterialVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:1.6.5")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.compose.runtime:runtime:$composeRuntimeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeRuntimeVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistPlaceholderVersion")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //okhttpBomVersion
    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttpBomVersion"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    //security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.69")

    //room database
    implementation("androidx.room:room-ktx:$roomVersion")

    implementation ("androidx.compose.runtime:runtime-livedata:1.7.6")

}