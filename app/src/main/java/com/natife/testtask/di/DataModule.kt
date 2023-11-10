package com.natife.testtask.di

import android.content.Context
import androidx.room.Room
import com.natife.testtask.data.GifsRepository
import com.natife.testtask.data.GifsRepositoryImpl
import com.natife.testtask.data.local.AppDatabase
import com.natife.testtask.data.local.DeletedGifsDao
import com.natife.testtask.data.local.GifsDao
import com.natife.testtask.data.local.KeysDao
import com.natife.testtask.data.remote.GiphyApi
import com.natife.testtask.util.Constants.DATABASE_NAME
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindGifsRepository(repository: GifsRepositoryImpl): GifsRepository

}

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataModule {
    private const val BASE_URL = "https://api.giphy.com/v1/gifs/"
    private const val API_KEY = "2pllDtmX2IkG5lySzqcdJkP30IOWlPbH"

    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun providesHttpQueryInterceptor(): Interceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val modifiedUrl = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .build()
        val modifiedRequest = originalRequest.newBuilder()
            .url(modifiedUrl)
            .build()
        chain.proceed(modifiedRequest)
    }

    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        httpQueryInterceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(httpQueryInterceptor)
            .build()

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): GiphyApi = retrofit.create(GiphyApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideGifsDao(database: AppDatabase): GifsDao = database.gifsDao

    @Provides
    fun provideKeysDao(database: AppDatabase): KeysDao = database.keysDao

    @Provides
    fun provideDeletedGifsDao(database: AppDatabase): DeletedGifsDao = database.deletedGifsDao
}