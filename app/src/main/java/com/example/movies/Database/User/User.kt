package com.example.movies.Database.User

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int = 0,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "password")
    var password: String,

    @ColumnInfo(name = "role")
    var role: String,

    @ColumnInfo(name = "token")
    var token : String
)