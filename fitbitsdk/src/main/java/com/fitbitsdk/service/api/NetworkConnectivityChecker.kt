package com.fitbitsdk.service.api


interface NetworkConnectivityChecker {
    fun isConnected(): Boolean
}