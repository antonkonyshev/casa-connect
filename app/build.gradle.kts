plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.github.antonkonyshev.casaconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.antonkonyshev.casaconnect"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    val lifecycleVersion = "2.7.0"
    val materialVersion = "1.6.7"
    val composeVersion = "2024.05.00"
    val navVersion = "2.7.7"
    val roomVersion = "2.6.1"
    val daggerVersion = "2.51"
    val okhttpVersion = "4.12.0"
    val accompanistVersion = "0.30.1"
    val ktCoilVersion = "2.6.0"
    val preferenceVersion = "1.2.1"

    implementation("androidx.core:core-ktx:1.13.1")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.22"))
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:$composeVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-android:$materialVersion")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("androidx.compose.material:material-icons-core-android:$materialVersion")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.material:material-icons-extended-android:$materialVersion")
    implementation("androidx.window:window:1.2.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("io.coil-kt:coil-compose:$ktCoilVersion")
    implementation("io.coil-kt:coil-svg:$ktCoilVersion")
    implementation("commons-net:commons-net:3.9.0")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
    implementation("com.google.dagger:dagger:$daggerVersion")
    implementation("com.google.dagger:dagger-android:$daggerVersion")
    implementation("com.google.dagger:dagger-android-support:$daggerVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("com.github.philjay:mpandroidchart:v3.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    ksp("com.google.dagger:dagger-compiler:$daggerVersion")
    ksp("com.google.dagger:dagger-android-processor:$daggerVersion")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okhttpVersion")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
