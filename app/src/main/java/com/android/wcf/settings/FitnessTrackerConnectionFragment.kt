package com.android.wcf.settings

import android.content.Context
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
import com.android.wcf.fitbit.FitbitHelper
import com.fitbitsdk.authentication.AuthenticationManager
import com.fitbitsdk.authentication.LogoutTaskCompletionHandler
import com.fitbitsdk.service.FitbitService
import com.fitbitsdk.service.models.Device
import com.fitbitsdk.service.models.UserProfile
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
        var deviceLoggedIn = false
        sharedPreferences?.let {
            deviceLoggedIn = it.getBoolean(FitbitHelper.FITBIT_DEVICE_LOGGED_IN, false)
        }
        if (deviceLoggedIn) {
            getDeviceProfile()
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

                if (deviceChoice) {
                    rbFitnessApp.setChecked(!deviceChoice)
                    btnFitnessApp.setEnabled(!deviceChoice)
                    btnFitnessDevice.setEnabled(deviceChoice)
                    rbFitnessDevice.setChecked(deviceChoice)
                }
                else {
                    rbFitnessApp.setChecked(deviceChoice)
                    btnFitnessApp.setEnabled(deviceChoice)
                    btnFitnessDevice.setEnabled(!deviceChoice)
                    rbFitnessDevice.setChecked(!deviceChoice)
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

                rbFitnessApp.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_SELECTED, false).commit()
                        btnFitnessApp.setEnabled(isChecked)
                        btnFitnessDevice.setEnabled(!isChecked)
                        rbFitnessDevice.setChecked(!isChecked)
                    }
                }

                rbFitnessDevice.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        sharedPreferences.edit().putBoolean(FitbitHelper.FITBIT_DEVICE_SELECTED, true).commit()
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
}
