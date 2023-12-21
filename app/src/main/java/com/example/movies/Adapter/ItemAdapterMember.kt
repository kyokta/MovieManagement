package com.example.movies.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movies.Database.Firebase.Movies
import com.example.movies.Database.Movie.Movie
import com.example.movies.Home.DetailsMember
import com.example.movies.R

class ItemAdapterMember (
    private val context: Context,
    private val data: List<Movies>
) : RecyclerView.Adapter<ItemAdapterMember.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title_movie)
        val date: TextView = view.findViewById(R.id.date_movie)
        val image: ImageView = view.findViewById(R.id.img_movie)
        val place: TextView = view.findViewById(R.id.place_movie)
        val detailCard: View = view.findViewById(R.id.detail_movie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_movie, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]

        Glide.with(context)
            .load(item.image)
            .into(holder.image)
        holder.title.text = item.title
        holder.date.text = item.date
        holder.place.text = item.place
        holder.detailCard.setOnClickListener {
            val intent = Intent(context, DetailsMember::class.java)
            intent.putExtra("MOVIE_ID", item.id.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}