plugins {
    application
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "net.resports"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("net.resports.eVent.MainKt")
}

repositories {
    mavenCentral()
}

val ktorVersion = "1.6.0"
val logbackVersion = "1.2.3"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}
