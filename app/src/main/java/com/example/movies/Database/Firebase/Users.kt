package com.example.movies.Database.Firebase

data class Users(
    val email: String = "",
    val username: String = "",
    val nama: String = "",
    val role: String = "",
    val password: String = "",
    val token: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "email" to email,
            "username" to username,
            "nama" to nama,
            "role" to role,
            "password" to password,
            "token" to token
        )
    }
}
