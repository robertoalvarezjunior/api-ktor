package com.cafeteria.models

import java.util.*

data class Produto(
    val id: String, val nome: String, val preco: Double, val quantidade: Int,
    val imagem:
    Base64,
)
