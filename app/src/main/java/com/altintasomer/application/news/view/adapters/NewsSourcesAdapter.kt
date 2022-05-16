package com.altintasomer.application.news.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.altintasomer.application.news.databinding.NewsSourcesItemBinding
import com.altintasomer.application.news.model.network.v2headlines.Source
import com.altintasomer.application.news.model.network.v2source.Sources
import com.altintasomer.application.news.utils.ItemAnimation
import com.altintasomer.application.news.utils.ItemAnimation.Companion.FADE_IN

class NewsSourcesAdapter(
   private var onItemClicked : (source : Sources) -> Unit
) : RecyclerView.Adapter<NewsSourcesAdapter.SourcesItemViewHolder>() {
    class SourcesItemViewHolder(val binding: NewsSourcesItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val differCallback = object : DiffUtil.ItemCallback<Sources>() {
        override fun areItemsTheSame(oldItem: Sources, newItem: Sources): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Sources, newItem: Sources): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onBindViewHolder(holder: SourcesItemViewHolder, position: Int) {
        with(holder.binding) {
            sources = differ.currentList.get(position)
            executePendingBindings()
            setAnimation(holder.itemView,position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourcesItemViewHolder {
        val binding =
            NewsSourcesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SourcesItemViewHolder(binding).apply {
            binding.root.setOnClickListener {
                adapterPosition.also {
                    if (it != Adapter.NO_SELECTION) {
                        differ.currentList.get(it)?.let {
                            onItemClicked(it)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size//sourcesList.size

    private var lastPosition = -1
    private val on_attach = true
    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, if (on_attach) position else -1, FADE_IN)
            lastPosition = position
        }
    }

}