package com.mxb.app.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.mxb.app.datastore.LocalDataRepository
import com.mxb.app.network.ApiService
import com.mxb.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    companion object {
        const val API_KEY_ACCEPT = "Accept"
        const val API_VALUE_ACCEPT = "application/json"
        const val API_KEY = "KEY"
        const val API_KEY_VALUE = "GoRBT@123*"
        const val API_KEY_AUTHORIZATION = "Authorization"
        const val API_KEY_BEARER = "Bearer"
    }

    @Inject
    lateinit var localDataRepository: LocalDataRepository

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, localDataRepository: LocalDataRepository): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient(localDataRepository))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


    @Singleton
    @Provides
    fun provideService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


    @Singleton
    @Provides
    fun okHttpClient(localDataRepository: LocalDataRepository): OkHttpClient {

        val levelType: HttpLoggingInterceptor.Level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val logging = HttpLoggingInterceptor()
        logging.setLevel(levelType)
        try {
            val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
            httpClient.addInterceptor(Interceptor { chain ->
                val original: Request = chain.request()
                val apiKey: String = localDataRepository.getBarrierToken()
                val requestBuilder = original.newBuilder()
                requestBuilder.addHeader(API_KEY_ACCEPT, API_VALUE_ACCEPT)
                requestBuilder.addHeader(API_KEY, API_KEY_VALUE)
                if (apiKey.isNotEmpty()) {
                    requestBuilder.addHeader(
                        API_KEY_AUTHORIZATION,
                        "$API_KEY_BEARER $apiKey"
                    )
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            })
            httpClient.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .pingInterval(1, TimeUnit.SECONDS)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            httpClient.addNetworkInterceptor(logging)
            val client = httpClient.build()
            return client
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return OkHttpClient.Builder()
            .addNetworkInterceptor(logging)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    @Singleton
    @Provides
    fun gson(): Gson = GsonBuilder().setStrictness(Strictness.LENIENT).create()

}