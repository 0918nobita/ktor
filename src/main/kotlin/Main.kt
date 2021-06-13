package net.resports

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

@Serializable
@SerialName("Period")
data class PeriodSurrogate(val from: String, val to: String)

object PeriodSerializer : KSerializer<Period> {
    override val descriptor = Period.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Period) {
        val surrogate = PeriodSurrogate(from = value.from.toString(), to = value.to.toString())
        encoder.encodeSerializableValue(PeriodSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Period {
        val surrogate = decoder.decodeSerializableValue(PeriodSurrogate.serializer())
        return Period(from = ZonedDateTime.parse(surrogate.from), to = ZonedDateTime.parse(surrogate.to))
    }
}

@Serializable(with = PeriodSerializer::class)
data class Period(val from: ZonedDateTime, val to: ZonedDateTime)

@Serializable
data class Tournament(
    val title: String,
    @SerialName("holding_period") val holdingPeriod: Period,
    @SerialName("application_period") val applicationPeriod: Period
)

fun main() {
    val tournament = Tournament(
        title = "APEX CRカップ",
        holdingPeriod = Period(
            ZonedDateTime.parse("2021-06-12T19:00:00+09:00"),
            ZonedDateTime.parse("2021-06-12T22:00:00+09:00")
        ),
        applicationPeriod = Period(
            ZonedDateTime.parse("2021-06-01T15:00:00+09:00"),
            ZonedDateTime.parse("2021-06-10T23:59:00+09:00")
        )
    )
    embeddedServer(Netty, port = 1234) {
        install(CORS) {
            host("localhost:3000")
        }
        routing {
            get("/v1/tournament/{id}") {
                call.respondText(Json.encodeToString(tournament), ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}
