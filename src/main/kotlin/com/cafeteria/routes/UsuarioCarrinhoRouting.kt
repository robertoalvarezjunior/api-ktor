package com.cafeteria.routes

import com.cafeteria.models.Produto
import com.cafeteria.models.Usuario
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usuarioCarrinhoRouting(database: MongoDatabase) {
    route("/usuarioCarrinho") {
        val collection = database.getCollection<Usuario>("usuarios")

        post("/criarCarrinho/{idUsuario}") {

            val param = call.parameters["idUsuario"] ?: return@post call.respondText(
                "Faltando id",
                status = HttpStatusCode.BadRequest
            )

//            var encodeCarrinho = Json.encodeToString(
//                Usuario(
//                    idUsuario = param,
//                    id = UUID.randomUUID().toString(),
//                    produtos = listOf()
//                )
//            )

//            collection.insertOne(Json.decodeFromString())

            call.respondText("Carrinho criado", status = HttpStatusCode.OK)

        }

        get("{idUsuario?}") {
            try {
                val param = call.parameters["idUsuario"] ?: return@get call.respondText(
                    "Faltando id",
                    status = HttpStatusCode.BadRequest
                )

//                val endereco = collection.find(eq(UsuarioCarrinho::idUsuario.name, param))

//                call.respond(endereco.toList())
            } catch (e: Exception) {
                call.respondText("${e.message}")
            }

        }

        put("{idUsuario?}") {
            try {
                val usuarioCarrinho = call.receive<Produto>()

                val param = call.parameters["idUsuario"] ?: return@put call.respondText(
                    "Faltando id",
                    status = HttpStatusCode.BadRequest
                )

                val filter = eq("idUsuario", param)
//                val uptade = Updates.push(UsuarioCarrinho::produtos.name, usuarioCarrinho)

//                collection.findOneAndUpdate(filter, uptade)

                call.respondText(
                    "Produto adicionado ao carrinho",
                    status = HttpStatusCode.OK
                )
            } catch (e: Exception) {
                call.respondText("${e.message}")
            }
        }

        delete("/{idUsuario?}") {
            try {
                val body = call.receive<Map<String, String>>()

                val param =
                    call.parameters["idUsuario"] ?: return@delete call.respondText(
                        "Faltando id",
                        status = HttpStatusCode.BadRequest
                    )

//                val filter = eq(UsuarioCarrinho::idUsuario.name, param)

//                val update = Updates.pull(UsuarioCarrinho::produtos.name, body)
//
//
//                collection.updateOne(filter, update)

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