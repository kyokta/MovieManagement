package com.example.movies.Offline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.movies.Adapter.ItemAdapterOffline
import com.example.movies.Database.Movie.MovieDao
import com.example.movies.Database.Movie.MovieRoomDatabase
import com.example.movies.databinding.ActivityOfflineMovieBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OfflineMovie : AppCompatActivity() {
    private lateinit var binding: ActivityOfflineMovieBinding
    private lateinit var mMovieDao: MovieDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        executorService = Executors.newSingleThreadExecutor()
        val db = MovieRoomDatabase.getDatabase(this)
        mMovieDao = db!!.movieDao()!!

        super.onCreate(savedInstanceState)
        binding = ActivityOfflineMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
    }

    private fun getData() {
        mMovieDao.allMovies.observe(this) {movies ->
            binding.listData.adapter = ItemAdapterOffline(this, movies)
        }
    }
}