plugins {
    `java-library`
    alias(libs.plugins.maven.publish)
}

mavenPublishing {
    publishToMavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":policy-core"))
    implementation(project(":policy-processor"))
    implementation(libs.symbol.processing.api)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation("dev.zacsweers.kctfork:core:0.13.0")
    testImplementation("dev.zacsweers.kctfork:ksp:0.13.0")
}