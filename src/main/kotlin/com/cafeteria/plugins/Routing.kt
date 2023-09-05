package com.cafeteria.plugins

import com.cafeteria.routes.produtosRouting
import com.cafeteria.routes.usuarioCarrinhoRouting
import com.cafeteria.routes.usuarioEnderecoRouting
import com.cafeteria.routes.usuarioRouting
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(database: MongoDatabase) {

    routing {
        usuarioRouting(database = database)
        usuarioEnderecoRouting(database = database)
        produtosRouting(database = database)
        usuarioCarrinhoRouting(database = database)
    }
}