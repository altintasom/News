package com.altintasomer.application.news.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altintasomer.application.news.R
import com.altintasomer.application.news.model.RepoImp
import com.altintasomer.application.news.model.db.ArticlesEntity
import com.altintasomer.application.news.model.network.v2headlines.Articles
import com.altintasomer.application.news.utils.Event
import com.altintasomer.application.news.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NewsSourceViewModel"

@HiltViewModel
class NewsSourceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: RepoImp
) : ViewModel() {

    private val _selectedSource = MutableLiveData<Event<Resource<List<Articles>>>>()
    val selectedSource: LiveData<Event<Resource<List<Articles>>>> get() = _selectedSource

    private val _dbStatus = MutableLiveData<Event<Resource<String>>>()
    val dbStatus: MutableLiveData<Event<Resource<String>>> get() = _dbStatus

    private val _articleies = ArrayList<Articles>()
    val articleies: List<Articles> get() = _articleies

    private val _articleEntities = ArrayList<ArticlesEntity>()
    val articleEntities: List<ArticlesEntity> get() = _articleEntities

    private val _repeatSearchFinish = MutableLiveData<Boolean>()
    val repeatSearchFinish: LiveData<Boolean> get() = _repeatSearchFinish

    private var _swipeCount: Int = 0
    fun incraeaseSwipeCounter(){
        _swipeCount++
    }

    fun init(sourceId: String) {
        controlNewNews(sourceId)
    }

    /**
     * get selected news source
     * */
    fun getSelectedSource(sourceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedSource.postValue(Event(Resource.loading()))
            if (_swipeCount < 3){
                val response = try {
                    repo.getNewsSourceForSelected(sourceId)
                } catch (e: Exception) {
                    Log.e(TAG, "getSelectedSource: ", e)
                    _selectedSource.postValue(Event(Resource.error(context.getString(R.string.error_connection))))
                    return@launch
                }

                if (response.isSuccessful) {
                    val responseArticles = response.body()?.articles

                    val isDiffer = isDiffer(
                        _articleies,
                        responseArticles
                    )/*responseArticles?.let { isDiffer(_articleies, it) }*/
                    if (!isDiffer) {
                        if (responseArticles?.isEmpty() == true) {
                            _selectedSource.postValue(Event(Resource.error("News not found!")))
                        } else {
                            if (responseArticles != null) {
                                _articleies.addAll(responseArticles)
                                _selectedSource.postValue(Event(Resource.success(responseArticles)))
                            }
                            _repeatSearchFinish.postValue(true)
                        }
                    } else {
                        _repeatSearchFinish.postValue(true)
                    }

                } else {
                    _selectedSource.postValue(Event(Resource.error(response.message())))
                }
            }else{
                _swipeCount = 0
                _selectedSource.postValue(Event(Resource.error(context.getString(R.string.noting_info))))
            }


        }
    }
    /**
     * insert ob operation for read list
     * */
    fun insertArticles(articlesEntity: ArticlesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbStatus.postValue(Event(Resource.loading()))
            val response = try {
                Log.d(TAG, "insertArticles: "+articlesEntity)
                repo.insertArticles(articlesEntity)
                _dbStatus.postValue(Event(Resource.success(context.resources.getString(R.string.success_added))))
            } catch (e: Exception) {
                Log.e(TAG, "insertArticles: ", e)
                _dbStatus.postValue(Event(Resource.error(context.resources.getString(R.string.error_added))))
                return@launch
            }
        }
    }

    fun getMyNews() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = try {
                repo.getMyNews()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
            response.forEach {
                Log.d(TAG, "getMyNews: "+it.title)
            }
            _articleEntities.addAll(response)
        }
    }

    fun deleteMyNews(articlesEntity: ArticlesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbStatus.postValue(Event(Resource.loading()))
            val response = try {
                repo.deleteArticles(articlesEntity)
                dbStatus.postValue(Event(Resource.success(context.resources.getString(R.string.success_deleted))))
            } catch (e: Exception) {
                Log.e(TAG, "insertArticles: ", e)
                _dbStatus.postValue(Event(Resource.error(context.resources.getString(R.string.error_deleted))))
                return@launch
            }
        }
    }

    /**
     * delete db operation for read list
     * */
    fun deleteWithUrl(articlesEntity: ArticlesEntity){
        viewModelScope.launch(Dispatchers.IO) {
            _dbStatus.postValue(Event(Resource.loading()))
            val response = try {
                repo.deleteWithUrl(articlesEntity.url)
                dbStatus.postValue(Event(Resource.success(context.resources.getString(R.string.success_deleted))))
            } catch (e: Exception) {
                _dbStatus.postValue(Event(Resource.error(context.resources.getString(R.string.error_deleted))))
                return@launch
            }
        }
    }

    /**
     * init repeat api operation for one minute
     * */
    fun controlNewNews(sourceId: String): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                getSelectedSource(sourceId)
                delay(60 * 1000)
            }
        }
    }

    /**
     *
     * check news list differ or not from api
     *
     * */
    private fun isDiffer(
        oldArticles: ArrayList<Articles>,
        newArticles: ArrayList<Articles>?
    ): Boolean {
        return oldArticles.containsAll(newArticles ?: arrayListOf())
    }


}