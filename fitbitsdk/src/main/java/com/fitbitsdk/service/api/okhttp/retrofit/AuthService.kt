package com.fitbitsdk.service.api.okhttp.retrofit

import com.fitbitsdk.service.api.endpoint.AuthEndpoint
import com.fitbitsdk.service.models.auth.OAuthAccessToken
import com.fitbitsdk.service.models.auth.RequestRefreshTokenModel
import com.fitbitsdk.service.models.auth.RequestTokenModel
import retrofit2.Call
import retrofit2.http.*


interface AuthService {
    companion object {
        const val TOKEN_ISSUING_ENDPOINT: String = "oauth2/token"
    }

    @POST(TOKEN_ISSUING_ENDPOINT)
    fun authorizeToken(@Header(AuthEndpoint.OAUTH_HEADER_KEY) basicAuth: String, @Body tokenModel : RequestTokenModel): Call<OAuthAccessToken>

//    @POST(TOKEN_ISSUING_ENDPOINT)
//    fun refreshToken(@Header(AuthEndpoint.OAUTH_HEADER_KEY) basicAuth: String, @Body refreshTokenModel : RequestRefreshTokenModel): Call<OAuthAccessToken>

    @FormUrlEncoded
    @POST(TOKEN_ISSUING_ENDPOINT)
    fun refreshToken(@Header(AuthEndpoint.OAUTH_HEADER_KEY) basicAuth: String,
                      @Field("grant_type") grantType: String,
                      @Field("refresh_token") refresh_token: String): Call<OAuthAccessToken>

}
