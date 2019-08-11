package com.android.wcf.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.wcf.R
import com.android.wcf.base.BaseActivity
import com.android.wcf.helper.SharedPreferencesUtil
import com.android.wcf.home.challenge.ChallengeFragment
import com.android.wcf.home.challenge.ChallengeMvp
import com.android.wcf.home.challenge.CreateTeamFragment
import com.android.wcf.home.challenge.CreateTeamMvp
import com.android.wcf.home.dashboard.DashboardFragment
import com.android.wcf.home.dashboard.DashboardMvp
import com.android.wcf.home.leaderboard.LeaderboardFragment
import com.android.wcf.home.notifications.NotificationsFragment
import com.android.wcf.login.LoginActivity
import com.android.wcf.model.Participant
import com.android.wcf.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity()
        , HomeMvp.HomeView
        , DashboardMvp.Host
        , ChallengeMvp.Host
        , CreateTeamMvp.Host
        , LeaderboardFragment.FragmentHost
        , NotificationsFragment.FragmentHost {

    private val SPLASH_TIMER = 3000

    private var homePresenter: HomePresenter? = null
    private var dashboardFragment: DashboardFragment? = null
    private var challengeFragment: ChallengeFragment? = null
    private var leaderboardFragment: LeaderboardFragment? = null
    private var notificationsFragment: NotificationsFragment? = null

    private var myFacebookId: String? = null
    private val myFbEmail: String? = null
    private val myFacebookName: String? = null
    private val myFacebookProfileUrl: String? = null

    private var myActiveEventId: Int = 0
    private var myTeamId: Int = 0
    private var toolbar: Toolbar? = null

    private var currentNavigationId: Int = 0

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (currentNavigationId == item.itemId) {
            return@OnNavigationItemSelectedListener false
        }

        when (item.itemId) {
            R.id.nav_dashboard -> {
                if (dashboardFragment != null) {
                    loadFragment(dashboardFragment, getString(R.string.nav_dashboard), R.id.nav_dashboard)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_challenge -> {
                if (challengeFragment != null) {
                    loadFragment(challengeFragment, getString(R.string.nav_challenge), R.id.nav_challenge)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_leaderboard -> {
                if (leaderboardFragment != null) {
                    loadFragment(leaderboardFragment, getString(R.string.nav_leaderboard), R.id.nav_leaderboard)
                    leaderboardFragment!!.setMyTeamId(myTeamId)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                if (notificationsFragment != null) {
                    loadFragment(notificationsFragment, getString(R.string.nav_notifications), R.id.nav_notifications)
                }
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        myFacebookId = SharedPreferencesUtil.getMyFacebookId()
        myActiveEventId = SharedPreferencesUtil.getMyActiveEventId()
        myTeamId = SharedPreferencesUtil.getMyTeamId()

        homePresenter = HomePresenter(this)

        setupView()
    }

    override fun onStart() {
        super.onStart()

        if (myActiveEventId < 1) {
            showErrorAndCloseApp(R.string.events_not_selected_error)
            return
        }
        if (myFacebookId == null || TextUtils.isEmpty(myFacebookId)) {
            showLoginActivity()
            finish()
            return
        }
        homePresenter!!.getParticipant(myFacebookId!!)
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

    private fun setupView() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)

        val navigation = findViewById<BottomNavigationView>(R.id.home_navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun loadFragment(fragment: Fragment?, title: String, navItemId: Int) {
        val fm = supportFragmentManager
        // Pop off everything up to and including the current tab
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment!!)
                .addToBackStack(BACK_STACK_ROOT_TAG)
                .commit()

        setViewTitle(title)
        currentNavigationId = navItemId
    }

    override fun setViewTitle(title: String?) {
        val actionBar = supportActionBar
        if (title != null) {
            actionBar!!.title = title
            actionBar.setDisplayShowTitleEnabled(true)
        } else {
            actionBar!!.setDisplayShowTitleEnabled(false)
        }

        actionBar.setHomeButtonEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)
    }

    override fun showToolbarUpAffordance(show: Boolean) {
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(show)
        actionBar.setDisplayShowHomeEnabled(show)
    }

    fun showLoginActivity() {
        val intent = LoginActivity.createIntent(this)
        this.startActivity(intent)
    }

    fun setMyFacebookId(fbid: String) {
        this.myFacebookId = fbid
    }

    fun setMyTeamId(myTeamId: Int) {
        this.myTeamId = myTeamId
    }

    fun setMyActiveEventId(myActiveEventId: Int) {
        this.myActiveEventId = myActiveEventId
    }

    override fun onDashboardFragmentInteraction(uri: Uri) {

    }

    override fun onChallengeFragmentInteraction(uri: Uri) {

    }

    override fun onLeaderboardFragmentInteraction(uri: Uri) {

    }

    override fun onNotificationFragmentInteraction(uri: Uri) {

    }

    override fun showErrorAndCloseApp(@StringRes messageId: Int) {
        showError(messageId)
        Handler().postDelayed({ finish() }, SPLASH_TIMER.toLong())
    }

    override fun onGetParticipant(participant: Participant?) {
        if (participant == null) {
            return
        }
        val participantTeamId = participant.teamId
        if (participantTeamId == null && myTeamId > 0) {
            myTeamId = 0
            homePresenter!!.participantLeaveFromTeam(myFacebookId)
        } else if (participantTeamId != null) {
            myTeamId = participantTeamId.toInt() // team must have been assigned remotely
        }

        if (participant.eventId == null || participant.eventId?.toInt() != myActiveEventId) {
            homePresenter!!.updateParticipantEvent(myFacebookId, myActiveEventId)
        } else {
            addNavigationFragments()
        }
    }

    override fun onGetParticipantNotFound() {
        homePresenter!!.createParticipant(myFacebookId)
    }

    override fun onParticipantCreated(participant: Participant) {
        setParticipant(participant);
        homePresenter!!.updateParticipantEvent(myFacebookId, myActiveEventId)
    }

    override fun onAssignedParticipantToEvent(fbId: String, eventId: Int) {
        addNavigationFragments()
    }

    protected fun addNavigationFragments() {
        if (dashboardFragment == null) {
            dashboardFragment = DashboardFragment.newInstance()
        }
        if (notificationsFragment == null) {
            notificationsFragment = NotificationsFragment.newInstance(null, null)
        }

        if (challengeFragment == null) {
            challengeFragment = ChallengeFragment.newInstance(myFacebookId, myActiveEventId, myTeamId)
        }
        if (leaderboardFragment == null) {
            leaderboardFragment = LeaderboardFragment.newInstance(myTeamId)
        }

        val navigation = findViewById<BottomNavigationView>(R.id.home_navigation)
        navigation.selectedItemId = R.id.nav_challenge

    }

    override fun showCreateTeam() {
        val fragment = CreateTeamFragment()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    companion object {

        private val TAG = HomeActivity::class.java.simpleName
        private val BACK_STACK_ROOT_TAG = "root_fragment"

        fun createIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }
    }
}
