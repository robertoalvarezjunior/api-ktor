package com.cafeteria.routes

import com.cafeteria.models.Produto
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.util.*

fun Route.produtosRouting(database: MongoDatabase) {

    route("/produtos") {
        val collection = database.getCollection<Produto>("produtos")
        get {
            val produtos = collection.find().toList()

            call.respond(Json.encodeToJsonElement(produtos))
        }

        post {
            try {
                val produto = call.receive<Produto>()

                val encodeProduto = Json.encodeToString(
                    Produto(
                        id = produto.id ?: UUID.randomUUID().toString(),
                        nome = produto.nome,
                        descricao = produto.descricao,
                        preco = produto.preco,
                        imagem = produto.imagem,
                        tag = produto.tag
                    )
                )

                collection.insertOne(Json.decodeFromString(encodeProduto))

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