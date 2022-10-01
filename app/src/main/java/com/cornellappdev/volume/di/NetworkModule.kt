package com.cornellappdev.volume.di

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.cornellappdev.volume.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val ENDPOINT = BuildConfig.DEV_ENDPOINT

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("NetworkRequest", message)
            }
        })
        logging.level = (HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient
            .Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun provideApollo(
        okHttpClient: OkHttpClient,
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(ENDPOINT)
            .okHttpClient(okHttpClient)
            .build()
    }
}
