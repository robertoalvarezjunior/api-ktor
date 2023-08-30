package com.cafeteria.databases

import com.mongodb.kotlin.client.coroutine.MongoClient

val uri = "mongodb+srv://${System.getenv("mongoUsuario")}:${
    System.getenv("mongoSenha")
}@cafeteriadb" +
        ".fi1b8bj" +
        ".mongodb" +
        ".net/?retryWrites=true&w=majority"
val mongoClient = MongoClient.create(uri)
val database = mongoClient.getDatabase("cafeteriaDB")