plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.lukasstancikas.zedge_photos_details.core.navigation"
    compileSdk = 37

    defaultConfig {
        minSdk = 28
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    // Exposed as api so feature modules get EntryProviderBuilder / NavKey transitively
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.serialization.json)
}
