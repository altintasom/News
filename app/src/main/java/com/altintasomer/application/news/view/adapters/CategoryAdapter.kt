package com.altintasomer.application.news.view.adapters

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.altintasomer.application.news.R
import com.altintasomer.application.news.databinding.CategoryItemLayoutBinding
import com.altintasomer.application.news.utils.ItemAnimation

private const val TAG = "CategoryAdapter"
class CategoryAdapter(
    private val categories: ArrayList<String>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

   private val selectedCategories = ArrayList<String>()
    interface OnItemClickListener {
        fun onAddCategory(category : String)
        fun onDeleteCategory(category: String)
    }

    fun updateList(categories: ArrayList<String>) {
        this.categories.clear()
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }

    class CategoryViewHolder(val binding: CategoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            CategoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CategoryAdapter.CategoryViewHolder(binding).apply {
            binding.btnCategory.setOnClickListener {
                adapterPosition.also {
                    if (it != Adapter.NO_SELECTION) {
                        binding.btnCategory.isSelected = !binding.btnCategory.isSelected

                        if (binding.btnCategory.isSelected){
                            Log.d(TAG, "onCreateViewHolder: selected: "+binding.btnCategory.text)
                            onItemClickListener.onAddCategory(binding.btnCategory.text?.toString()?:"")
                        }else{
                            Log.d(TAG, "onCreateViewHolder: not selected: "+binding.btnCategory.text)
                            onItemClickListener.onDeleteCategory(binding.btnCategory.text?.toString()?:"")
                        }


                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        with(holder.binding) {
            category = categories.get(position)
            executePendingBindings()
            setAnimation(holder.itemView, position)
        }
    }

    override fun getItemCount(): Int = categories.size

    private var lastPosition = -1
    private val on_attach = true
    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, if (on_attach) position else -1, ItemAnimation.FADE_IN)
            lastPosition = position
        }
    }
}