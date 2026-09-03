plugins {
    `java-library`
    alias(libs.plugins.maven.publish)
    signing
}

mavenPublishing {
    coordinates(
        groupId = "io.github.jcodeforge",
        artifactId = "policy-core",
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
    testImplementation(libs.junit)
}