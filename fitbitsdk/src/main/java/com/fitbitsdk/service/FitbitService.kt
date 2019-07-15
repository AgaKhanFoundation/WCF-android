package com.fitbitsdk.service

import android.content.SharedPreferences
import android.util.Log
import com.fitbitsdk.authentication.ClientCredentials
import com.fitbitsdk.gsonhelper.SafeGson
import com.fitbitsdk.service.api.*
import com.fitbitsdk.service.api.endpoint.AuthEndpoint
import com.fitbitsdk.service.api.endpoint.Endpoint
import com.fitbitsdk.service.api.endpoint.Environment
import com.fitbitsdk.service.api.okhttp.interceptor.AuthenticationInterceptor
import com.fitbitsdk.service.api.okhttp.interceptor.Authenticator
import com.fitbitsdk.service.api.okhttp.interceptor.BasicAuthenticationInterceptor
import com.fitbitsdk.service.api.okhttp.retrofit.RetrofitRefreshTokenService
import com.fitbitsdk.service.models.auth.OAuthAccessToken
import com.fitbitsdk.service.models.auth.OAuthAccessTokenTypeAdapter
import com.fitbitsdk.service.storage.SharedPreferenceTokenStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FitbitService(sharedPreferences: SharedPreferences, clientCredentials: ClientCredentials){

    var service: Retrofit
    var basicService: Retrofit


    init{
        val authEndpoint = AuthEndpoint(clientCredentials.clientId, clientCredentials.clientSecret)
        val endpoint = object: Endpoint(){
            override val authEndpoint: AuthEndpoint
                get() = authEndpoint
        }
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        val oAuthDataService = OAuthDataService(
                SharedPreferenceTokenStorage(sharedPreferences),
                RetrofitRefreshTokenService(
                        Environment.PRODUCTION,
                        endpoint,
                        OkHttpClient.Builder()
                                .addInterceptor(loggingInterceptor)
                ),
                object: NetworkConnectivityChecker {
                    override fun isConnected(): Boolean {
                        return true
                    }

                },
                object: TokenDroppedListener{
                    override fun onTokenDropped() {
                        Log.d("Authenticator","Token Dropped")
                    }

                }
        )
        val client = OkHttpClient.Builder()
                .authenticator(Authenticator(
                        oAuthDataService
                ))
                .addInterceptor(loggingInterceptor)
                .addInterceptor(AuthenticationInterceptor(oAuthDataService))
                .build()

        val basicClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(BasicAuthenticationInterceptor(authEndpoint, Environment.PRODUCTION))
                .build()

        val builder = SafeGson.getSingleton().gsonInstance.newBuilder()
        builder.registerTypeAdapter(OAuthAccessToken::class.java, OAuthAccessTokenTypeAdapter())
        service = Retrofit.Builder()
                .baseUrl("https://api.fitbit.com")
                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                .client(client)
                .build()

        basicService = Retrofit.Builder()
                .baseUrl("https://api.fitbit.com")
                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                .client(basicClient)
                .build()
    }

    fun getBodyAndWeightService(): BodyAndWeightService{
        return service.create(BodyAndWeightService::class.java)
    }

    fun getUserService(): UserService{
        return service.create(UserService::class.java)
    }

    fun getTokenService(): TokenService{
        return basicService.create(TokenService::class.java)
    }

    fun getActivityService(): ActivityApi{
        return service.create(ActivityApi::class.java)
    }

    fun getDeviceService(): DevicesApi{
        return service.create(DevicesApi::class.java)
    }

}