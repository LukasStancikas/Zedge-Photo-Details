plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.lukasstancikas.zedge_photos_details.core.domain"
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
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
}
