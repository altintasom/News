package com.altintasomer.application.news.di

import android.content.Context
import androidx.room.Room
import com.altintasomer.application.news.di.db.MyNewsDao
import com.altintasomer.application.news.di.db.MyNewsDatabase
import com.altintasomer.application.news.di.network.NewsApi
import com.altintasomer.application.news.model.RepoImp
import com.altintasomer.application.news.utils.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun providesPumpPaymentDatabase(@ApplicationContext context: Context) : MyNewsDatabase {
        return Room.databaseBuilder(context, MyNewsDatabase::class.java, "news_articles")
            .build()
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor() : HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor : HttpLoggingInterceptor) : OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(client : OkHttpClient) : NewsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApi::class.java)

    @Singleton
    @Provides
    fun provideWeatherRepo(api : NewsApi,newsDatabase: MyNewsDatabase) : RepoImp{
        return RepoImp(api = api, myNewsDao = newsDatabase.myNewsDao())
    }
}