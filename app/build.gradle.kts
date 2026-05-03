plugins {
    alias(libs.plugins.android.application)
}

val localProps = java.util.Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) {
        f.inputStream().use { load(it) }
    }
}

android {
    namespace = "com.example.crudapi"

    buildFeatures {
        buildConfig = true
    }
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.crudapi"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Kredensial upload bertanda Cloudinary dari local.properties (jangan dibagikan lewat APK ke publik)
        fun q(s: String?) = "\"" + (s ?: "").replace("\\", "\\\\").replace("\"", "\\\"") + "\""

        buildConfigField("String", "CLOUDINARY_API_KEY", q(localProps.getProperty("CLOUDINARY_API_KEY")))
        buildConfigField("String", "CLOUDINARY_API_SECRET", q(localProps.getProperty("CLOUDINARY_API_SECRET")))
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}