package com.example.movies.Database.Movie

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movie: Movie)

    @Update
    fun update(movie: Movie)

    @Delete
    fun delete(movie: Movie)

    @get:Query("SELECT * from movie_table ORDER BY id ASC")
    val allMovies: LiveData<List<Movie>>

    @Query("SELECT * FROM movie_table WHERE id = :movieId")
    fun getMovieById(movieId: Int): LiveData<Movie?>

    @Query("DELETE FROM movie_table WHERE movie_id = :movieId")
    fun deleteMovieById(movieId: String)
}