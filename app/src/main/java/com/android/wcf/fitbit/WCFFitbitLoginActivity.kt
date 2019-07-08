package com.android.wcf.fitbit

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.android.wcf.R
import com.fitbitsdk.authentication.AuthenticationHandler
import com.fitbitsdk.authentication.AuthenticationManager
import com.fitbitsdk.authentication.AuthenticationResult
import com.fitbitsdk.service.FitbitService
import com.fitbitsdk.service.models.ActivitySteps
import com.fitbitsdk.service.models.DailyActivitySummary
import com.fitbitsdk.service.models.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WCFFitbitLoginActivity : AppCompatActivity(), AuthenticationHandler {

    companion object{
        val TAG = "FitbitLoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wcf_fitbit_login)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!AuthenticationManager.onActivityResult(requestCode, resultCode, data, this)) {
            // Handle other activity results, if needed
        }
    }
    override fun onResume() {
        super.onResume()
        if (AuthenticationManager.isLoggedIn()) {
            onLoggedIn()
        }else{
            AuthenticationManager.login(this)
        }
    }

    override fun onAuthFinished(authenticationResult: AuthenticationResult?) {
        if (authenticationResult != null) {
            if (authenticationResult.isSuccessful) {
                onLoggedIn()
            } else {
                displayAuthError(authenticationResult)
            }
        }
    }

    fun onLoggedIn(){
        val sharedPreferences = getSharedPreferences("Fitbit", Context.MODE_PRIVATE)
        val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
        fService.getUserService().profile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>?, response: Response<UserProfile>?) {
                Log.d(TAG, "Response: " + response?.body()?.user?.dateOfBirth)
                //getSteps()
                getActivities()
            }

            override fun onFailure(call: Call<UserProfile>?, t: Throwable?) {
                Log.d(TAG, "Error: " + t?.message)
            }

        })
    }

    fun getSteps() {
        val sharedPreferences = getSharedPreferences("Fitbit", Context.MODE_PRIVATE)
        val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
        fService.getActivityService().getActivityStepsForDateRange("2018-08-01", "2018-08-15")
                .enqueue(object : Callback<ActivitySteps> {
                    override fun onResponse(call: Call<ActivitySteps>, response: Response<ActivitySteps>) {
                        val s = response.body()
                        Log.d(TAG, "activity")
                    }

                    override fun onFailure(call: Call<ActivitySteps>, t: Throwable) {
                        Log.e(TAG, "activity error")

                    }
                })
    }

    fun getActivities() {
        val sharedPreferences = getSharedPreferences("Fitbit", Context.MODE_PRIVATE)
        val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
        fService.getActivityService().getDailyActivitySummary( "2018-08-10")
                .enqueue(object : Callback<DailyActivitySummary> {
                    override fun onResponse(call: Call<DailyActivitySummary>, response: Response<DailyActivitySummary>) {
                        val s = response.body()
                        Log.d(TAG, "activity")
                    }

                    override fun onFailure(call: Call<DailyActivitySummary>, t: Throwable) {
                        Log.e(TAG, "activity error")

                    }
                })
    }

    private fun displayAuthError(authenticationResult: AuthenticationResult) {
        var message = ""

        when (authenticationResult.status) {
            AuthenticationResult.Status.dismissed -> message = getString(R.string.login_dismissed)
            AuthenticationResult.Status.error -> message = authenticationResult.errorMessage
            AuthenticationResult.Status.missing_required_scopes -> {
                val missingScopes = authenticationResult.missingScopes
                val missingScopesText = TextUtils.join(", ", missingScopes)
                message = getString(R.string.missing_scopes_error) + missingScopesText
            }
        }

        AlertDialog.Builder(this)
                .setTitle(R.string.login_title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, id -> })
                .create()
                .show()
    }

}
