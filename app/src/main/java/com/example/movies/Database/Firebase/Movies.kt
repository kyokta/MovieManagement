package com.example.movies.Database.Firebase

import androidx.room.PrimaryKey

data class Movies(
    @PrimaryKey
    var id: String = "",
    val title: String = "",
    var image: String = "",
    val description: String = "",
    val place: String = "",
    val date: String = ""
)