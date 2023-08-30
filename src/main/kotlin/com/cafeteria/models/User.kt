package com.cafeteria.models

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: String, val email: String, val senha: String)
