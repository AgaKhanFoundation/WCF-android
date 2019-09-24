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
import com.android.wcf.tracker.TrackingHelper
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
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_device_connection.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

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
                    iv_device_message_expand.setImageResource(R.drawable.ic_chevron_down_blue)
                } else {
                    other_device_message_long.visibility = View.VISIBLE
                    iv_device_message_expand.setImageResource(R.drawable.ic_chevron_up_blue)
                }
            R.id.btn_connect_to_fitness_app -> {
                Log.i(TAG, "btn_connect_to_fitness_app")
                if (view.tag == TAG_DISCONNECT) {
                    disconnectAppFromGoogleFit()
                } else {
                    connectAppToGoogleFit()
                }
            }
            R.id.btn_connect_to_fitness_device -> {
                Log.i(TAG, "btn_connect_to_fitness_device")
                if (view.tag == TAG_DISCONNECT) {
                    disconnectAppFromFitbit()
                } else {
                    connectAppToFitbit()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        sharedPreferences = activity?.getSharedPreferences(TrackingHelper.TRACKER_SHARED_PREF_NAME, Context.MODE_PRIVATE)
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
        host?.setToolbarTitle(getString(R.string.settings_connect_device_title), true)
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
        var trackingSourceId = 0;

        sharedPreferences?.let {
            fitnessDeviceLoggedIn = it.getBoolean(TrackingHelper.FITBIT_DEVICE_LOGGED_IN, false)
            fitnessAppLoggedIn = it.getBoolean(TrackingHelper.GOOGLE_FIT_APP_LOGGED_IN, false)
            trackingSourceId = it.getInt(TrackingHelper.SELECTED_TRACKING_SOURCE_ID, 0)
        }
        if (fitnessDeviceLoggedIn) {
            getDeviceProfile()
        } else if (fitnessAppLoggedIn) {
            updateConnectionInfoView() //fake it to update view
        } else {
            updateConnectionInfoView() //fake it to update view
        }
    }

    override fun onAttach(context: Context) {
        Log.i(TAG, "onAttach")
        super.onAttach(context)
        if (context is FitnessTrackerConnectionMvp.Host) {
            this.host = context
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
    protected fun onFitbitLoggedIn() {
        Log.i(TAG, "onFitbitLoggedIn")
        getUserProfile()
        getDeviceProfile()
    }

    private fun updateConnectionInfoView() {
        view?.let { fragmentView ->
            val rbFitnessApp: RadioButton = fragmentView.findViewById(R.id.rb_connect_to_fitness_app)
            val btnFitnessApp: Button = fragmentView.findViewById(R.id.btn_connect_to_fitness_app)

            val rbFitnessDevice: RadioButton = fragmentView.findViewById(R.id.rb_connect_to_fitness_device)
            val btnFitnessDevice: Button = fragmentView.findViewById(R.id.btn_connect_to_fitness_device)
            val tvFitnessDeviceInfo: TextView = fragmentView.findViewById(R.id.tv_fitness_device_info)
            val tvFitnessDeviceUser: TextView = fragmentView.findViewById(R.id.tv_fitness_device_user)
            val tvFitnessAappMessage:TextView = fragmentView.findViewById(R.id.tv_fitness_app_message)

            sharedPreferences?.let { sharedPreferences ->
                var deviceChoice = sharedPreferences.getBoolean(TrackingHelper.FITBIT_DEVICE_SELECTED, false)
                val deviceLoggedIn = sharedPreferences.getBoolean(TrackingHelper.FITBIT_DEVICE_LOGGED_IN, false)

                var appChoice = sharedPreferences.getBoolean(TrackingHelper.GOOGLE_FIT_APP_SELECTED, false)
                val appLoggedIn = sharedPreferences.getBoolean(TrackingHelper.GOOGLE_FIT_APP_LOGGED_IN, false)

                if (deviceLoggedIn) {
                    deviceChoice = true;
                    appChoice = false;
                }
                else if (appLoggedIn) {
                    deviceChoice = false;
                    appChoice = true;
                }

                if (deviceChoice) {
                    btnFitnessDevice.setEnabled(deviceChoice)
                    rbFitnessDevice.setChecked(deviceChoice)
                    rbFitnessApp.setChecked(!deviceChoice)
                    btnFitnessApp.setEnabled(!deviceChoice)
                } else if (appChoice) {
                    rbFitnessApp.setChecked(appChoice)
                    btnFitnessApp.setEnabled(appChoice)
                    btnFitnessDevice.setEnabled(!appChoice)
                    rbFitnessDevice.setChecked(!appChoice)
                }
                else {
                    btnFitnessApp.setEnabled(false)
                    btnFitnessDevice.setEnabled(false)
                }

                if (deviceLoggedIn) {
                    btnFitnessDevice.text = getText(R.string.settings_fitness_device_disconnect_label)
                    btnFitnessDevice.tag = TAG_DISCONNECT

                    var htmlAsString = sharedPreferences.getString(TrackingHelper.FITBIT_DEVICE_INFO, "")
                    htmlAsString = "<html><body>" + htmlAsString + "</body></html>"
                    val htmlAsSpanned: Spanned = Html.fromHtml(htmlAsString)
                    tvFitnessDeviceInfo.text = htmlAsSpanned

                    tvFitnessDeviceUser.text = sharedPreferences.getString(TrackingHelper.FITBIT_USER_DISPLAY_NAME, "")
                    tvFitnessDeviceUser.visibility = View.VISIBLE
                } else {
                    btnFitnessDevice.text = getText(R.string.settings_fitness_device_connect_label)
                    btnFitnessDevice.tag = TAG_CONNECT

                    tvFitnessDeviceInfo.text = getString(R.string.settings_fitness_device_connect_message)
                    tvFitnessDeviceUser.text = ""
                    tvFitnessDeviceUser.visibility = View.GONE
                }

                if (appLoggedIn) {
                    btnFitnessApp.text = getText(R.string.settings_fitness_app_disconnect_label)
                    btnFitnessApp.tag = TAG_DISCONNECT
                    val googleFitUsername = sharedPreferences.getString(TrackingHelper.GOOGLE_FIT_USER_DISPLAY_NAME, null)
                    val googleFitEmail = sharedPreferences.getString(TrackingHelper.GOOGLE_FIT_USER_DISPLAY_EMAIL, "")
                    googleFitUsername?.let{
                        tvFitnessAappMessage.text = it + " " + googleFitEmail
                    } ?: run {
                        tvFitnessAappMessage.visibility = View.GONE
                    }

                } else {
                    btnFitnessApp.text = getText(R.string.settings_fitness_app_connect_label)
                    btnFitnessApp.tag = TAG_CONNECT
                    tvFitnessAappMessage.text = getString(R.string.settings_fitness_app_connect_message)

                }
                rbFitnessApp.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        sharedPreferences.edit().putBoolean(TrackingHelper.GOOGLE_FIT_APP_SELECTED, true).commit()
                        sharedPreferences.edit().putBoolean(TrackingHelper.FITBIT_DEVICE_SELECTED, false).commit()
                        sharedPreferences.edit().putInt(TrackingHelper.SELECTED_TRACKING_SOURCE_ID, TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID).commit()

                        btnFitnessApp.setEnabled(isChecked)
                        btnFitnessDevice.setEnabled(!isChecked)
                        rbFitnessDevice.setChecked(!isChecked)
                    }
                }

                rbFitnessDevice.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        sharedPreferences.edit().putBoolean(TrackingHelper.FITBIT_DEVICE_SELECTED, true).commit()
                        sharedPreferences.edit().putBoolean(TrackingHelper.GOOGLE_FIT_APP_SELECTED, false).commit()
                        sharedPreferences.edit().putInt(TrackingHelper.SELECTED_TRACKING_SOURCE_ID, TrackingHelper.FITBIT_TRACKING_SOURCE_ID).commit()
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
        sharedPreferences?.edit()?.putBoolean(TrackingHelper.FITBIT_DEVICE_LOGGED_IN, false)?.commit()
        sharedPreferences?.edit()?.putInt(TrackingHelper.SELECTED_TRACKING_SOURCE_ID, 0)?.commit()

        updateConnectionInfoView()
    }

    protected fun onFitbitLogoutError(message: String) {
        Log.i(TAG, "onFitbitLogoutError")
        sharedPreferences?.edit()?.putBoolean(TrackingHelper.FITBIT_DEVICE_LOGGED_IN, false)?.commit()
        sharedPreferences?.edit()?.putInt(TrackingHelper.SELECTED_TRACKING_SOURCE_ID, 0)?.commit()
        updateConnectionInfoView()
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

                    sharedPreferences.edit().putString(TrackingHelper.FITBIT_USER_DISPLAY_NAME, displayName).commit()
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
                    sharedPreferences.edit().putString(TrackingHelper.FITBIT_DEVICE_INFO, stringBuilder.toString()).commit()
                    updateConnectionInfoView()
                }

                override fun onFailure(call: Call<Array<Device>>, t: Throwable) {
                    Log.e(TAG, "Device api Error: " + t.message)
                    updateConnectionInfoView()
                }
            })
        }
    }

    /*********** End of Fitbit related methods *************/

    /************* Google Fit related methods    */

    val googleFitFitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()

    val googleFitSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .addExtension(googleFitFitnessOptions).build()

    fun connectAppToGoogleFit() {
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), googleFitFitnessOptions)) {

            context?.let { context ->
                val client = GoogleSignIn.getClient(context, googleFitSignInOptions) //
                val intent = client.getSignInIntent()
                startActivityForResult(intent, RequestCodes.GFIT_PERMISSIONS_REQUEST_CODE);
            }
        } else {
            onGoogleFitAuthComplete()
        }
    }

    protected fun disconnectAppFromGoogleFit() {
        context?.let { context ->
            val client = GoogleSignIn.getClient(context, googleFitSignInOptions)
            client.revokeAccess()
        }

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)

        if (googleSignInAccount != null) {
            val configClient = Fitness.getConfigClient(activity!!, googleSignInAccount)
            configClient?.disableFit()
        }

        sharedPreferences?.edit()?.putBoolean(TrackingHelper.GOOGLE_FIT_APP_LOGGED_IN, false)?.commit()
        sharedPreferences?.edit()?.putBoolean(TrackingHelper.GOOGLE_FIT_APP_LOGGED_IN, false)?.commit()
        sharedPreferences?.edit()?.remove(TrackingHelper.GOOGLE_FIT_USER_DISPLAY_NAME)?.commit()
        sharedPreferences?.edit()?.remove(TrackingHelper.GOOGLE_FIT_USER_DISPLAY_EMAIL)?.commit()

        onGoogleFitAuthComplete()
    }

    protected fun onActivityResultForGoogleFit(requestCode: Int, resultCode: Int, data: Intent) {
        Log.i(TAG, "onActivityResultForGoogleFit")
        sharedPreferences?.let { sharedPreferences ->
            if (resultCode == Activity.RESULT_OK) {
                sharedPreferences.edit().putBoolean(TrackingHelper.GOOGLE_FIT_APP_LOGGED_IN, true).commit()
            } else {
                sharedPreferences.edit().putBoolean(TrackingHelper.GOOGLE_FIT_APP_LOGGED_IN, false).commit()
            }
        }
        onGoogleFitAuthComplete()
    }

    protected fun onGoogleFitAuthComplete() {
        Log.i(TAG, "onGoogleFitAuthComplete")

        val signedInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        val displayName = signedInAccount?.displayName
        val email = signedInAccount?.email
        displayName?.let {
            sharedPreferences?.edit()?.putString(TrackingHelper.GOOGLE_FIT_USER_DISPLAY_NAME, displayName)?.commit()
            sharedPreferences?.edit()?.putString(TrackingHelper.GOOGLE_FIT_USER_DISPLAY_EMAIL, email)?.commit()

            readStepsCountForWeek()
        } ?: run {
            sharedPreferences?.edit()?.remove(TrackingHelper.GOOGLE_FIT_USER_DISPLAY_NAME)?.commit()
            sharedPreferences?.edit()?.remove(TrackingHelper.GOOGLE_FIT_USER_DISPLAY_EMAIL)?.commit()
        }
        updateConnectionInfoView()
    }

    private fun readStepsCountForWeek() {
        val cal = Calendar.getInstance()
        cal.time = Date()
        val endTime = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = cal.timeInMillis

        //TODO: try this, looks like it waits for results to come
        //from: /Users/sultan/Documents/projects/samples/sample_android/sensors/Corey/app/src/main/java/at/shockbytes/corey/data/body/GoogleFitBodyRepository.kt
        //        GoogleSignInOptionsExtension fitnessOptions =
        //                FitnessOptions.builder()
        //                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        //                        .build();
        //        GoogleSignInAccount gsa = GoogleSignIn.getAccountForExtension(this, fitnessOptions);
        //
        //        Date now = new Date();
        //        Task<DataReadResponse> response = Fitness.getHistoryClient(this, gsa)
        //                .readData(new DataReadRequest.Builder()
        //                        .read(DataType.TYPE_STEP_COUNT_DELTA)
        //                        .setTimeRange(now.getTime() - 7*24*60*60*1000, now.getTime(), TimeUnit.MILLISECONDS)
        //                        .build());
        //
        //        DataReadResult readDataResult = Tasks.await(response);
        //        DataSet dataSet = readDataResult.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);


        val readRequest = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()

        GoogleSignIn.getLastSignedInAccount(activity)?.let {
            Fitness.getHistoryClient(activity as Activity, it).readData(readRequest)
                    .addOnSuccessListener { dataReadResponse ->
                        val buckets = dataReadResponse.buckets

                        var totalSteps = 0

                        for (bucket in buckets) {

                            val dataSets = bucket.dataSets

                            for (dataSet in dataSets) {
                                // if (dataSet.getDataType() == DataType.TYPE_STEP_COUNT_DELTA) {

                                for (dp in dataSet.dataPoints) {
                                    for (field in dp.dataType.fields) {
                                        val steps = dp.getValue(field).asInt()
                                        totalSteps += steps
                                    }
                                }
                            }
                            // }
                        }
                        Log.i(TAG, "total steps for this week: $totalSteps")
                    }
                    .addOnCompleteListener(object : OnCompleteListener<DataReadResponse> {
                        override fun onComplete(task: Task<DataReadResponse>) {
                            val buckets = (task.result as DataReadResponse).buckets

                            var totalSteps = 0

                            for (bucket in buckets) {

                                val dataSets = bucket.dataSets

                                for (dataSet in dataSets) {
                                    //                                if (dataSet.getDataType() == DataType.TYPE_STEP_COUNT_DELTA) {


                                    for (dp in dataSet.dataPoints) {
                                        for (field in dp.dataType.fields) {
                                            val steps = dp.getValue(field).asInt()
                                            totalSteps += steps
                                        }
                                    }
                                }
                                //                            }
                            }
                            Log.i(TAG, "total steps for this week: $totalSteps")
                        }
                    })
        }
    }

}
