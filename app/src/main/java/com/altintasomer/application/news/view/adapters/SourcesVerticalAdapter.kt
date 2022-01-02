package com.altintasomer.application.news.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.altintasomer.application.news.R
import com.altintasomer.application.news.databinding.NewsSourcesItemBinding
import com.altintasomer.application.news.databinding.SliderItemBinding
import com.altintasomer.application.news.databinding.VerticalSourceItemBinding
import com.altintasomer.application.news.model.db.ArticlesEntity
import com.altintasomer.application.news.model.network.v2headlines.Articles
import com.altintasomer.application.news.model.network.v2source.Sources
import com.altintasomer.application.news.utils.ItemAnimation
import com.squareup.picasso.Picasso

private const val TAG = "SourcesVerticalAdapter"

class SourcesVerticalAdapter(
    private val readList : List<ArticlesEntity>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SourcesVerticalAdapter.SourcesItemViewHolder>() {

    interface OnItemClickListener {
        fun onInsertClick(articles: Articles)
        fun onDeleteClick(articles: Articles)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Articles>() {
        override fun areItemsTheSame(oldItem: Articles, newItem: Articles): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Articles, newItem: Articles): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    class SourcesItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sliderImage = itemView.findViewById<ImageView>(R.id.iv_source)
        val title = itemView.findViewById<TextView>(R.id.title)
        val date = itemView.findViewById<TextView>(R.id.tv_date)
        val addRemove =itemView.findViewById<TextView>(R.id.tv_add_read_list)
    }

    override fun onBindViewHolder(holder: SourcesItemViewHolder, position: Int) {
        val articles = differ.currentList.get(position)
        holder.title.text = articles.title
        holder.date.text =onlyDate(articles.publishedAt)
        Picasso.get().load(articles.urlToImage).into(holder.sliderImage)
        holder.addRemove.setOnClickListener {
           if (holder.addRemove.text.equals(holder.itemView.resources.getString(R.string.added))){
               holder.addRemove.text = holder.itemView.resources.getString(R.string.added_read)
               onItemClickListener.onDeleteClick(articles)
           }else{
               holder.addRemove.text = holder.itemView.resources.getString(R.string.added)
               onItemClickListener.onInsertClick(articles)
           }
        }
        setAnimation(holder.itemView,position)
        /**
         * check read list
         * */
        readList.forEach { _read->
            val rUrl = _read.url?:""
            val cUrl = articles?.url?:""
            if (rUrl.equals(cUrl)){
                holder.addRemove.text = holder.itemView.resources.getString(R.string.added)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourcesItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vertical_source_item,parent,false)

        return SourcesItemViewHolder(view)
    }

    override fun getItemCount(): Int = differ.currentList.size

    /**
     * animation function
     * */
    private var lastPosition = -1
    private val on_attach = true
    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, if (on_attach) position else -1, ItemAnimation.FADE_IN)
            lastPosition = position
        }
    }

    private fun onlyDate(fullTime : String?) : String{
        return fullTime?.subSequence(0,10).toString()
    }
}