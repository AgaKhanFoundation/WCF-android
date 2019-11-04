package com.android.wcf.home.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.wcf.R
import com.android.wcf.base.BaseFragment
import com.android.wcf.helper.DistanceConverter
import com.android.wcf.helper.SharedPreferencesUtil
import com.fitbitsdk.service.models.ActivitySteps
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class ParticipantActivityFragment : BaseFragment() {

    companion object {
        val TAG = ParticipantActivityFragment::class.java.simpleName
        val ACTIVITY_TYPE_ARG = "activity_type"

        val ACTIVITY_TYPE_DAILY = 0
        val ACTIVITY_TYPE_WEEKLY = 1

        @JvmStatic
        fun instanceDaily(): ParticipantActivityFragment {
            val args = Bundle()
            args.putInt(ACTIVITY_TYPE_ARG, ACTIVITY_TYPE_DAILY)
            args.putInt(ACTIVITY_TYPE_ARG, ACTIVITY_TYPE_DAILY)

            val fragment = ParticipantActivityFragment()
            fragment.arguments = args
            return fragment
        }

        @JvmStatic
        fun instanceWeekly(): ParticipantActivityFragment {
            val args = Bundle()
            args.putInt(ACTIVITY_TYPE_ARG, ACTIVITY_TYPE_WEEKLY)
            val fragment = ParticipantActivityFragment()
            fragment.arguments = args
            return fragment
        }

        val numberFormatter = DecimalFormat("#,###,###.##")
        val stepsDataDateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val dayOfWeekFormatter = SimpleDateFormat("EEE")
        val weekStartFormatter = SimpleDateFormat("MMM dd")
        val weekEndFormatter = SimpleDateFormat("MMM dd, yyyy")
    }

    var activityType: Int = ACTIVITY_TYPE_DAILY

    var daysInChallenge: Int = 0

    var activityGoal: Int = 0

    var stepsInfo: ActivitySteps? = null
    var stepsIndex = 0;
    var weekStartIndex = -1;
    var weekEndIndex = -1;

    lateinit var navPrev: ImageView
    lateinit var navNext: ImageView
    lateinit var dateTv: TextView

    lateinit var trackedInfoDailyContainer: View

    lateinit var activityPb: ProgressBar
    lateinit var activityCompletedTv: TextView
    lateinit var activityGoalTv: TextView
    lateinit var activityDailyViewSelectorRg: RadioGroup
    lateinit var activityDailySelectorStepsRb: RadioButton
    lateinit var activityDailySelectorDistanceRb: RadioButton

    lateinit var trackedInfoWeeklyContainer: View
    lateinit var activityMonPb: ProgressBar
    lateinit var activityTuePb: ProgressBar
    lateinit var activityWedPb: ProgressBar
    lateinit var activityThuPb: ProgressBar
    lateinit var activityFriPb: ProgressBar
    lateinit var activitySatPb: ProgressBar
    lateinit var activitySunPb: ProgressBar

    lateinit var activityMonTv: TextView
    lateinit var activityTueTv: TextView
    lateinit var activityWedTv: TextView
    lateinit var activityThuTv: TextView
    lateinit var activityFriTv: TextView
    lateinit var activitySatTv: TextView
    lateinit var activitySunTv: TextView

    var activityDailyViewSteps: Boolean = true

    val onDailyViewCheckedChangeListener = object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            when (checkedId) {
                R.id.activity_daily_selector_distance_rb ->
                    activityDailyViewSteps = false
                else ->
                    activityDailyViewSteps = true
            }
            SharedPreferencesUtil.saveDailyViewSelection(if (activityDailyViewSteps) 0 else 1)
            updateViewDaily()
        }
    }

    var onClickListener: View.OnClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.navigate_prev -> {
                if (activityType == ACTIVITY_TYPE_DAILY) {

                    var stepsSize = 0
                    stepsInfo?.steps?.let {
                        stepsSize = it.size
                    }

                    if (stepsIndex < stepsSize) {
                        stepsIndex++
                        updateView()
                    }
                } else {
                    scrollToPreviousWeek()
                }
            }
            R.id.navigate_next -> {
                if (activityType == ACTIVITY_TYPE_DAILY) {

                    if (stepsIndex > 0) {
                        stepsIndex--
                        updateView()
                    }
                } else {
                    scrollToRecentWeek()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_participant_tracked_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            activityType = it.getInt(ACTIVITY_TYPE_ARG)
        }
        setupView(view);
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
        //  presenter.onStop()

    }

    fun setStepsData(data: ActivitySteps) {
        stepsInfo = sortDescending(data)
        if (activityType == ACTIVITY_TYPE_DAILY) {
            stepsIndex = 0
        } else {
            weekStartIndex = findStartOfWeekIndex()
            weekEndIndex = weekStartIndex - 6
        }

        updateView()
    }

    fun sortDescending(data: ActivitySteps): ActivitySteps {
        var activitySteps = ActivitySteps()
        data.steps.sortByDescending { it.date }
        activitySteps.steps = data.steps
        return activitySteps
    }

    fun setDistanceGoal(goal: Int) {
        if (activityType == ACTIVITY_TYPE_DAILY) {
            activityGoal = goal / daysInChallenge
        } else {
            activityGoal = (goal / daysInChallenge) * 7
        }
    }

    fun setupView(fragmentView: View) {
        val navContainer: View = fragmentView.findViewById(R.id.nav_container)
        navPrev = navContainer.findViewById(R.id.navigate_prev)
        navNext = navContainer.findViewById(R.id.navigate_next)
        dateTv = navContainer.findViewById(R.id.date)
        navPrev.setOnClickListener(onClickListener)
        navNext.setOnClickListener(onClickListener)

        trackedInfoDailyContainer = fragmentView.findViewById(R.id.tracked_info_container_daily)
        trackedInfoWeeklyContainer = fragmentView.findViewById(R.id.tracked_info_container_weekly)
        if (activityType == ACTIVITY_TYPE_DAILY) {
            trackedInfoWeeklyContainer.visibility = View.GONE
            trackedInfoDailyContainer.visibility = View.VISIBLE

            activityPb = trackedInfoDailyContainer.findViewById(R.id.activity_progress)
            activityCompletedTv = trackedInfoDailyContainer.findViewById(R.id.activity_completed)
            activityGoalTv = trackedInfoDailyContainer.findViewById(R.id.activity_goal)

            activityDailyViewSelectorRg = trackedInfoDailyContainer.findViewById(R.id.activity_daily_view_selector_rg)
            activityDailySelectorStepsRb = trackedInfoDailyContainer.findViewById(R.id.activity_daily_selector_steps_rb)
            activityDailySelectorDistanceRb = trackedInfoDailyContainer.findViewById(R.id.activity_daily_selector_distance_rb)

            activityDailyViewSteps = if (SharedPreferencesUtil.getDailyViewSelection() == 0) true else false
            if (activityDailyViewSteps) {
                activityDailySelectorStepsRb.isChecked = true
            } else {
                activityDailySelectorDistanceRb.isChecked = true
            }

            activityDailyViewSelectorRg.setOnCheckedChangeListener(onDailyViewCheckedChangeListener)

        } else {
            trackedInfoWeeklyContainer.visibility = View.VISIBLE
            trackedInfoDailyContainer.visibility = View.GONE

            activityMonTv = trackedInfoWeeklyContainer.findViewById(R.id.activity_distance_mon)
            activityTueTv = trackedInfoWeeklyContainer.findViewById(R.id.activity_distance_tue)
            activityWedTv = trackedInfoWeeklyContainer.findViewById(R.id.activity_distance_wed)
            activityThuTv = trackedInfoWeeklyContainer.findViewById(R.id.activity_distance_thu)
            activityFriTv = trackedInfoWeeklyContainer.findViewById(R.id.activity_distance_fri)
            activitySatTv = trackedInfoWeeklyContainer.findViewById(R.id.activity_distance_sat)
            activitySunTv = trackedInfoWeeklyContainer.findViewById(R.id.activity_distance_sun)

            activityMonPb = trackedInfoWeeklyContainer.findViewById(R.id.activity_progress_mon)
            activityTuePb = trackedInfoWeeklyContainer.findViewById(R.id.activity_progress_tue)
            activityWedPb = trackedInfoWeeklyContainer.findViewById(R.id.activity_progress_wed)
            activityThuPb = trackedInfoWeeklyContainer.findViewById(R.id.activity_progress_thu)
            activityFriPb = trackedInfoWeeklyContainer.findViewById(R.id.activity_progress_fri)
            activitySatPb = trackedInfoWeeklyContainer.findViewById(R.id.activity_progress_sat)
            activitySunPb = trackedInfoWeeklyContainer.findViewById(R.id.activity_progress_sun)

        }
    }

    fun updateView() {
        if (!isAttached()) {
            return
        }
        if (activityType == ACTIVITY_TYPE_DAILY) {
            updateViewDaily()
        } else {
            updateViewWeekly()
        }
    }

    fun updateViewDaily() {

        stepsInfo?.steps?.let {
            if (stepsIndex >= 0 && stepsIndex < it.size) {
                val steps = it.get(stepsIndex);
                navPrev.setEnabled(if (stepsIndex < it.size - 1) true else false)
                dateTv.text = steps.date
                val stepsCompletedEvent = steps.value.toInt()
                if (activityDailyViewSteps) {
                    activityCompletedTv.text = numberFormatter.format(stepsCompletedEvent)
                } else {
                    activityCompletedTv.text = numberFormatter.format(DistanceConverter.distance(stepsCompletedEvent))
                }

                val progress = (stepsCompletedEvent * 1.0 / this.activityGoal) * 100
                activityPb.progress = progress.toInt()
            }
        }

        if (activityDailyViewSteps) {
            activityGoalTv.text = getString(R.string.dashboard_steps_goal_template,
                    numberFormatter.format(activityGoal))
        } else {
            activityGoalTv.text =
                    getString(R.string.dashboard_distance_goal_template,
                            numberFormatter.format(DistanceConverter.distance(activityGoal)),
                            "mi")
        }

        navNext.setEnabled(if (stepsIndex > 0) true else false)
    }

    fun updateViewWeekly() {

        val stepsBarMaxMiles = 8 // arbitrary - most people will walk less than 8 miles/day; walked >= 8miles implies 100% progressBar
        var weekDateRange: String = ""
        var knownDateIdx = -1
        var knownDate: Date? = null

        for (idx in 0..6) {
            var dayIdx: Int
            var progress = 0.0
            var milesComplete = 0L
            stepsInfo?.steps?.let {

                dayIdx = weekStartIndex - idx

                if (dayIdx >= 0 && dayIdx < it.size) {
                    val steps = it.get(dayIdx);
                    milesComplete = DistanceConverter.distance(steps.value.toInt())

                    progress = ((milesComplete * 1.0) / stepsBarMaxMiles) * 100

                    if (knownDateIdx == -1) {
                        knownDateIdx = idx
                        knownDate = stepsDataDateFormatter.parse(steps.date)
                    }
                }
            }

            updateWeekDayInfo(idx, milesComplete, progress.toInt())
        }

        if (knownDateIdx != -1) {
            val cal = Calendar.getInstance()
            cal.time = knownDate
            cal.add(Calendar.DATE, 6 - knownDateIdx)
            val weekEndDate = cal.getTime()
            cal.add(Calendar.DATE, -6)
            val weekStartDate = cal.getTime()

            weekDateRange = weekStartFormatter.format(weekStartDate) + " - " + weekEndFormatter.format(weekEndDate)
        }

        dateTv.text = weekDateRange
        navPrev.setEnabled(moreOlderWeekData())
        navNext.setEnabled(moreRecentWeekData())
    }

    fun updateWeekDayInfo(idx: Int, milesComplete: Long, progress: Int) {
        when (idx) {
            0 -> {
                activityMonTv.text = numberFormatter.format(milesComplete)
                showWeekDayProgress(activityMonPb, progress)
            }
            1 -> {
                activityTueTv.text = numberFormatter.format(milesComplete)
                showWeekDayProgress(activityTuePb, progress)
            }
            2 -> {
                activityWedTv.text = numberFormatter.format(milesComplete)
                showWeekDayProgress(activityWedPb, progress)
            }
            3 -> {
                activityThuTv.text = numberFormatter.format(milesComplete)
                showWeekDayProgress(activityThuPb, progress)
            }
            4 -> {
                activityFriTv.text = numberFormatter.format(milesComplete)
                showWeekDayProgress(activityFriPb, progress)
            }
            5 -> {
                activitySatTv.text = numberFormatter.format(milesComplete)
                showWeekDayProgress(activitySatPb, progress)
            }
            6 -> {
                activitySunTv.text = numberFormatter.format(milesComplete)
                showWeekDayProgress(activitySunPb, progress)
            }
            else -> {
            }

        }
    }

    fun showWeekDayProgress(pb: ProgressBar, progress: Int) {
        if (progress <= 0) {
            pb.visibility = View.GONE
        } else {
            pb.visibility = View.VISIBLE

            val layoutParams = pb.layoutParams
            var newHeightFraction: Double = progress / 100.0
            if (newHeightFraction > 1.1) newHeightFraction = 1.1
            layoutParams.height = (getResources().getDimensionPixelSize(R.dimen.progressbar_vertical_height_weekly) * newHeightFraction).toInt()
            pb.layoutParams = layoutParams
            pb.progress = 100
        }
    }

    fun findStartOfWeekIndex(): Int {
        stepsInfo?.steps?.let {
            val steps = it.get(0)
            val stepDate = stepsDataDateFormatter.parse(steps.date)
            var dayOfWeek = dayOfWeekFormatter.format(stepDate)
            if (dayOfWeek.equals("Mon")) {
                return 0
            }
            if (dayOfWeek.equals("Tue")) {
                return 1
            }
            if (dayOfWeek.equals("Wed")) {
                return 2
            }
            if (dayOfWeek.equals("Thu")) {
                return 3
            }
            if (dayOfWeek.equals("Fri")) {
                return 4
            }
            if (dayOfWeek.equals("Sat")) {
                return 5
            }
            if (dayOfWeek.equals("Sun")) {
                return 6
            }
        }

        return -1
    }

    fun findEndOfWeekIndex(): Int {
        stepsInfo?.steps?.let {
            val steps = it.get(0)
            val dayOfWeek = dayOfWeekFormatter.parse(steps.date)
            if (dayOfWeek.equals("Mon")) {
                return -6
            }
            if (dayOfWeek.equals("Tue")) {
                return -5
            }
            if (dayOfWeek.equals("Wed")) {
                return -4
            }
            if (dayOfWeek.equals("Thu")) {
                return -3
            }
            if (dayOfWeek.equals("Fri")) {
                return -2
            }
            if (dayOfWeek.equals("Sat")) {
                return -1
            }
            if (dayOfWeek.equals("Sun")) {
                return 0
            }
        }


        return -1
    }

    fun moreOlderWeekData(): Boolean {
        stepsInfo?.steps?.let {
            return (weekStartIndex >= 0 && weekStartIndex < it.size - 1)

        }
        return false
    }

    fun moreRecentWeekData(): Boolean {
        return (weekEndIndex > 0)
    }

    fun scrollToPreviousWeek() {
        var stepsSize = 0
        stepsInfo?.steps?.let {
            stepsSize = it.size
        }

        if (weekStartIndex < stepsSize - 1) {
            weekStartIndex += 7
            weekEndIndex += 7
        }

        updateViewWeekly()
    }

    fun scrollToRecentWeek() {
        if (weekStartIndex - 7 > 0) {
            weekStartIndex -= 7
            weekEndIndex -= 7
        }
        updateViewWeekly()
    }
}