package com.android.wcf.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import com.android.wcf.R
import com.android.wcf.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_device_connection.*

class DeviceConnectionFragment : BaseFragment(), DeviceConnectionMvp.View {

    companion object {
        val TAG = DeviceConnectionFragment::class.java.simpleName
    }

    var host: DeviceConnectionMvp.Host? = null

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
                Log.d(TAG, "btn_connect_to_fitness_app")
            }
            R.id.btn_connect_to_fitbit -> {
                Log.d(TAG, "btn_connect_to_fitbit")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_device_connection, container, false)

        val rbFitnessApp: RadioButton = fragmentView.findViewById(R.id.rb_connect_to_fitness_app)
        val btnFitnessApp: Button = fragmentView.findViewById(R.id.btn_connect_to_fitness_app)

        val rbFitnessFitbit: RadioButton = fragmentView.findViewById(R.id.rb_connect_to_fitbit)
        val btnFitnessFitbit: Button = fragmentView.findViewById(R.id.btn_connect_to_fitbit)

        rbFitnessApp.setOnCheckedChangeListener { buttonView, isChecked ->
            btnFitnessApp.setEnabled(isChecked)
            btnFitnessFitbit.setEnabled(!isChecked)

            rbFitnessFitbit.setChecked(!isChecked)
        }

        rbFitnessFitbit.setOnCheckedChangeListener { buttonView, isChecked ->
            btnFitnessFitbit.setEnabled(isChecked)
            btnFitnessApp.setEnabled(!isChecked)

            rbFitnessApp.setChecked(!isChecked)
        }

        val expandImage: ImageView = fragmentView.findViewById(R.id.iv_device_message_expand)
        val fitnessAppButton: Button = fragmentView.findViewById(R.id.btn_connect_to_fitness_app)
        val fitnessDeviceButton: Button = fragmentView.findViewById(R.id.btn_connect_to_fitbit)

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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is DeviceConnectionMvp.Host) {
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
}