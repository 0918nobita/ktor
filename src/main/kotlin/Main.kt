package net.resports.eVent

import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.resports.eVent.controller.tournamentController
import org.jetbrains.exposed.sql.Database

fun initDB() {
    val user = "root"
    val password = "password"
    val url = "jdbc:mysql://localhost:3306/e_vent?allowPublicKeyRetrieval=true&useSSL=false"
    val driver = "com.mysql.cj.jdbc.Driver"
    Database.connect(url, driver, user, password)
}

fun main() {
    initDB()
    embeddedServer(Netty, port = 1234) {
        install(CORS) {
            host("localhost:3000")
        }
        routing {
            tournamentController()
        }
    }.start(wait = true)
}
