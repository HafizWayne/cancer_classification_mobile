package com.dicoding.asclepius.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.response.ArticlesItem

class NewsAdapter(private val listNews: List<ArticlesItem> ) :
        RecyclerView.Adapter<NewsAdapter.ViewHolder>() {




    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsTextView: TextView = itemView.findViewById(R.id.newsTitleTextView)
        val newsImageView: ImageView = itemView.findViewById(R.id.newsImageView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.newsDetailView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listNews.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = listNews[position]

        holder.newsTextView.text = news.title
        holder.descriptionTextView.text = news.description
        Glide.with(holder.itemView.context)
            .load(news.urlToImage)
            .placeholder(R.drawable.ic_place_holder)
            .error(R.drawable.ic_error)
            .into(holder.newsImageView)
    }
    }


