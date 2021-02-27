package com.android.wcf.home

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.wcf.R
import com.android.wcf.application.WCFApplication
import com.android.wcf.base.BaseActivity
import com.android.wcf.base.ErrorDialogCallback
import com.android.wcf.helper.SharedPreferencesUtil
import com.android.wcf.home.challenge.*
import com.android.wcf.home.challenge.journey.JourneyFragment
import com.android.wcf.home.challenge.journey.JourneyMvp
import com.android.wcf.home.dashboard.*
import com.android.wcf.home.leaderboard.LeaderboardFragment
import com.android.wcf.home.leaderboard.LeaderboardMvp
import com.android.wcf.home.notifications.NotificationsFragment
import com.android.wcf.home.notifications.NotificationsMvp
import com.android.wcf.login.AKFParticipantProfileFragment
import com.android.wcf.login.AKFParticipantProfileMvp
import com.android.wcf.login.LoginActivity
import com.android.wcf.login.LoginHelper
import com.android.wcf.model.*
import com.android.wcf.settings.SettingsActivity
import com.android.wcf.tracker.TrackerLoginStatusCallback
import com.android.wcf.tracker.TrackingHelper
import com.android.wcf.tracker.fitbit.FitbitHelper
import com.android.wcf.tracker.googlefit.GoogleFitHelper
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import java.io.IOException

