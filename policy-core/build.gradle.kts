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
    testImplementation(libs.junit)
}