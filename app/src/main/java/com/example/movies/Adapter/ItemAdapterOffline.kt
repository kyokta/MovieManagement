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
import com.example.movies.Database.Movie.Movie
import com.example.movies.Offline.OfflineDetail
import com.example.movies.R

class ItemAdapterOffline (
    private val context: Context,
    private val data: List<Movie>
) : RecyclerView.Adapter<ItemAdapterOffline.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.offline_title_movie)
        val date: TextView = view.findViewById(R.id.offline_date_movie)
        val image: ImageView = view.findViewById(R.id.offline_img_movie)
        val place: TextView = view.findViewById(R.id.offline_place_movie)
        val detailCard: View = view.findViewById(R.id.offline_detail_movie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_movie_offline, parent, false)

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
        holder.detailCard.setOnClickListener{
            val detail = Intent(context, OfflineDetail::class.java)
            detail.putExtra("MOVIE_ID", item.id.toString())
            context.startActivity(detail)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}