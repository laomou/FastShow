plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "com.laomou"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("net.coobird:thumbnailator:0.4.20")
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