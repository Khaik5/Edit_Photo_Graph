import org.gradle.kotlin.dsl.plugins

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.drawcanvas_v2"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.drawcanvas_v2"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }

    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets", "src\\main\\assets", "src\\main\\assets")
            }
        }
    }
}
val cameraxVersion = "1.4.2"
dependencies {

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.litertlm.jvm)
    implementation(libs.material)
    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0"
    )
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-video:${cameraxVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation(
        "com.vanniktech:android-image-cropper:4.6.0"
    )
    implementation(
        "androidx.datastore:datastore-preferences:1.1.1"
    )

    implementation(
        "com.airbnb.android:lottie:6.7.0"
    )

    implementation(
        "com.github.bumptech.glide:glide:5.0.5"
    )

    implementation(
        "com.squareup.retrofit2:retrofit:3.0.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:3.0.0"
    )

    implementation(
        "com.squareup.okhttp3:okhttp:4.12.0"
    )

    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )

    coreLibraryDesugaring(
        "com.android.tools:desugar_jdk_libs:2.0.4"
    )
    implementation("com.vanniktech:android-image-cropper:4.6.0")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    testImplementation(libs.junit)

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )
}
