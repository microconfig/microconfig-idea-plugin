import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    idea
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij.platform)
}

group = "io.microconfig"
version = "2.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        apiVersion = KotlinVersion.KOTLIN_1_9
    }
}

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "241"
            untilBuild = provider { null }
        }
        changeNotes = """
          &#183; Update microconfig core to ${libs.versions.microconfig.get()}<br>
          &#183; Migrate plugin implementation to Kotlin<br>
          &#183; Requires IntelliJ 2024.1+ (Java 17)<br>
          &#183; Spread the word about microconfig if you enjoy using it :)<br>
        """.trimIndent()
    }
    pluginVerification {
        ides { recommended() }
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1")
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.JUnit5)
    }

    implementation(platform(libs.spring.boot.bom))
    implementation(libs.microconfig) { isTransitive = false }

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.mockito)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit4)
}

tasks.processResources {
    inputs.property("version", version)
    filesMatching("**/greeter/version.txt") {
        expand("version" to version)
    }
}

tasks.test {
    useJUnitPlatform()
    exclude("**/*IT.class")
}
