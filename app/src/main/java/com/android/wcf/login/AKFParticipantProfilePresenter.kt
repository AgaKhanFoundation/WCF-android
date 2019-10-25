package com.android.wcf.login

import android.util.Log
import com.android.wcf.home.BasePresenter

class AKFParticipantProfilePresenter(val view: AKFParticipantProfileMvp.View) : BasePresenter(), AKFParticipantProfileMvp.Presenter {

    private val TAG = AKFParticipantProfilePresenter::class.java.simpleName

    override fun updateParticipantProfileRegistered(participantId: String) {
        super.updateParticipantProfileRegistered(participantId)
    }

    override fun onUpdateParticipantProfileRegisteredSuccess(participantId: String) {
        Log.d(TAG, "onUpdateParticipantProfileRegisteredSuccess: participantId=$participantId")
        view.akfProfileRegistered()
    }

    override fun onUpdateParticipantProfileRegisteredError(error: Throwable, participantId: String) {
        view.akfProfileRegistrationError(error, participantId)
    }
}
