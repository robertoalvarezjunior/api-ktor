package com.cafeteria.models

import kotlinx.serialization.Serializable

@Serializable
data class Produto(
    val id: String, val nome: String, val preco: Double, val quantidade: Int,
    val imagem: Byte
)
