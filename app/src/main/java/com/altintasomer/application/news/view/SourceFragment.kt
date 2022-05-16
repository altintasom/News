package com.altintasomer.application.news.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.altintasomer.application.news.R
import com.altintasomer.application.news.databinding.FragmentSourceBinding
import com.altintasomer.application.news.model.db.ArticlesEntity
import com.altintasomer.application.news.model.network.v2headlines.Articles
import com.altintasomer.application.news.utils.Status
import com.altintasomer.application.news.view.adapters.SliderAdapter
import com.altintasomer.application.news.view.adapters.SourcesVerticalAdapter
import com.altintasomer.application.news.viewmodel.NewsSourceViewModel
import com.google.android.material.snackbar.Snackbar
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SourceFragment"

@AndroidEntryPoint
class SourceFragment : Fragment(R.layout.fragment_source) {

    private val args: SourceFragmentArgs by navArgs()

    private val viewModel: NewsSourceViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        /**
         * bind layout view
         * */
        val binding = FragmentSourceBinding.bind(view)

        /**
         * init repeat api operation for one minute
         * */
        viewModel.init(args.sourceId)

        /**
         * get selected news source
         * */
        viewModel.getSelectedSource(args.sourceId)

        /**
         * get read list from db
         * */
        viewModel.getMyNews()

        /**
         * initialize slider adapter and callback function for db operation (insert and delete)
         * */
        val sliderAdapter =
            SliderAdapter(arrayListOf(),viewModel.articleEntities, onDeleteClicked = {
                viewModel.deleteWithUrl(it.articleEntity())
            },onInsertClicked = {
                viewModel.insertArticles(it.articleEntity())
            })
        /**
         * initialize adapter and callback function for db operation (insert and delete)
         * */
        val verticalAdapter = SourcesVerticalAdapter(viewModel.articleEntities,
            onDeleteClicked = {
                viewModel.deleteWithUrl(it.articleEntity())
            },onInsertClicked = {
                viewModel.insertArticles(it.articleEntity())
            })
        /**
         * setting slider field
         * */
        binding.sliderView.also {
            it.setSliderAdapter(sliderAdapter)
            it.setIndicatorAnimation(IndicatorAnimationType.SLIDE)
            it.setSliderTransformAnimation(SliderAnimations.FANTRANSFORMATION)
            it.startAutoCycle()
            it.scrollTimeInMillis = 5000
        }

        /**
         * observing api response and UI Update
         * */
        viewModel.selectedSource.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        it.data?.let {

                            if (it.size > 2) {
                                val firstThreeNews: ArrayList<Articles> =
                                    ArrayList(it.subList(0, 3))
                                sliderAdapter.update(firstThreeNews)
                            }

                            if (it.size > 3) {
                                val threeToLast: ArrayList<Articles> =
                                    ArrayList(it.subList(3, (it.size)))
                                verticalAdapter.differ.submitList(threeToLast)
                            }

                        }


                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(view, it.message ?: "Error", Snackbar.LENGTH_INDEFINITE)
                            .setAction(
                                resources.getText(R.string.again),
                                object : View.OnClickListener {
                                    override fun onClick(p0: View?) {
                                        viewModel.getSelectedSource(args.sourceId)
                                    }

                                }
                            ).show()
                    }

                }
            }
        }

        binding.rvVertical.also {
            it.adapter = verticalAdapter
            val span =
                1
            it.layoutManager = GridLayoutManager(context, span, GridLayoutManager.VERTICAL, false)
        }

        /**
         * handle the status of db operation
         * */
        viewModel.dbStatus.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.ERROR -> {
                        Log.e(TAG, "init: error")
                        Snackbar.make(view, it.message ?: "Error", Snackbar.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        Log.d(TAG, "init: loading")
                    }
                    Status.SUCCESS -> {
                        Log.d(TAG, "init: success")
                        Snackbar.make(view, it.data ?: "success", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.repeatSearchFinish.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.swipe.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                viewModel.incraeaseSwipeCounter()
                viewModel.getSelectedSource(args.sourceId)
                binding.swipe.isRefreshing = false
            }

        })

    }

    fun Articles.articleEntity(): ArticlesEntity {
        return ArticlesEntity(
            sourceId = this.source?.id,
            sourceName = this.source?.name,
            title = this.title,
            url = this.url
        )
    }


}