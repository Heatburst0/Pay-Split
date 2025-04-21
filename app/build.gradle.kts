plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.paysplit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.paysplit"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }
}

dependencies {
    implementation("com.firebaseui:firebase-ui-database:8.0.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.18.0")
    implementation("com.google.http-client:google-http-client-gson:1.42.3")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation ("dev.shreyaspatil.EasyUpiPayment:EasyUpiPayment:3.0.3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.compose.material3:material3:1.1.2")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}