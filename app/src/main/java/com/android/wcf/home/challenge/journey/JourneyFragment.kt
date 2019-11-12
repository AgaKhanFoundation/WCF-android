package com.android.wcf.home.challenge.journey

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.*
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wcf.R
import com.android.wcf.base.BaseFragment
import com.android.wcf.model.Milestone

class JourneyFragment : BaseFragment(), JourneyMvp.View {
    lateinit var host: JourneyMvp.Host
    lateinit var presenter: JourneyMvp.Presenter

    lateinit var titleText: TextView
    lateinit var recycler: RecyclerView
    private var adapter: JourneyMilestonesAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is JourneyMvp.Host) {
            host = context
        } else {
            throw RuntimeException("$context must implement BadgesMvp.Host")
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
        host.setToolbarTitle(getString(R.string.journey_fragment_title), true)
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

    fun setupView(view: View) {
        titleText = view.findViewById(R.id.toolbar_title)
        recycler = view.findViewById(R.id.journey_recycler)

        recycler.layoutManager = LinearLayoutManager(context);
        adapter = JourneyMilestonesAdapter(context!!, object : JourneyMilestonesAdapter.AdapterHost {
            override fun onItemSelected(milestone: Milestone) {
                onMilestoneSelected()
            }
        })
        recycler.adapter = adapter
    }

    fun onMilestoneSelected() {
        presenter.onMilestoneSelected()
    }

    override fun showMilestoneDetail() {
        Toast.makeText(context, "Milestone details coming soon", Toast.LENGTH_SHORT).show()
        //TODO: create and Milestones detail fragment
    }

    override fun showMilestoneData(milestones: List<Milestone>, currentMilestoneSequence: Int, currentMilestonePercentageCompletion: Double) {
        val context = context
        val adapter = adapter
        if (context != null && adapter != null) {
            adapter.setData(milestones, currentMilestoneSequence, currentMilestonePercentageCompletion)
        }
    }

    override fun showJourneyOverview(completedMiles: Long, totalMiles: Long, nextLocation: String) {
        val bold = getString(R.string.journey_miles_left_1, completedMiles, totalMiles)
        val notBold = getString(R.string.journey_miles_left_2, nextLocation)
        val text = SpannableString(bold + notBold)
        text.setSpan(StyleSpan(Typeface.BOLD), 0, bold.length, 0)
        titleText.setText(text, BufferType.SPANNABLE)
    }
}