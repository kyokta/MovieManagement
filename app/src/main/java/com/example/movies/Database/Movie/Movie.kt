package com.example.movies.Database.Movie

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_table")
data class Movie (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "place")
    val place: String,

    @ColumnInfo(name = "date")
    val date: String
)