package com.cafeteria.routes

import com.cafeteria.models.UsuarioEndereco
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.util.*

fun Route.usuarioEnderecoRouting(database: MongoDatabase) {
    route("/usuarioEndereco") {
        val collection = database.getCollection<UsuarioEndereco>("usuariosEndereco")

        authenticate {
            get("{idusuario?}") {
                try {
                    val param = call.parameters["idusuario"] ?: return@get call.respondText(
                        "Faltando id",
                        status = HttpStatusCode.BadRequest
                    )

                    val endereco =
                        collection.find(eq(UsuarioEndereco::idUsuario.name, param)).toList()

                    call.respond(Json.encodeToJsonElement(endereco))
                } catch (e: Exception) {
                    call.respondText("${e.message}")
                }
            }
        }

        authenticate {
            post {
                try {
                    val body = Json.decodeFromString<UsuarioEndereco>(call.receive<String>())

                    val encodeBody = Json.encodeToString(
                        UsuarioEndereco(
                            id = body.id ?: UUID.randomUUID().toString(),
                            idUsuario = body.idUsuario,
                            rua = body.rua,
                            cidade = body.cidade,
                            estado = body.estado,
                            cep = body.cep,
                            numero = body.numero,
                            complemento = body.complemento
                        )
                    )

                    val decodeBody = Json.decodeFromString<UsuarioEndereco>(encodeBody)

                    collection.insertOne(decodeBody)

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
            delete("{id?}") {
                try {
                    val param =
                        call.parameters["id"] ?: return@delete call.respondText(
                            "Faltando id",
                            status = HttpStatusCode.BadRequest
                        )

                    val filter = eq(UsuarioEndereco::id.name, param)

                    collection.findOneAndDelete(filter)

                    call.respondText("Endereço removido", status = HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respondText("${e.message}")
                }
            }
        }
    }
}