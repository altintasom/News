package com.altintasomer.application.news.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altintasomer.application.news.R
import com.altintasomer.application.news.model.RepoImp
import com.altintasomer.application.news.model.network.v2source.Sources
import com.altintasomer.application.news.utils.Event
import com.altintasomer.application.news.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val TAG = "NewsSourcesListViewMode"

@HiltViewModel
class NewsSourcesListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: RepoImp
) : ViewModel() {

    private val _newsSources = MutableLiveData<Event<Resource<List<Sources>>>>()
    val newsSources: LiveData<Event<Resource<List<Sources>>>> get() = _newsSources

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> get() = _categories

    private val _sourceCategories = ArrayList<String>()

    private val filteringSources = ArrayList<Sources>()

    /*private val _filteredSources = ArrayList<Sources>()*/

    private val _selectedCategories = ArrayList<String>()
    val selectedCategories: List<String> get() = _selectedCategories

    fun init() {
        getNewsSourcesList()
    }

    private fun getNewsSourcesList() {
        viewModelScope.launch(Dispatchers.IO) {
            _newsSources.postValue(Event(Resource.loading()))
            val response = try {
                repo.getNewsSources()
            } catch (e: Exception) {
                Log.e(TAG, "getNewsSourcesList: ", e)
                _newsSources.postValue(Event(Resource.error(context.getString(R.string.error_connection))))
                return@launch
            }

            if (response.isSuccessful) {
                val sources = response.body()?.sources
                if (sources.isNullOrEmpty()) {
                    Log.e(TAG, "getNewsSourcesList: sources is null or empty")
                    _newsSources.postValue(Event(Resource.error(context.getString(R.string.error_no_news_sources))))
                } else {
                    sources.forEach {
                        if (!_sourceCategories.contains(it.category)) {
                            _sourceCategories.add(it.category ?: "")
                        }
                    }
                    _categories.postValue(_sourceCategories)
                    filteringSources.addAll(sources)
                    _newsSources.postValue(Event(Resource.success(filteringSources)))

                }

            } else {
                _newsSources.postValue(Event(Resource.error(response.message())))
            }
        }
    }

    fun deleteCategory(category: String) {
        _selectedCategories.remove(category)

        if (_selectedCategories.isEmpty()){
            _newsSources.postValue(Event(Resource.success(filteringSources)))
        }else{
            val tempSources = ArrayList<Sources>()

            filteringSources.forEach { _sources ->
                val rCategory = _sources.category ?: ""
                _selectedCategories.forEach { _category ->
                    Log.d(TAG, "deleteCategory: _category: "+ _category.lowercase()+ " rCategory: "+rCategory)

                    if (_category.equals(rCategory)) {
                        tempSources.add(_sources)
                    }
                }

            }
            tempSources.forEach {
                Log.d(TAG, "deleteCategory: "+it.category)
            }
            _newsSources.postValue(Event(Resource.success(tempSources)))
        }



    }

    fun addCategory(category: String) {
        _selectedCategories.add(category)

        if (_selectedCategories.isEmpty()){
            _newsSources.postValue(Event(Resource.success(filteringSources)))
        }else{
            val tempSources = ArrayList<Sources>()

            filteringSources.forEach { _sources ->
                val rCategory = _sources.category ?: ""
                _selectedCategories.forEach { _category ->
                    Log.d(TAG, "deleteCategory: _category: "+ _category.lowercase()+ " rCategory: "+rCategory)

                    if (_category.equals(rCategory)) {
                        tempSources.add(_sources)
                    }
                }

            }

            _newsSources.postValue(Event(Resource.success(tempSources)))
        }
    }
}