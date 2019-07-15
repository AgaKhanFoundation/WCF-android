package com.fitbitsdk.service.models.auth


open class RequestTokenModel(val username: String?, val password: String?, val scope: String, val grant_type: String)