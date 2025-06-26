plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "com.github.laomou"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("com.github.laomou:thumbnailer:04f79f3ff1")
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