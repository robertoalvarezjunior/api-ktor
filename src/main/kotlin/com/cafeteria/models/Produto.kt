package com.cafeteria.models

import kotlinx.serialization.Serializable

@Serializable
data class Produto(
    val id: String?,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val imagem: String,
    val tag: String
)
