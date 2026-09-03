plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.maven.publish)
}

mavenPublishing {
    publishToMavenCentral()
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
    api(project(":policy-core"))
    androidTestAnnotationProcessor(project(":policy-processor"))

    implementation("androidx.appfunctions:appfunctions:1.0.0-alpha10")
    ksp("androidx.appfunctions:appfunctions-compiler:1.0.0-alpha10")

    kspAndroidTest(project(":policy-processor-ksp"))

    add(
        "kspAndroidTestDebug",
        "androidx.appfunctions:appfunctions-compiler:1.0.0-alpha10"
    )

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.process)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}