import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseKeystorePropertiesFile = rootProject.file("release-keystore.properties")
val releaseKeystoreProperties = Properties().apply {
    if (releaseKeystorePropertiesFile.exists()) {
        releaseKeystorePropertiesFile.inputStream().use { load(it) }
    }
}
val hasReleaseKeystore = releaseKeystorePropertiesFile.exists()
// A public clone has no signing key. In that case Gradle uses its normal
// machine-local debug key for debug builds and leaves the release unsigned.
// A maintainer can provide keystore.properties for the local debug identity and
// release-keystore.properties for the public release identity. Neither file is committed.
val localKeystorePropertiesFile = rootProject.file("keystore.properties")
val localKeystoreProperties = Properties().apply {
    if (localKeystorePropertiesFile.exists()) {
        localKeystorePropertiesFile.inputStream().use { load(it) }
    }
}
val hasLocalKeystore = localKeystorePropertiesFile.exists()

android {
    namespace = "dev.legendtuner.app"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "dev.legendtuner.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 17
        versionName = "1.4.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        if (hasLocalKeystore) {
            create("local") {
                storeFile = rootProject.file(
                    localKeystoreProperties.getProperty("storeFile", "keystore/debug.jks")
                )
                storePassword = localKeystoreProperties.getProperty("storePassword")
                keyAlias = localKeystoreProperties.getProperty("keyAlias")
                keyPassword = localKeystoreProperties.getProperty("keyPassword")
            }
        }
        if (hasReleaseKeystore) {
            create("releaseKey") {
                storeFile = rootProject.file(
                    releaseKeystoreProperties.getProperty("storeFile")
                )
                storePassword = releaseKeystoreProperties.getProperty("storePassword")
                keyAlias = releaseKeystoreProperties.getProperty("keyAlias")
                keyPassword = releaseKeystoreProperties.getProperty("keyPassword")
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            if (hasLocalKeystore) {
                signingConfig = signingConfigs.getByName("local")
            }
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasReleaseKeystore) {
                signingConfig = signingConfigs.getByName("releaseKey")
            }
        }
    }

    sourceSets {
        getByName("test").resources.srcDir(rootProject.file("testdata"))
    }

    buildFeatures {
        compose = true
        buildConfig = true
        aidl = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    debugImplementation("androidx.compose.ui:ui-tooling")

    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
