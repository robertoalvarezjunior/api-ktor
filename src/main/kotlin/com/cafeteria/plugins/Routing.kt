package com.cafeteria.plugins

import com.cafeteria.routes.cadastroRouting
import com.cafeteria.routes.loginRouting
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(database: MongoDatabase) {

    routing {
        loginRouting(database = database)
        cadastroRouting(database = database)
    }
}