package com.cafeteria.routes

import com.cafeteria.models.Usuario
import com.cafeteria.models.UsuarioEndereco
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

fun Route.usuarioEnderecoRouting(database: MongoDatabase) {
    route("/usuarioEndereco") {
        val collection = database.getCollection<UsuarioEndereco>("usuarios")

        authenticate {
            post("{idUsuario?}") {
                try {
                    val body = call.receive<UsuarioEndereco>()

                    val param =
                        call.parameters["idUsuario"] ?: return@post call.respondText(
                            "Faltando id",
                            status = HttpStatusCode.BadRequest
                        )

                    val encodeEndereco = Json.encodeToString(
                        UsuarioEndereco(
                            id = body.id ?: UUID.randomUUID().toString(),
                            rua = body.rua,
                            cidade = body.cidade,
                            estado = body.estado,
                            cep = body.cep,
                            numero = body.numero,
                            complemento = body.complemento
                        )
                    )

                    val filter = eq(Usuario::idUsuario.name, param)
                    val update = Updates.push(
                        Usuario::enderecos.name,
                        Json.decodeFromString<UsuarioEndereco>(encodeEndereco)
                    )

                    collection.findOneAndUpdate(
                        filter, update
                    )

                    call.respondText(
                        "Endereço adicionado", status = HttpStatusCode
                            .Created
                    )

                } catch (e: Exception) {
                    call.respondText("${e.message}")
                }
            }
        }
//
        authenticate {
            delete("{idUsuario?}") {
                try {
                    val param =
                        call.parameters["idUsuario"] ?: return@delete call.respondText(
                            "Faltando id",
                            status = HttpStatusCode.BadRequest
                        )

                    val body = call.receive<Map<String, String>>()

                    val filter = eq(Usuario::idUsuario.name, param)
                    val update = Updates.pull(Usuario::enderecos.name, body)

                    collection.updateOne(filter, update)

                    call.respondText("Endereço removido", status = HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respondText("${e.message}")
                }
            }
        }
    }
}