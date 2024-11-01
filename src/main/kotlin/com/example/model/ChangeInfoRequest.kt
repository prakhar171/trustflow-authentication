// File: src/main/kotlin/com/example/model/ChangeInfoRequest.kt
package com.example.model

data class ChangeInfoRequest(
        val username: String,
        val name: String,
        val phone: String,
        val email: String,
        val nationality: String
)