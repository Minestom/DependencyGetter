plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.5.31"

    java
}
group = "net.minestom"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib", "1.5.0"))

    // Add shrinkwrap resolver
    implementation("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-depchain:3.1.4")

    // Use the kotlin test library
    testImplementation("io.kotest:kotest-assertions-core:4.6.3")
    testImplementation("io.kotest:kotest-runner-junit5:4.6.3")
}

tasks {
    withType<Test> { useJUnitPlatform() }
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = JavaVersion.VERSION_16.toString()