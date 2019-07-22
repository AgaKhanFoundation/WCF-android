package com.android.wcf.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import com.android.wcf.R
import com.android.wcf.base.BaseFragment
import com.android.wcf.base.RequestCodes
import com.android.wcf.fitbit.FitbitHelper
import com.android.wcf.googlefit.GoogleFitHelper
import com.fitbitsdk.authentication.AuthenticationManager
import com.fitbitsdk.authentication.LogoutTaskCompletionHandler
import com.fitbitsdk.service.FitbitService
import com.fitbitsdk.service.models.Device
import com.fitbitsdk.service.models.UserProfile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import kotlinx.android.synthetic.main.fragment_device_connection.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FitnessTrackerConnectionFragment : BaseFragment(), FitnessTrackerConnectionMvp.View {

    internal var sharedPreferences: SharedPreferences? = null

    companion object {
        val TAG = FitnessTrackerConnectionFragment::class.java.simpleName
        val TAG_DISCONNECT = "1"
        val TAG_CONNECT = "0"
    }

    var host: FitnessTrackerConnectionMvp.Host? = null

    var onClickListener: View.OnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.iv_device_message_expand ->
                if (other_device_message_long.visibility == View.VISIBLE) {
                    other_device_message_long.visibility = View.GONE
                    iv_device_message_expand.setImageResource(R.drawable.ic_chevron_down)
                } else {
                    other_device_message_long.visibility = View.VISIBLE
                    iv_device_message_expand.setImageResource(R.drawable.ic_chevron_up)
                }
            R.id.btn_connect_to_fitness_app -> {
                Log.i(TAG, "btn_connect_to_fitness_app")
                if (view.tag == TAG_DISCONNECT) {
                   disconnectAppFromGoogleFit()
                }
                else {
                    connectAppToGoogleFit()
                }
            }
            R.id.btn_connect_to_fitness_device -> {
                Log.i(TAG, "btn_connect_to_fitness_device")
                if (view.tag == TAG_DISCONNECT) {
                    disconnectAppFromFitbit()
                }
                else {
                    connectAppToFitbit()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        sharedPreferences = activity?.getSharedPreferences(FitbitHelper.FITBIT_SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val fragmentView = inflater.inflate(R.layout.fragment_device_connection, container, false)

        val expandImage: ImageView = fragmentView.findViewById(R.id.iv_device_message_expand)
        val fitnessAppButton: Button = fragmentView.findViewById(R.id.btn_connect_to_fitness_app)
        val fitnessDeviceButton: Button = fragmentView.findViewById(R.id.btn_connect_to_fitness_device)

        expandImage.setOnClickListener(onClickListener)
        fitnessAppButton.setOnClickListener(onClickListener)
        fitnessDeviceButton.setOnClickListener(onClickListener)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        host?.setToolbarTitle(getString(R.string.settings_connect_device_title))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
        var fitnessDeviceLoggedIn = false
        var fitnessAppLoggedIn = false

        sharedPreferences?.let {
            fitnessDeviceLoggedIn = it.getBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false)
            fitnessAppLoggedIn = it.getBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, false)
        }
        if (fitnessDeviceLoggedIn) {
            getDeviceProfile()
        }
        else if(fitnessAppLoggedIn){
            updateDeviceInfoView() //fake it to update view
        }
        else {
            updateDeviceInfoView() //fake it to update view
        }
    }

    override fun onAttach(context: Context?) {
        Log.i(TAG, "onAttach")
        super.onAttach(context)
        if (context is FitnessTrackerConnectionMvp.Host) {
            this.host = context
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                activity!!.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RequestCodes.GFIT_PERMISSIONS_REQUEST_CODE -> {
                run {
                    //TODO: make a GoogleFitAuthManager similar to Fitbit's
                    if (data != null) {
                        onActivityResultForGoogleFit(requestCode, resultCode, data)
                    }

                }
                Log.e(TAG, "Unhandled Request Code $requestCode")
            }
            else -> Log.e(TAG, "Unhandled Request Code $requestCode")
        }
    }


    /*********** Fitbit related methods *************/
    protected fun onLoggedIn() {
        Log.i(TAG, "onLoggedIn")
        getUserProfile()
        getDeviceProfile()
    }

    private fun updateDeviceInfoView() {
        view?.let { fragmentView ->
            val rbFitnessApp: RadioButton = fragmentView.findViewById(R.id.rb_connect_to_fitness_app)
            val btnFitnessApp: Button = fragmentView.findViewById(R.id.btn_connect_to_fitness_app)

            val rbFitnessDevice: RadioButton = fragmentView.findViewById(R.id.rb_connect_to_fitness_device)
            val btnFitnessDevice: Button = fragmentView.findViewById(R.id.btn_connect_to_fitness_device)
            val tvFitnessDeviceInfo: TextView = fragmentView.findViewById(R.id.tv_fitness_device_info)
            val tvFitnessDeviceUser: TextView = fragmentView.findViewById(R.id.tv_fitness_device_user)

            sharedPreferences?.let { sharedPreferences ->
                val deviceChoice = sharedPreferences.getBoolean(FitbitHelper.FITBIT_DEVICE_SELECTED, false)
                val deviceLoggedIn = sharedPreferences.getBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false)

                val appChoice = sharedPreferences.getBoolean(GoogleFitHelper.GOOGLE_FIT_APP_SELECTED, false)
                val appLoggedIn = sharedPreferences.getBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, false)

                if (deviceChoice) {
                    btnFitnessDevice.setEnabled(deviceChoice)
                    rbFitnessDevice.setChecked(deviceChoice)
                    rbFitnessApp.setChecked(!deviceChoice)
                    btnFitnessApp.setEnabled(!deviceChoice)
                }
                else if(appChoice) {
                    rbFitnessApp.setChecked(appChoice)
                    btnFitnessApp.setEnabled(appChoice)
                    btnFitnessDevice.setEnabled(!appChoice)
                    rbFitnessDevice.setChecked(!appChoice)
                }

                if (deviceLoggedIn) {
                    btnFitnessDevice.text = getText(R.string.settings_fitness_device_disconnect_text)
                    btnFitnessDevice.tag = TAG_DISCONNECT

                    var htmlAsString = sharedPreferences.getString(FitbitHelper.FITBIT_DEVICE_INFO, "")
                    htmlAsString = "<html><body>" + htmlAsString + "</body></html>"
                    val htmlAsSpanned: Spanned = Html.fromHtml(htmlAsString)
                    tvFitnessDeviceInfo.text = htmlAsSpanned

                    tvFitnessDeviceUser.text = sharedPreferences.getString(FitbitHelper.FITBIT_USER_DISPLAY_NAME, "")
                    tvFitnessDeviceUser.visibility = View.VISIBLE
                } else {
                    btnFitnessDevice.text = getText(R.string.settings_fitness_device_connect_text)
                    btnFitnessDevice.tag = TAG_CONNECT

                    tvFitnessDeviceInfo.text = getString(R.string.settings_connect_device_fitness_device_message)
                    tvFitnessDeviceUser.text = ""
                    tvFitnessDeviceUser.visibility = View.GONE
                }

                if (appLoggedIn) {
                    btnFitnessApp.text = getText(R.string.settings_device_fitness_app_disconnect_text)
                    btnFitnessApp.tag = TAG_DISCONNECT

                } else {
                    btnFitnessApp.text = getText(R.string.settings_device_fitness_app_connect_text)
                    btnFitnessApp.tag = TAG_CONNECT

                }
                rbFitnessApp.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        sharedPreferences.edit().putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_SELECTED, true).commit()
                        sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_SELECTED, false).commit()
                        btnFitnessApp.setEnabled(isChecked)
                        btnFitnessDevice.setEnabled(!isChecked)
                        rbFitnessDevice.setChecked(!isChecked)
                    }
                }

                rbFitnessDevice.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_SELECTED, true).commit()
                        sharedPreferences.edit().putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_SELECTED, false).commit()
                        btnFitnessDevice.setEnabled(isChecked)
                        btnFitnessApp.setEnabled(!isChecked)
                        rbFitnessApp.setChecked(!isChecked)
                    }
                }

            }
        }
    }

    fun connectAppToFitbit() {
        Log.i(TAG, "connectAppToFitbit")
        AuthenticationManager.login(activity)
    }

    fun disconnectAppFromFitbit() {
        Log.i(TAG, "disconnectAppFromFitbit")
        AuthenticationManager.logout(activity, fitbitLogoutTaskCompletionHandler)
    }

    private val fitbitLogoutTaskCompletionHandler = object : LogoutTaskCompletionHandler {
        override fun logoutSuccess() {
            Log.i(TAG, "fitbitLogoutTaskCompletionHandler: logoutSuccess")
            onFitbitLogoutSuccess()
        }

        override fun logoutError(message: String) {
            Log.i(TAG, "fitbitLogoutTaskCompletionHandler: logoutError")
            onFitbitLogoutError(message)

        }
    }

    protected fun onFitbitLogoutSuccess() {
        Log.i(TAG, "onFitbitLogoutSuccess")
        sharedPreferences?.edit()?.putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false)?.commit()
        onLoggedIn()
    }

    protected fun onFitbitLogoutError(message: String) {
        Log.i(TAG, "onFitbitLogoutError")
        sharedPreferences?.edit()?.putBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false)?.commit()
        updateDeviceInfoView()
    }

    private fun getUserProfile() {
        Log.i(TAG, "getUserProfile")
        sharedPreferences?.let { sharedPreferences ->
            val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
            fService.getUserService().profile().enqueue(object : Callback<UserProfile> {
                override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                    val userProfile = response.body()
                    var displayName: String = ""
                    userProfile?.let {
                        val user = it.user
                        Log.i(TAG, "User Response: " +
                                user.displayName + " stride: " + user.strideLengthWalking + " " + user.distanceUnit)
                        displayName = user.displayName
                    }

                    sharedPreferences.edit().putString(FitbitHelper.FITBIT_USER_DISPLAY_NAME, displayName).commit()
                }

                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    Log.e(TAG, "Error: " + t.message)
                }
            })
        }
    }

    private fun getDeviceProfile() {
        Log.i(TAG, "getDeviceProfile")
        sharedPreferences?.let { sharedPreferences ->
            val fService = FitbitService(sharedPreferences, AuthenticationManager.getAuthenticationConfiguration().clientCredentials)
            fService.getDeviceService().devices().enqueue(object : Callback<Array<Device>> {
                override fun onResponse(call: Call<Array<Device>>, response: Response<Array<Device>>) {
                    val devices = response.body()

                    val stringBuilder = StringBuilder()

                    devices?.let { devices ->
                        for (device in devices!!) {

                            Log.i(TAG, "Device Response: " +
                                    "Type: " + device.type + "\n" +
                                    "version: " + device.deviceVersion + "\n" +
                                    "last sync at : " + device.lastSyncTime)

                            stringBuilder.append("<b>FITBIT ")
                            stringBuilder.append(device.deviceVersion.toUpperCase())
                            stringBuilder.append("&trade;</b><br>")

                            stringBuilder.append("<b>Last Sync: </b>")
                            stringBuilder.append(device.lastSyncTime)
                            stringBuilder.append("<br><br>")
                        }

                        if (stringBuilder.length > 0) {
                            stringBuilder.replace(stringBuilder.length - 8, stringBuilder.length, "")
                            Log.i(TAG, "Device info: $stringBuilder")
                        }
                    }
                    sharedPreferences.edit().putString(FitbitHelper.FITBIT_DEVICE_INFO, stringBuilder.toString()).commit()
                    updateDeviceInfoView()
                }

                override fun onFailure(call: Call<Array<Device>>, t: Throwable) {
                    Log.e(TAG, "Device api Error: " + t.message)
                    updateDeviceInfoView()
                }
            })
        }
    }

    /*********** End of Fitbit related methods *************/

    /************* Google Fit related methods    */
     fun connectAppToGoogleFit() {
        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build()

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    RequestCodes.GFIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions)
        } else {
            onGoogleFitLoggedIn()
        }
    }

    protected fun disconnectAppFromGoogleFit1() {
        val lastSignIn = GoogleSignIn.getLastSignedInAccount(activity)
        lastSignIn?.let {
            Fitness.getConfigClient(activity as Activity, it).disableFit()
        }
        sharedPreferences?.edit()?.putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, false)?.commit()
        onGoogleFitLogoutSuccess()
    }

    protected fun disconnectAppFromGoogleFit() {
        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build()

        val signInOptions = GoogleSignInOptions.Builder().addExtension(fitnessOptions).build()
        context?.let { context ->
            val client = GoogleSignIn.getClient(context, signInOptions)
            client.revokeAccess()
        }

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)

        if (googleSignInAccount != null) {
            val configClient = Fitness.getConfigClient(activity!!, googleSignInAccount)
            configClient?.disableFit()
        }

        sharedPreferences?.edit()?.putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, false)?.commit()
        onGoogleFitLogoutSuccess()
    }

    protected fun onActivityResultForGoogleFit(requestCode: Int, resultCode: Int, data: Intent) {
        Log.i(TAG, "onActivityResultForGoogleFit")
        sharedPreferences?.let { sharedPreferences ->
            if (resultCode == Activity.RESULT_OK) {
                sharedPreferences.edit().putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, true).commit()
                onGoogleFitLoggedIn()
            } else {
                sharedPreferences.edit().putBoolean(GoogleFitHelper.GOOGLE_FIT_APP_LOGGED_IN, false).commit()
            }
        }
    }

    protected fun onGoogleFitLoggedIn() {
        Log.i(TAG, "onGoogleFitLoggedIn")

       val signedInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        val username = signedInAccount?.displayName
        val email = signedInAccount?.email
        onLoggedIn()
    }


    protected fun onGoogleFitLogoutSuccess() {
        Log.i(TAG, "onGoogleFitLogoutSuccess")
        onLoggedIn()
    }

    protected fun onGoogleFitLogoutError() {
        Log.i(TAG, "onGoogleFitLogoutError")
    }
}
