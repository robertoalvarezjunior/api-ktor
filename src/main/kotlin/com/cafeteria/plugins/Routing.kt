package com.cafeteria.plugins

import com.cafeteria.routes.usuarioRouting
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(database: MongoDatabase) {

    routing {
        usuarioRouting(database = database)
    }
}