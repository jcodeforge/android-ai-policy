plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":policy-core"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.3.11")

    testImplementation(libs.junit)
}