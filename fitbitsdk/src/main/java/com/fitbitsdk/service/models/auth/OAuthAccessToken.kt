package com.fitbitsdk.service.models.auth

import android.os.Parcel
import android.os.Parcelable
import com.fitbitsdk.authentication.Scope
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class OAuthAccessToken(
        var access_token: String?,
        var expires_in: Int?,
        var refresh_token: String?,
        var scopes: List<Scope>?,
        var token_type: String?,
        var user_id: String?) : Parcelable{

    var expiration: Calendar

    private companion object : Parceler<OAuthAccessToken> {
        override fun OAuthAccessToken.write(parcel: Parcel, flags: Int) {
            parcel.writeString(access_token)
            parcel.writeInt(expires_in ?: 0)
            parcel.writeString(refresh_token)
            parcel.writeString(scopes?.joinToString(","){ it.name } ?: "")
            parcel.writeString(token_type)
            parcel.writeString(user_id)
            parcel.writeLong(expiration.timeInMillis)
        }

        override fun create(parcel: Parcel): OAuthAccessToken {
            val token = OAuthAccessToken(parcel.readString(), parcel.readInt(), parcel.readString(), parseScopes(parcel.readString()), parcel.readString(), parcel.readString())
            val expirationDate = Calendar.getInstance()
            expirationDate.timeInMillis = parcel.readLong()
            token.expiration = expirationDate
            return token
        }

        private fun parseScopes(scopes: String): List<Scope> {
            val scopesArray = scopes.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val scopesList = ArrayList<Scope>()
            for (scopeStr in scopesArray) {
                val scope = Scope.fromString(scopeStr)
                if (scope != null) {
                    scopesList.add(scope)
                }
            }

            return scopesList
        }


    }

    init {
        this.expiration = expires_in?.let {
            Calendar.getInstance().apply {
                this.add(Calendar.SECOND, it)
            }
        } ?: Calendar.getInstance()
    }

    fun needsRefresh(): Boolean {
        //Force a refresh up to 5 minutes before the expiration time
        val windowPeriod = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }

        return expiration.before(windowPeriod)
    }

}