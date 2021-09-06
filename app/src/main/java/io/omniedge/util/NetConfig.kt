package io.omniedge.util

import com.google.gson.GsonBuilder
import io.omniedge.App
import io.omniedge.BuildConfig
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetConfig {

    private fun provideHttpClient(): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = App.repository.getToken()
                var request = chain.request()
                if (!token.isNullOrBlank()) {
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }

                chain.proceed(request)
            }.also {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    it.addInterceptor(loggingInterceptor)
                }
            }.build()
    }

    fun provideRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .client(provideHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .create()
                )
            )
            .baseUrl(baseUrl)
            .build();

    }
}