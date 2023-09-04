package com.cafeteria.routes

import com.cafeteria.models.Produto
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList

fun Route.produtosRouting(database: MongoDatabase) {

    route("/produtos") {
        val collection = database.getCollection<Produto>("produtos")
        get {
            val produtos = collection.find().toList()

            call.respond(produtos)
        }

        post {
            try {
                val produto = call.receive<Produto>()

                collection.insertOne(produto)

                call.respondText(
                    "Produto adicionado com sucesso",
                    status = HttpStatusCode.Created
                )
            } catch (e: Exception) {
                call.respondText("${e.message}")
            }
        }
    }
}