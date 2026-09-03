import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.plugins.signing.SigningExtension

plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.maven.publish) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    pluginManager.withPlugin("signing") {
        extensions.configure<SigningExtension> {
            useGpgCmd()
        }
    }

    plugins.withId("com.vanniktech.maven.publish") {
        extensions.configure<MavenPublishBaseExtension> {
            pom {
                name.set("Android AI Policy")
                description.set(
                    "A lightweight policy engine for controlling AI-initiated actions in Android applications."
                )
                inceptionYear.set("2026")
                url.set("https://github.com/jcodeforge/android-ai-policy")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("jcodeforge")
                        name.set("Alexander Scholz")
                        url.set("https://github.com/jcodeforge/")
                    }
                }

                scm {
                    url.set("https://github.com/jcodeforge/android-ai-policy")
                    connection.set(
                        "scm:git:git://github.com/jcodeforge/android-ai-policy.git"
                    )
                    developerConnection.set(
                        "scm:git:ssh://git@github.com/jcodeforge/android-ai-policy.git"
                    )
                }
            }
        }
    }
}