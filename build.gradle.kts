plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "com.laomou"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.laomou.FastShowAppKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}