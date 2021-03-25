package com.android.wcf.home.challenge.journey

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.*
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wcf.R
import com.android.wcf.base.BaseFragment
import com.android.wcf.model.Milestone
import com.android.wcf.helper.view.ListPaddingDecoration
import com.android.wcf.model.Media

class JourneyFragment : BaseFragment(), JourneyMvp.View {
    private var host: JourneyMvp.Host? = null
    lateinit var presenter: JourneyMvp.Presenter

    lateinit var titleText: TextView
    lateinit var recycler: RecyclerView
    private var adapter: JourneyMilestonesAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is JourneyMvp.Host) {
            host = context
        } else {
            throw RuntimeException("$context must implement JourneyMvp.Host")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = JourneyPresenter(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.findItem(R.id.settings)?.isVisible = false
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                closeView()
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun closeView() {
        activity?.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        presenter.getMilestonesData()
    }

    override fun onResume() {
        super.onResume()
        host?.setToolbarTitle(getString(R.string.journey_fragment_title), true)
    }

    fun setupView(view: View) {
        titleText = view.findViewById(R.id.toolbar_title)
        recycler = view.findViewById(R.id.journey_recycler)

        context?.let {
            recycler.layoutManager = LinearLayoutManager(it)
//            recycler.addItemDecoration(ListPaddingDecoration(it))
//
//            recycler.addItemDecoration(DividerItemDecoration(it, DividerItemDecoration.VERTICAL))

        }

        adapter = JourneyMilestonesAdapter(context!!, object : JourneyMilestonesAdapter.AdapterHost {
            override fun onItemSelected(milestone: Milestone) {
                onMilestoneSelected(milestone)
            }
        })
        recycler.adapter = adapter
    }

    fun onMilestoneSelected(milestone:Milestone) {
        host?.showMilestoneDetail(milestone)
    }

    override fun showMilestoneData(milestones: List<Milestone>, lastCompletedMilestoneSequence:Int, nextMilestoneSequence: Int, nextMilestonePercentageCompletion: Double) {
        val context = context
        val adapter = adapter
        if (context != null && adapter != null) {
            adapter.setData(milestones, lastCompletedMilestoneSequence, nextMilestoneSequence, nextMilestonePercentageCompletion)
        }
        Handler().post({
            recycler.smoothScrollToPosition(nextMilestoneSequence)
        })
    }

    override fun showJourneyOverview(remainingMiles: Long, totalMiles: Long, nextMilestoneName: String) {
        var text:SpannableString
        if (totalMiles > 0 && remainingMiles <= totalMiles) {
            val bold = getString(R.string.journey_miles_left_1, remainingMiles, totalMiles)
            val notBold = getString(R.string.journey_miles_left_2, nextMilestoneName)
            text = SpannableString(bold + notBold)
            text.setSpan(StyleSpan(Typeface.BOLD), 0, bold.length, 0)
        }
        else if (totalMiles > 0 && remainingMiles == 0L) {
            val bold = getString(R.string.journey_milestone_reached, nextMilestoneName)
            text = SpannableString( bold)
            text.setSpan(StyleSpan(Typeface.BOLD), 0, bold.length, 0)
        }
        else {
            val bold = getString(R.string.journey_completed)
            text = SpannableString( bold)
            text.setSpan(StyleSpan(Typeface.BOLD), 0, bold.length, 0)
        }

        titleText.setText(text, BufferType.SPANNABLE)

    }
}