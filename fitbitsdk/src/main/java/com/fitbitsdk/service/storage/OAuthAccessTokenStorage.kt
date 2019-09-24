package com.fitbitsdk.service.storage

import com.fitbitsdk.service.models.auth.OAuthAccessToken


interface OAuthAccessTokenStorage {
    var token : OAuthAccessToken?
}