package com.cafeteria.models

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioCarrinhoItens(
    val id: String?, val idUsuario: String, val quantidade: Int, val produto:
    Produto
)
