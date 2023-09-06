package com.cafeteria.routes

import com.cafeteria.models.Produto
import com.cafeteria.models.Usuario
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usuarioCarrinhoRouting(database: MongoDatabase) {
    route("/usuarioCarrinho") {
        val collection = database.getCollection<Usuario>("usuarios")

        authenticate {
            post("{idUsuario?}") {
                try {
                    val body = call.receive<Produto>()

                    val param =
                        call.parameters["idUsuario"] ?: return@post call.respondText(
                            "Faltando id",
                            status = HttpStatusCode.BadRequest
                        )

                    val filter = eq(Usuario::idUsuario.name, param)
                    val uptade = Updates.push(Usuario::carrinho.name, body)

                    collection.findOneAndUpdate(filter, uptade)

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
            delete("{idUsuario?}") {
                try {
                    val body = call.receive<Map<String, String>>()

                    val param =
                        call.parameters["idUsuario"] ?: return@delete call.respondText(
                            "Faltando id",
                            status = HttpStatusCode.BadRequest
                        )

                    val filter = eq(Usuario::idUsuario.name, param)

                    val update = Updates.pull(Usuario::carrinho.name, body)

                    collection.updateOne(filter, update)

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