package com.android.wcf.home.challenge.journey

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.android.wcf.R
import com.android.wcf.base.BaseFragment
import com.android.wcf.helper.view.ViewPager2ChangeHandler
import com.android.wcf.model.Milestone

class MilestoneDetailFragment : BaseFragment(), JourneyDetailMvp.View, MilestoneContentAdapter.AdapterHost {
    private var host: JourneyDetailMvp.Host? = null
    private lateinit var presenter: JourneyDetailMvp.Presenter
    private lateinit var titleText: TextView
    private var mediaPager: ViewPager2? = null
    private var pageIndicatorView: LinearLayout? = null

    companion object {
        val MILESTONE_DATA = "milestone_data"

        fun newInstance(milestone: Milestone): MilestoneDetailFragment {
            val frag = MilestoneDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(MILESTONE_DATA, milestone)
            frag.setArguments(bundle)
            return frag
        }
    }
    var milestone: Milestone? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is JourneyDetailMvp.Host) {
            host = context
        } else {
            throw RuntimeException("$context must implement JourneyDetailMvp.Host")
        }
    }

    override fun onDetach() {
        super.onDetach()
        host = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_milestone_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        host?.setToolbarTitle(getString(R.string.milestone_detail_title), true)
        milestone = arguments?.getParcelable(MILESTONE_DATA)
        setupView(view)
    }

    fun setupView(view: View) {
        titleText = view.findViewById(R.id.toolbar_title)
        mediaPager = view.findViewById(R.id.media_pager)
        milestone?.let { milestone ->
            showMilestoneDetail(milestone)
        }
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

    val viewPagerAnimationRotate = false
    val viewPagerAnimationTranslateY = false
    val viewPagerAnimationTranslateX = false
    val viewPagerAnimationTranslateScale = false

    private val mAnimator = ViewPager2.PageTransformer { page, position ->
        val absPos = Math.abs(position)
        page.apply {
            rotation = if (viewPagerAnimationRotate) position * 360 else 0f
            translationY = if (viewPagerAnimationTranslateY) absPos * 500f else 0f
            translationX = if (viewPagerAnimationTranslateX) absPos * 350f else 0f
            if (viewPagerAnimationTranslateScale) {
                val scale = if (absPos > 1) 0F else 1 - absPos
                scaleX = scale
                scaleY = scale
            } else {
                scaleX = 1f
                scaleY = 1f
            }
        }
    }

    private var viewPager2ChangeHandler: ViewPager2ChangeHandler? = null


    private fun showMilestoneDetail(milestone: Milestone) {
        titleText.setText(milestone.name, TextView.BufferType.SPANNABLE)
        mediaPager = view?.findViewById(R.id.media_pager)
        val viewpagerIndicators:LinearLayout? =  view?.findViewById(R.id.media_pager_indicator)
        val adapter = MilestoneContentAdapter(context, this)
        mediaPager?.adapter = adapter

        viewPager2ChangeHandler = ViewPager2ChangeHandler(
                requireContext(),
                viewpagerIndicators,
                mediaPager,
                R.drawable.viewpager_selector)

        val mediaPageChangeCallback = MediaPageChangeListener(viewPager2ChangeHandler)
        mediaPager?.setPageTransformer(mAnimator)
        mediaPager?.registerOnPageChangeCallback(mediaPageChangeCallback)


        val pagerItemCount:Int = adapter?.setData(milestone)
        viewPager2ChangeHandler?.setPageCount(pagerItemCount)
        if (pagerItemCount > 1) {
            viewPager2ChangeHandler?.show(0)
        }
    }

    override fun onItemSelected(milestone: Milestone) {
    }

    class MediaPageChangeListener(val viewPager2ChangeHandler: ViewPager2ChangeHandler?) : ViewPager2.OnPageChangeCallback() {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            viewPager2ChangeHandler?.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
        }
    }
}


