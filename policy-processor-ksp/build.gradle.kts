plugins {
    `java-library`
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
}