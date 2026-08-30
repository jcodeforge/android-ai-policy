plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.github.jcodeforge.aipolicy.android"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":policy-core"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.process)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}