package net.resports.eVent

import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.resports.eVent.controller.tournamentController

fun main() {
    embeddedServer(Netty, port = 1234) {
        install(CORS) {
            host("localhost:3000")
        }
        routing {
            tournamentController()
        }
    }.start(wait = true)
}
