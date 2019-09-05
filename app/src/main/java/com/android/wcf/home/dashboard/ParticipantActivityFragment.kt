package com.android.wcf.home.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.wcf.R
import com.android.wcf.base.BaseFragment

class ParticipantActivityFragment: BaseFragment() {

    companion object {
        val  ACTIVITY_TYPE_ARG = "activity_type"

        val  ACTIVITY_TYPE_DAILY = 0
        val  ACTIVITY_TYPE_WEEKLY = 1

        fun instance(type:Int): Fragment {
            val args =  Bundle()
            if (type == ACTIVITY_TYPE_DAILY) {
                args.putInt(ACTIVITY_TYPE_ARG, ACTIVITY_TYPE_DAILY)
            }
            else {
                args.putInt(ACTIVITY_TYPE_ARG, ACTIVITY_TYPE_WEEKLY)
            }
            val fragment = ParticipantActivityFragment()
            fragment.arguments = args
            return fragment
        }

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

    fun setupView() {

    }

}