class HomeActivity : BaseActivity()
        , HomeMvp.HomeView
        , DashboardMvp.Host
        , ChallengeMvp.Host
        , CreateTeamMvp.Host
        , JoinTeamMvp.Host
        , TeamChallengeProgressMvp.Host
        , SupportsInviteMvp.Host
        , LeaderboardMvp.Host
        , NotificationsMvp.Host
        , AKFParticipantProfileMvp.Host
        , BadgesMvp.Host
        , BadgeDetailMvp.Host
        , JourneyMvp.Host {

    private lateinit var homePresenter: HomeMvp.HomePresenter
    private var dashboardFragment: DashboardFragment? = null
    private var challengeFragment: ChallengeFragment? = null
    private var leaderboardFragment: LeaderboardFragment? = null
    private var notificationsFragment: NotificationsFragment? = null
    private var toolbar: Toolbar? = null

    private val networkErrorDialogCallback = object : ErrorDialogCallback {
        override fun onOk() {
            checkConnections()
        }
    }

    private var myActiveEventId: Int = 0
    private var myTeamId: Int = 0
    private var myParticipantId: String? = null

    private var currentNavigationId: Int = 0

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (currentNavigationId == item.itemId) {
            return@OnNavigationItemSelectedListener false
        }

        when (item.itemId) {
            R.id.nav_dashboard -> {
                if (dashboardFragment != null) {
                    loadFragment(dashboardFragment, R.id.nav_dashboard)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_challenge -> {
                if (challengeFragment != null) {
                    loadFragment(challengeFragment, R.id.nav_challenge)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_leaderboard -> {
                if (leaderboardFragment != null) {
                    loadFragment(leaderboardFragment, R.id.nav_leaderboard)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                if (notificationsFragment != null) {
                    loadFragment(notificationsFragment, R.id.nav_notifications)
                }
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initializeLoadingProgressView("onCreate")

        myParticipantId = SharedPreferencesUtil.getMyParticipantId()
        myActiveEventId = SharedPreferencesUtil.getMyActiveEventId()
        myTeamId = SharedPreferencesUtil.getMyTeamId()

        homePresenter = HomePresenter(this)

        setupView()
    }

    override fun onStart() {
        super.onStart()

        checkConnections()
    }

    private fun checkConnections() {
        if (!isNetworkConnected) {
            showNoNetworkMessage()
            return
        }

        if (myActiveEventId < 1) {
            noActiveEventFound()
            return
        }

        if (!isLoginValid()) {
            Log.d(TAG, "login not valid")
            askToRelogin()
            return
        }

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Firebase getInstanceId failed", task.exception)
                    }
                    // Get new Instance ID token
                    val token = task.result?.token
                    Log.d(TAG, "Firebase token=$token")
                    token?.let {
                        //register token with backend
                    }
                    afterFirebaseConnection()
                })
    }

    private fun afterFirebaseConnection() {
        val fitnessTracker = TrackingHelper.getSelectedFitnessTracker()
        if (fitnessTracker == TrackingHelper.FITBIT_TRACKING_SOURCE_ID) {
            checkFitbitConnection()
        } else if (fitnessTracker == TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID) {
            checkGoogleFitConnection()
        } else {
            getParticipantData()
        }
    }

    override fun showNoNetworkMessage() {
        showError(getString(R.string.no_network), getString(R.string.no_network_message), networkErrorDialogCallback)
    }

    private fun checkFitbitConnection() {
        if (TrackingHelper.isTimeToValidateTrackerConnection()) {
            FitbitHelper.validateLogin(this, object : TrackerLoginStatusCallback {
                override fun onTrackerLoginValid(trackerId: Int) {
                    getParticipantData()
                }

                override fun onTrackerLoginVerifyError() {
                    showTrackerConnectionError(TrackingHelper.FITBIT_TRACKING_SOURCE_ID)
                }

                override fun trackerNeedsReLogin(trackerSourceId: Int) {
                    showTrackerNeedsReLoginError(TrackingHelper.FITBIT_TRACKING_SOURCE_ID)
                }
            })
        } else {
            getParticipantData()
        }
    }

    private fun checkGoogleFitConnection() {
        if (TrackingHelper.isTimeToValidateTrackerConnection()) {
            GoogleFitHelper.validateLogin(this, object : TrackerLoginStatusCallback {
                override fun onTrackerLoginValid(trackerId: Int) {
                    getParticipantData()
                }

                override fun onTrackerLoginVerifyError() {
                    showTrackerConnectionError(TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID)
                }

                override fun trackerNeedsReLogin(trackerSourceId: Int) {
                    showTrackerNeedsReLoginError(TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID)
                }
            })
        } else {
            getParticipantData()
        }
    }

    fun showTrackerConnectionError(trackerId: Int) {
        hideLoadingProgressViewUnStack("showTrackerConnectionError id=" + trackerId)
        var title: String = ""
        if (trackerId == TrackingHelper.FITBIT_TRACKING_SOURCE_ID) {
            title = getString(R.string.tracker_connection_title_template, "Fitbit")
        } else if (trackerId == TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID) {
            title = getString(R.string.tracker_connection_title_template, "Google Fit App")
        }

        val message = getString(R.string.tracker_connection_check_error)
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    getParticipantData()
                })
                .show()
    }

    fun showTrackerNeedsReLoginError(trackerId: Int) {
        hideLoadingProgressViewUnStack("showTrackerNeedsReLoginError id=" + trackerId)
        var title: String = ""
        if (trackerId == TrackingHelper.FITBIT_TRACKING_SOURCE_ID) {
            title = getString(R.string.tracker_connection_title_template, "Fitbit")
        } else if (trackerId == TrackingHelper.GOOGLE_FIT_TRACKING_SOURCE_ID) {
            title = getString(R.string.tracker_connection_title_template, "Google Fit App")
        }

        val message = getString(R.string.tracker_needs_reconnection)
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    getParticipantData()
                })
                .show()
    }

    private fun getParticipantData() {
        myParticipantId?.let {
            if (!TextUtils.isEmpty(it)) {
                //TODO when other auth providers are implemented, call the appropriate method for participant retrieval
                homePresenter.getParticipant(it)
            }
        } ?: run {
            showLoginActivity()
            finish()
        }
    }

    private fun askToRelogin() {
        hideLoadingProgressViewUnStack("askToRelogin")
        val message = getString(R.string.login_invalid_relogin_message)
        AlertDialog.Builder(this)
                .setTitle(R.string.login_title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->

                    showLoginActivity()
                    finish()

                })
                .setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { dialog, which ->

                    finish()

                })

                .create()
                .show()
    }

    private fun isLoginValid(): Boolean {
        if (LoginHelper.isTimeToValidateLogin()) {
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                var authSource = AuthSource.valueOf(SharedPreferencesUtil.getMyAuthSource())
                when (authSource) {
                    AuthSource.Facebook -> {
                        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
                        accessToken?.let {
                            return !it.isExpired
                        }
                        return false
                    }
                    else -> return true
                }
            }
            return false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.settings) {
            val intent = SettingsActivity.createIntent(this)
            startActivity(intent)

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val navigation = findViewById<BottomNavigationView>(R.id.home_navigation)

        if (supportFragmentManager.getBackStackEntryCount() > 0) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment is DashboardFragment
                    || fragment is LeaderboardFragment
                    || fragment is NotificationsFragment) {
                navigation.setSelectedItemId(R.id.nav_challenge)
            } else if (fragment is ChallengeFragment) {
                finish()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun setupView() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)

        val navigation = findViewById<BottomNavigationView>(R.id.home_navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun loadFragment(fragment: Fragment?, navItemId: Int) {
        // Pop off everything up to and including the current tab
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment!!)
                .addToBackStack(BACK_STACK_ROOT_TAG)
                .commit()

        currentNavigationId = navItemId
    }

    fun showLoginActivity() {
        val intent = LoginActivity.createIntent(this)
        this.startActivity(intent)
    }

    fun setMyActiveEventId(myActiveEventId: Int) {
        this.myActiveEventId = myActiveEventId
    }

    override fun noActiveEventFound() {
        showErrorAndCloseApp(R.string.events_not_selected_error)
        return
    }

    override fun showErrorAndCloseApp(@StringRes messageId: Int) {
        showError(messageId)
        if (getEvent() == null) {
            Handler().postDelayed({ finish() }, Constants.SPLASH_TIMER.toLong())
        }
    }

    override fun onGetParticipant(participant: Participant?) {
        if (participant == null) {
            return
        }
        val participantTeamId: Int? = participant.teamId
        participantTeamId?.let {
            myTeamId = it       // team must have been assigned previously
            SharedPreferencesUtil.saveMyTeamId(myTeamId)

        } ?: run {
            if (myTeamId > 0) {
                myTeamId = 0
                homePresenter.participantLeaveFromTeam(myParticipantId)
            }
        }

        val event = participant.getEvent(myActiveEventId)
        event?.let {
            cacheEvent(event) //this will also have participant's commitment if already committed for this event
            val commitment: Commitment? = event.participantCommitment
            commitment?.let {

                var myStepsCommited = SharedPreferencesUtil.getMyStepsCommitted()
                if (myStepsCommited <= 0) {
                    myStepsCommited = event.defaultSteps
                }
                if (myStepsCommited != commitment.commitmentSteps) {
                    homePresenter.updateParticipantCommitment(it.id, participant.fbId!!, myActiveEventId, myStepsCommited)
                } else {
                    addNavigationFragments()
                }
            } ?: run {
                homePresenter.createParticipantCommitment(participant.fbId!!, myActiveEventId, event.defaultSteps)
            }
        } ?: run {
            homePresenter.createParticipantCommitment(participant.fbId!!, myActiveEventId, getEvent().defaultSteps)
        }
    }

    override fun onGetParticipantNotFound() {
        homePresenter.createParticipant(myParticipantId)
    }

    override fun onParticipantCreated(participant: Participant) {
        cacheParticipant(participant)
        homePresenter.createParticipantCommitment(participant.fbId!!, myActiveEventId, event.defaultSteps)
    }

    override fun onAssignedParticipantToEvent(participantId: String, eventId: Int) {
        addNavigationFragments()
    }

    protected fun addNavigationFragments() {
        if (dashboardFragment == null) {
            dashboardFragment = DashboardFragment.newInstance()
        }
        if (notificationsFragment == null) {
            notificationsFragment = NotificationsFragment.newInstance()
        }

        if (challengeFragment == null) {
            challengeFragment = ChallengeFragment.newInstance()
        }
        if (leaderboardFragment == null) {
            leaderboardFragment = LeaderboardFragment.newInstance()
        }

        val navigation = findViewById<BottomNavigationView>(R.id.home_navigation)
        navigation.selectedItemId = R.id.nav_challenge

        Handler().postDelayed({
            val profileCreated = SharedPreferencesUtil.getAkfProfileCreated()
            homePresenter.confirmAKFProfile(profileCreated)
        }, 1000)
    }

    override fun akfProfileCreationSkipped() {
        akfProfileCreationComplete()
    }

    override fun akfProfileRegistered() {
    }

    override fun participantNotRetrieved() {
        showError(getString(R.string.data_error), getString(R.string.participant_data_not_loaded_error), networkErrorDialogCallback)
    }

    override fun showAKFProfileView() {
        val fragment = AKFParticipantProfileFragment()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun akfProfileCreationComplete() {
        onBackPressed()
    }

    override fun showToolbar() {
        toolbar?.setVisibility(View.VISIBLE)
    }

    override fun hideToolbar() {
        toolbar?.setVisibility(View.GONE)
    }

    override fun restartApp() {
        SharedPreferencesUtil.clearMyLogin()
        WCFApplication.instance.restartApp()
        finish()
    }

    override fun akfProfileRegistrationError(error: Throwable, participantId: String) {
        if (error is IOException) {
            showNetworkErrorMessage(R.string.data_error)
        }
    }

    override fun showCreateTeam() {
        val fragment = CreateTeamFragment()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun showJoinTeam() {
        val fragment = JoinTeamFragment()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun showTeamChallengeProgress(isTeamLead: Boolean) {
        val fragment = TeamChallengeProgressFragment.getInstance(isTeamLead)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun showSupportersInvite() {
        val fragment = SupporterInviteFragment()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun showSupportersList() {
        //TODO: create and show the supporters fragment
    }

    override fun showParticipantBadgesEarned() {
        val fragment = BadgesFragment()
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .addToBackStack("BadgesFragment")
                .commit()
    }

    override fun showMilestones() {
        val fragment = JourneyFragment()
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .addToBackStack("JourneyFragment")
                .commit()
    }

    override fun showMilestoneDetail(milestone: Milestone) {
        Toast.makeText(this, "Milestone details coming soon", Toast.LENGTH_SHORT).show()
        //TODO: create and Milestones detail fragment
    }

    override fun showNotificationsCount(count: Int) {
        val navBar = findViewById<BottomNavigationView>(R.id.home_navigation)
        if (count == 0) {
            navBar.removeBadge(R.id.nav_notifications)
        }
        else {
            navBar.getOrCreateBadge(R.id.nav_notifications).number = count
        }
    }

    companion object {

        private val TAG = HomeActivity::class.java.simpleName
        private val BACK_STACK_ROOT_TAG = "root_fragment"

        @JvmStatic
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }
    }
}
