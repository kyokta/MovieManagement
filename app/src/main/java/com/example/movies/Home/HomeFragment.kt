package com.example.movies.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.example.movies.Adapter.ItemAdapterMember
import com.example.movies.Database.Firebase.Movies
import com.example.movies.Database.Movie.MovieDao
import com.example.movies.Database.Movie.MovieRoomDatabase
import com.example.movies.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
//    Firebase
    private val firebase = FirebaseFirestore.getInstance()
    private val movieCollection = firebase.collection("movies")
    private val moviesListLiveData : MutableLiveData<List<Movies>> by lazy {
        MutableLiveData<List<Movies>>()
    }

//    Dao
    private lateinit var mMovieDao: MovieDao
    private lateinit var executorService: ExecutorService

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        executorService = Executors.newSingleThreadExecutor()
        val db = MovieRoomDatabase.getDatabase(requireContext())
        mMovieDao = db!!.movieDao()!!

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        var view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userNama = arguments?.getString("USER_NAMA")
        binding.txtName.text = userNama
        observeData()
        getData()
    }

    private fun getData() {
        moviesListLiveData.observe(viewLifecycleOwner) { movies ->
            binding.listData.adapter = ItemAdapterMember(requireContext(), movies)
        }
    }

    private fun observeData() {
        movieCollection.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("HomeFragment", "Error listening for movie: ", error)
                return@addSnapshotListener
            }

            val movies = value?.toObjects(Movies::class.java)
            if (movies != null) {
                moviesListLiveData.postValue(movies)
            }
        }
    }
}