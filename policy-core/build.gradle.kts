plugins {
    `java-library`
    alias(libs.plugins.maven.publish)
    signing
}

mavenPublishing {
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