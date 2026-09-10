package com.example.fieldops.data.remote

import android.content.Context
import com.example.fieldops.BuildConfig
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private lateinit var applicationContext: Context

    private val baseUrl: String =
        BuildConfig.FIELDOPS_API_BASE_URL.trim().let { value ->
            if (value.isBlank()) "" else if (value.endsWith("/")) value else "$value/"
        }

    val isConfigured: Boolean
        get() = baseUrl.isNotBlank()

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    private val authInterceptor =
        Interceptor { chain ->
            val requestBuilder =
                chain.request()
                    .newBuilder()
                    .header("Accept", "application/json")

            if (::applicationContext.isInitialized) {
                val token =
                    SessionManager(applicationContext)
                        .token()

                if (!token.isNullOrBlank()) {
                    requestBuilder.header(
                        "Authorization",
                        "Bearer $token"
                    )
                }
            }

            chain.proceed(
                requestBuilder.build()
            )
        }

    val api: FieldOpsApi by lazy {
        require(isConfigured) {
            "FIELDOPS_API_BASE_URL is not configured."
        }

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build()
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(FieldOpsApi::class.java)
    }
}
