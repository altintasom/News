package com.altintasomer.application.news.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.altintasomer.application.news.R
import com.altintasomer.application.news.databinding.FragmentNewsSourcesListBinding
import com.altintasomer.application.news.model.network.v2source.Sources
import com.altintasomer.application.news.utils.Status
import com.altintasomer.application.news.view.adapters.CategoryAdapter
import com.altintasomer.application.news.view.adapters.NewsSourcesAdapter
import com.altintasomer.application.news.viewmodel.NewsSourcesListViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "NewsSourcesListFragment"
@AndroidEntryPoint
class NewsSourcesListFragment : Fragment(R.layout.fragment_news_sources_list) {
    private val viewModel: NewsSourcesListViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        viewModel.init()
    }

    private fun init(view: View) {
        val binding = FragmentNewsSourcesListBinding.bind(view)

        val newsSourcesAdapter = NewsSourcesAdapter(onItemClicked = {
            val action = NewsSourcesListFragmentDirections.actionNewsSourcesListFragmentToSourceFragment(sourceId = it.id?:"")
            findNavController().navigate(action)
        })

        binding.rvNewsSources.also{
            it.adapter = newsSourcesAdapter
            val span = 1//if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 1
            it.layoutManager = GridLayoutManager(context, span, GridLayoutManager.VERTICAL, false)
        }


        viewModel.newsSources.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it.status){
                    Status.LOADING ->{
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS ->{
                        binding.progressBar.visibility = View.GONE
                        it.data?.let {
                            newsSourcesAdapter.differ.submitList(it)
                        }
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(view, it.message ?: "Error", Snackbar.LENGTH_INDEFINITE)
                            .setAction(
                                resources.getText(R.string.again),
                                object : View.OnClickListener{
                                    override fun onClick(p0: View?) {
                                        viewModel.init()
                                    }

                                }
                            ).show()

                    }

                }
            }
        }

        val adapterCategory = CategoryAdapter(arrayListOf(),onAddCategory = {
            viewModel.addCategory(it)
        },onDeleteCategory = {
            viewModel.deleteCategory(it)
        })

       binding.rvCategory.also {
           it.adapter = adapterCategory
            it.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
       }
        viewModel.categories.observe(viewLifecycleOwner){
            adapterCategory.updateList(it as ArrayList<String>)
        }
    }



}