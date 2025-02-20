plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.ksp)
    alias(libs.plugins.parcelize)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.gallery.photos.editpic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gallery.photos.editpic"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
        viewBinding = true
        buildConfig = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {

    implementation(project(":patternlockview"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //Glide
    implementation(libs.glide)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.volley)
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    annotationProcessor(libs.compiler)

    // ViewModel and LiveData
    implementation(libs.lifecycle.viewmodel.ktx.v260)
    implementation(libs.androidx.lifecycle.livedata.ktx.v260)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //ssp & sdp
    implementation(libs.ssp.android)
    implementation(libs.sdp.android)

    //Gson
    implementation(libs.gson)

    //shimmer
    implementation(libs.shimmer)

    //SharedPreference
    implementation(libs.androidx.security.crypto)

    //RoomDatabase
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.room.compiler)

    //Swipe to refresh
    implementation(libs.androidx.swiperefreshlayout)
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("com.karumi:dexter:6.2.3")
    implementation("com.airbnb.android:lottie:6.1.0")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation(libs.relativepopupwindow)

    //Editing Modul
    implementation("com.burhanrashid52:photoeditor:3.0.2")

//     AD
    implementation(libs.play.services.ads)
}