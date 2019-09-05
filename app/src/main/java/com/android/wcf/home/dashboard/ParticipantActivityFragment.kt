package com.android.wcf.home.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.wcf.R
import com.android.wcf.base.BaseFragment

class ParticipantActivityFragment: BaseFragment() {

    companion object {
        public val TAG = ParticipantActivityFragment.javaClass.simpleName
        val  ACTIVITY_TYPE_ARG = "activity_type"

        val  ACTIVITY_TYPE_DAILY = 0
        val  ACTIVITY_TYPE_WEEKLY = 1


        fun instanceDaily(): ParticipantActivityFragment {
            val args = Bundle()
            args.putInt(ACTIVITY_TYPE_ARG, ACTIVITY_TYPE_DAILY)
            val fragment = ParticipantActivityFragment()
            fragment.arguments = args
            return fragment
        }

        fun instanceWeekly(): ParticipantActivityFragment {
            val args = Bundle()
            args.putInt(ACTIVITY_TYPE_ARG, ACTIVITY_TYPE_WEEKLY)
            val fragment = ParticipantActivityFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_participant_tracked_activity, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView();
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    fun setupView() {

    }

}
