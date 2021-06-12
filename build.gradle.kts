plugins {
    application
    kotlin("jvm") version "1.5.0"
}

group = "net.resports"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("net.resports.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core:1.6.0")
    implementation("io.ktor:ktor-server-netty:1.6.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}
