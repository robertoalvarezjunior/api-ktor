package com.cafeteria.routes

import com.cafeteria.models.UsuarioCarrinhoItens
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

fun Route.usuarioCarrinhoRouting(database: MongoDatabase) {
    route("/usuarioCarrinho") {
        val collection =
            database.getCollection<UsuarioCarrinhoItens>("usuarioCarrinhoItens")

        authenticate {
            post {
                try {
                    val body = call.receive<UsuarioCarrinhoItens>()

                    val encodeBody = Json.encodeToString(
                        UsuarioCarrinhoItens(
                            id = body.id ?: UUID.randomUUID().toString(),
                            idUsuario = body.idUsuario,
                            quantidade = body.quantidade,
                            produto = body.produto
                        )
                    )

                    val decodeBody = Json.decodeFromString<UsuarioCarrinhoItens>(encodeBody)

                    collection.insertOne(decodeBody)

                    call.respondText(
                        "Produto adicionado ao carrinho",
                        status = HttpStatusCode.OK
                    )
                } catch (e: Exception) {
                    call.respondText("${e.message}")
                }
            }
        }
        authenticate {
            put("{id?}/{quantidade?}") {
                try {
                    val paramId = call.parameters["id"] ?: return@put call.respondText(
                        "Faltando id",
                        status = HttpStatusCode.BadRequest
                    )

                    val paramQnt = call.parameters["quantidade"] ?: return@put call.respondText(
                        "Faltando id",
                        status = HttpStatusCode.BadRequest
                    )

                    val filter = eq(
                        UsuarioCarrinhoItens::id.name, paramId
                    )

                    val update = Updates.inc(
                        UsuarioCarrinhoItens::quantidade.name,
                        paramQnt.toInt()
                    )

                    collection.findOneAndUpdate(filter, update)

                    call.respondText(
                        "Quantidade atualizada",
                        status = HttpStatusCode.OK
                    )
                } catch (e: Exception) {
                    call.respondText("${e.message}")
                }
            }
        }
        authenticate {
            delete("{id?}") {
                try {
                    val param = call.parameters["id"] ?: return@delete call.respondText(
                        "Faltando id",
                        status = HttpStatusCode.BadRequest
                    )

                    val filter = eq(UsuarioCarrinhoItens::id.name, param)

                    collection.deleteOne(filter)

                    call.respondText(
                        "Produto removido do carrinho",
                        status = HttpStatusCode.OK
                    )
                } catch (e: Exception) {
                    call.respondText("${e.message}")
                }
            }
        }
    }
}