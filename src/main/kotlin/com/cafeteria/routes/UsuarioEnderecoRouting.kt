package com.cafeteria.routes

import com.cafeteria.models.UsuarioEndereco
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

fun Route.usuarioEnderecoRouting(database: MongoDatabase) {
    route("/usuarioEndereco") {
        val collection = database.getCollection<UsuarioEndereco>("usuariosEndereco")

        get {
            try {
                val usuariosEndereco = collection.find().toList()

                call.respond(usuariosEndereco)
            } catch (e: Exception) {
                call.respondText("${e.message}")

            }
        }

        post {
            try {
                val endereco = call.receive<UsuarioEndereco>()

                val decodeEndereco = Json.encodeToString(
                    UsuarioEndereco(
                        id = endereco.id ?: UUID.randomUUID().toString(),
                        idUsuario = endereco.idUsuario,
                        rua = endereco.rua,
                        cidade = endereco.cidade,
                        estado = endereco.estado,
                        cep = endereco.cep,
                        numero = endereco.numero,
                        complemento = endereco.complemento
                    )

                )

                collection.insertOne(Json.decodeFromString(decodeEndereco))

                call.respondText("Endereço adicionado", status = HttpStatusCode.OK)

            } catch (e: Exception) {
                call.respondText("${e.message}")
            }
        }

        put {
            try {
                val endereco = call.receive<UsuarioEndereco>()

                collection.replaceOne(Filters.eq("id", endereco.id), endereco)

                call.respondText("Endereço atualizado", status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("${e.message}")
            }
        }

        delete("/{id?}") {
            try {
                val param = call.parameters["id"] ?: return@delete call.respondText(
                    "Faltando id",
                    status = HttpStatusCode.BadRequest
                )

                collection.deleteOne(Filters.eq("id", param))

                call.respondText("Endereço removido", status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("${e.message}")
            }
        }
    }
}