plugins {
    `java-library`
    alias(libs.plugins.maven.publish)
    signing
}

mavenPublishing {
    coordinates(
        groupId = "io.github.jcodeforge",
        artifactId = "policy-processor",
        version = "1.0.0"
    )

    publishToMavenCentral()
    signAllPublications()
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