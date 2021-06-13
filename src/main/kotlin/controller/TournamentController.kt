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
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
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

object TournamentTable : IntIdTable("tournament") {
    val title = text("title")
    val holdingPeriodFrom = datetime("holding_period_from")
    val holdingPeriodTo = datetime("holding_period_to")
    val applicationPeriodFrom = datetime("application_period_from")
    val applicationPeriodTo = datetime("application_period_to")
}

class TournamentModel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TournamentModel>(TournamentTable)

    var title by TournamentTable.title
    var holdingPeriodFrom by TournamentTable.holdingPeriodFrom
    var holdingPeriodTo by TournamentTable.holdingPeriodTo
    var applicationPeriodFrom by TournamentTable.applicationPeriodFrom
    var applicationPeriodTo by TournamentTable.applicationPeriodTo
}

fun TournamentModel.toEntity() = Tournament(
    title = title,
    holdingPeriod = Period(holdingPeriodFrom, holdingPeriodTo),
    applicationPeriod = Period(applicationPeriodFrom, applicationPeriodTo)
)

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
                val tournamentModel = TournamentModel.findById(tournamentId)
                if (tournamentModel != null) {
                    tournament = tournamentModel.toEntity()
                }
            }
            if (tournament != null) {
                call.respondText(Json.encodeToString(tournament), ContentType.Application.Json)
                return@get
            }
            call.respond(HttpStatusCode.NotFound, "Tournament not found")
        }
    }
}
