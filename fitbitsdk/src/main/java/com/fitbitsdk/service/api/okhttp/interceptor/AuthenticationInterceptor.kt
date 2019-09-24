package com.fitbitsdk.service.api.okhttp.interceptor

import com.fitbitsdk.service.api.OAuthDataService
import com.fitbitsdk.service.api.RefreshTokenService
import com.fitbitsdk.service.api.endpoint.AuthEndpoint
import com.fitbitsdk.service.models.auth.OAuthAccessToken
import okhttp3.*
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class AuthenticationInterceptor(val oAuthDataService: OAuthDataService) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //Auth calls should be pre-authed with a `Basic` token
        if(chain.request().url().toString().equals(oAuthDataService.getTokenServiceUrl(), true)) {
            return chain.proceed(chain.request())
        }

        //Check token validity
        val token = oAuthDataService.token ?: return noTokenResponse(chain.request())
        //If valid, attach as Header to request, continue in the chain
        val tokenNeedsRefreshed:Boolean = token.needsRefresh()

        if(!tokenNeedsRefreshed) {
            return proceedInChain(chain, token)
        } else {
            //All active Threads will pause here
            return synchronized(oAuthDataService) {
                val accessToken = oAuthDataService.token
                when {
                    accessToken?.needsRefresh() == false -> return proceedInChain(chain, accessToken)
                    accessToken != null ->
                        return try {
                            proceedInChain(chain, oAuthDataService.refreshTokenWithLatch(accessToken))
                        } catch (error: RefreshTokenService.HttpError) {
                            Response.Builder()
                                    .code(error.code)
                                    .protocol(Protocol.HTTP_1_1)
                                    .body(ResponseBody.create(MediaType.parse(error.contentType), error.errorBody))
                                    .message(error.errorBody)
                                    .request(chain.request())
                                    .build()
                        }
                    else -> return noTokenResponse(chain.request())
                }
            }
        }
    }

    private fun noTokenResponse(request: Request) = Response.Builder()
            .protocol(Protocol.HTTP_1_1)
            .code(HTTP_UNAUTHORIZED)
            .body(ResponseBody.create(MediaType.parse("text/plain"), "No token supplied"))
            .message("No token supplied")
            .request(request)
            .build()

    private fun proceedInChain(chain: Interceptor.Chain, validToken: OAuthAccessToken): Response {
        val request = chain.request()
        val header: Pair<String, String> = AuthEndpoint.bearerToken(validToken)
        val authedRequest = request.newBuilder().addHeader(header.first, header.second).build()
        return chain.proceed(authedRequest)
    }
}