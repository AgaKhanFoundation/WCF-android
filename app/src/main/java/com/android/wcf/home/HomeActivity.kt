package com.android.wcf.home

import android.content.Context
import android.content.Intent
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
import com.android.wcf.home.challenge.*
import com.android.wcf.home.dashboard.DashboardFragment
import com.android.wcf.home.dashboard.DashboardMvp
import com.android.wcf.home.leaderboard.LeaderboardFragment
import com.android.wcf.home.leaderboard.LeaderboardMvp
import com.android.wcf.home.notifications.NotificationsFragment
import com.android.wcf.home.notifications.NotificationsMvp
import com.android.wcf.login.LoginActivity
import com.android.wcf.model.Participant
import com.android.wcf.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity()
        , HomeMvp.HomeView
        , DashboardMvp.Host
        , ChallengeMvp.Host
        , CreateTeamMvp.Host
        , JoinTeamMvp.Host
        , TeamChallengeProgressMvp.Host
        , SupportsInviteMvp.Host
        , LeaderboardMvp.Host
        , NotificationsMvp.Host {

    private val SPLASH_TIMER = 3000

    private var homePresenter: HomePresenter? = null
    private var dashboardFragment: DashboardFragment? = null
    private var challengeFragment: ChallengeFragment? = null
    private var leaderboardFragment: LeaderboardFragment? = null
    private var notificationsFragment: NotificationsFragment? = null
    private var toolbar: Toolbar? = null

    private var myActiveEventId: Int = 0
    private var myTeamId: Int = 0
    private var myParticpantId: String? = null

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

        myParticpantId = SharedPreferencesUtil.getMyParticipantId()
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
        if (myParticpantId == null || TextUtils.isEmpty(myParticpantId)) {
            showLoginActivity()
            finish()
            return
        }
        homePresenter!!.getParticipant(myParticpantId!!)
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

    private fun loadFragment(fragment: Fragment?, navItemId: Int) {
        val fm = supportFragmentManager
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

    fun setMyParticipantId(participantId: String) {
        this.myParticpantId = participantId
    }

    fun setMyTeamId(myTeamId: Int) {
        this.myTeamId = myTeamId
    }

    fun setMyActiveEventId(myActiveEventId: Int) {
        this.myActiveEventId = myActiveEventId
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
            homePresenter!!.participantLeaveFromTeam(myParticpantId)
        } else if (participantTeamId != null) {
            myTeamId = participantTeamId.toInt() // team must have been assigned previously
            SharedPreferencesUtil.saveMyTeamId(myTeamId)
        }
        if (participant.eventId == null || participant.eventId?.toInt() != myActiveEventId) {
            homePresenter!!.updateParticipantEvent(myParticpantId, myActiveEventId)
        } else {
            addNavigationFragments()
        }
    }

    override fun onGetParticipantNotFound() {
        homePresenter!!.createParticipant(myParticpantId)
    }

    override fun onParticipantCreated(participant: Participant) {
        setParticipant(participant);
        homePresenter!!.updateParticipantEvent(myParticpantId, myActiveEventId)
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

    override fun showTeamChallengeProgress() {
        val fragment = TeamChallengeProgressFragment()
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
