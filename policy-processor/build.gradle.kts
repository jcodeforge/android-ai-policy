plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":policy-core"))

    testImplementation(libs.junit)
    testImplementation("com.google.testing.compile:compile-testing:0.23.0")
}