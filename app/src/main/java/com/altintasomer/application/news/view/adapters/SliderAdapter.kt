package com.altintasomer.application.news.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.altintasomer.application.news.R
import com.altintasomer.application.news.model.db.ArticlesEntity
import com.altintasomer.application.news.model.network.v2headlines.Articles
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso

class SliderAdapter(
    private val articlesList: ArrayList<Articles?>,
    private val readList : List<ArticlesEntity>,
    private var onInsertClicked : (articles: Articles) -> Unit,
    private var onDeleteClicked : (articles: Articles) -> Unit
) : SliderViewAdapter<SliderAdapter.SliderViewHolder>() {

    fun update(articlesList: List<Articles>) {
        this.articlesList.clear()
        if (articlesList != null) {
            this.articlesList.addAll(articlesList)
        }
        notifyDataSetChanged()
    }

    class SliderViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val sliderImage = itemView.findViewById<ImageView>(R.id.slide_rimage)
        val title = itemView.findViewById<TextView>(R.id.title)
        val date = itemView.findViewById<TextView>(R.id.tv_date)
        val addRemove = itemView.findViewById<TextView>(R.id.tv_add_read_list)

    }

    override fun getCount(): Int = articlesList.size

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        val articles = articlesList.get(position)
        if (!articles?.urlToImage.isNullOrEmpty())
            Picasso.get().load(articles?.urlToImage).into(viewHolder.sliderImage)
        viewHolder.title.text = articles?.title
        viewHolder.date.text = onlyDate(articles?.publishedAt)
        viewHolder.addRemove.setOnClickListener {
            if (viewHolder.addRemove.text.equals(viewHolder.itemView.resources.getString(R.string.added))) {
                viewHolder.addRemove.text = viewHolder.itemView.resources.getString(R.string.added_read)
                if (articles != null) {
                    onDeleteClicked(articles)
                }
            } else {
                viewHolder.addRemove.text = viewHolder.itemView.resources.getString(R.string.added)
                if (articles != null) {
                    onInsertClicked(articles)
                }
            }
        }

        /**
         * check read list
         * */
        readList.forEach { _read->
            val rUrl = _read.url?:""
            val cUrl = articles?.url?:""
            if (rUrl.equals(cUrl)){
                viewHolder.addRemove.text = viewHolder.itemView.resources.getString(R.string.added)
            }
        }
    }

    private fun onlyDate(fullTime : String?) : String{
        return fullTime?.subSequence(0,10).toString()
    }
}