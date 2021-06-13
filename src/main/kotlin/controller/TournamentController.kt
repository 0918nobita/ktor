package net.resports.eVent.controller

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.get
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

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
        return Period(from = DateTime.parse(surrogate.from), to = DateTime.parse(surrogate.to))
    }
}

@Serializable(with = PeriodSerializer::class)
data class Period(val from: DateTime, val to: DateTime)

@Serializable
data class Tournament(
    val title: String,
    @SerialName("holding_period") val holdingPeriod: Period,
    @SerialName("application_period") val applicationPeriod: Period
)

object TournamentTable : Table("tournament") {
    val id = integer("id")
    val title = text("title")
    val holdingPeriodFrom = datetime("holding_period_from")
    val holdingPeriodTo = datetime("holding_period_to")
    val applicationPeriodFrom = datetime("application_period_from")
    val applicationPeriodTo = datetime("application_period_to")
}

fun Route.tournamentController() {
    route("/v1/tournament/{id}") {
        get {
            val tournamentId = call.parameters["id"]?.toIntOrNull()
            if (tournamentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid tournament id")
                return@get
            }
            var tournament: Tournament? = null
            transaction {
                val resultRow = TournamentTable.select { TournamentTable.id eq tournamentId }.limit(1).firstOrNull()
                if (resultRow == null) {
                    call.response.status(HttpStatusCode.NotFound)
                    return@transaction
                }
                tournament = Tournament(
                    title = resultRow[TournamentTable.title],
                    holdingPeriod = Period(
                        resultRow[TournamentTable.holdingPeriodFrom],
                        resultRow[TournamentTable.holdingPeriodTo]
                    ),
                    applicationPeriod = Period(
                        resultRow[TournamentTable.applicationPeriodFrom],
                        resultRow[TournamentTable.applicationPeriodTo]
                    )
                )
            }
            if (tournament != null) {
                call.respondText(Json.encodeToString(tournament), ContentType.Application.Json)
                return@get
            }
            call.respond(HttpStatusCode.NotFound, "Tournament not found")
        }
    }
}
