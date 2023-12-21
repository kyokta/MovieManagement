package com.example.movies.Database.User

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @get:Query("SELECT * from user_table ORDER BY id ASC")
    val allNotes: LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password")
    fun getUser(username: String, password: String): User?

    @Query("SELECT * FROM user_table WHERE id = :userId")
    fun getUserById(userId: Int): LiveData<User?>
}