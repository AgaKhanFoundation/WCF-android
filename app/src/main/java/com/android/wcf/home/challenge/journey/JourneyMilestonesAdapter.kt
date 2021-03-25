package com.android.wcf.home.challenge.journey

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.wcf.R
import com.android.wcf.model.Milestone
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class JourneyMilestonesAdapter(context: Context, val adapterHost: AdapterHost) : RecyclerView.Adapter<JourneyMilestonesAdapter.MilestoneViewHolder>() {
    var milestoneList: List<Milestone> = listOf()
    var numMilestones = 0
    var lastCompletedMilestoneSequence = 0
    var nextMilestoneSequence: Int = 0
    var nextMilestonePercentageCompletion: Double = 0.0
    var blackColor: Int = 0
    var greyColor: Int = 0
    var disabledGreyColor: Int = 0
    var blueColor: Int = 0
    var greenColor: Int = 0

    init {
        blackColor = context.getColor(R.color.color_black)
        greyColor = context.getColor(R.color.color_light_grey_3)
        blueColor = context.getColor(R.color.color_blue)
        greenColor = context.getColor(R.color.color_primary)
        disabledGreyColor = context.getColor(R.color.color_light_grey_8)
    }

    private val itemClickedListener = object : AdapterViewListener {
        override fun onItemClick(view: View, position: Int) {
            adapterHost.onItemSelected(milestoneList[position])
        }
    }

    fun setData(data: List<Milestone>, lastCompletedMilestoneSequence: Int, nextMilestoneSequence: Int, nextMilestonePercentageCompletion: Double) {
        milestoneList = data
        this.lastCompletedMilestoneSequence = lastCompletedMilestoneSequence
        this.nextMilestoneSequence = nextMilestoneSequence
        this.nextMilestonePercentageCompletion = nextMilestonePercentageCompletion
        numMilestones = milestoneList.size - 1

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return milestoneList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.requestLayout()
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilestoneViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_list_journey_milestone_item, parent, false)

        return MilestoneViewHolder(view, itemClickedListener, blackColor, greyColor, blueColor, greenColor, disabledGreyColor,
                lastCompletedMilestoneSequence, nextMilestoneSequence, nextMilestonePercentageCompletion)
    }

    override fun onBindViewHolder(viewHolder: MilestoneViewHolder, position: Int) {
        viewHolder.bindView(milestoneList[position], viewHolder.lastCompletedMilestoneSequence, viewHolder.nextMilestoneSequence, numMilestones)
    }

    class MilestoneViewHolder(itemView: View, val clickListener: AdapterViewListener?,
                              val black: Int, val grey: Int, val blue: Int, val green: Int, val disabledGreyColor: Int,
                              val lastCompletedMilestoneSequence: Int,
                              val nextMilestoneSequence: Int,
                              val nexttMilestonePercentageCompletion: Double) : RecyclerView.ViewHolder(itemView) {

        val cardView: CardView = itemView.findViewById(R.id.milestone_cardview)
        val infoContainer: View = itemView.findViewById(R.id.milestone_info_container)
        val milestoneSequenceLabel: TextView = infoContainer.findViewById(R.id.milestone_sequence_label)
        val milestoneName: TextView = infoContainer.findViewById(R.id.milestone_name)
        val milestoneDescription: TextView = infoContainer.findViewById(R.id.milestone_description)
        val line1: View = infoContainer.findViewById(R.id.milestone_line_1)
        val line2: View = infoContainer.findViewById(R.id.milestone_line_2)
        val line3: View = infoContainer.findViewById(R.id.milestone_line_3)
        val status: ImageView = infoContainer.findViewById(R.id.milestone_status)
        val map: ImageView = infoContainer.findViewById(R.id.milestone_map)

        val TAG = JourneyMilestonesAdapter::class.java.simpleName

        fun bindView(milestone: Milestone, lastCompletedMilestoneSequence: Int, nextMilestoneSequence: Int, numMilestones: Int) {
            milestoneSequenceLabel.text = itemView.context.getString(R.string.milestone_title, milestone.sequence, numMilestones)
            milestoneName.text = milestone.name
            milestoneDescription.text = milestone.description

            if (milestone.sequence == 0) {
                milestoneSequenceLabel.visibility = View.GONE
                line1.visibility = View.GONE
            } else {
                line1.visibility = View.VISIBLE
                milestoneSequenceLabel.visibility = View.VISIBLE
            }

            if (milestone.description.isEmpty()) {
                milestoneDescription.visibility = View.GONE
            } else {
                milestoneDescription.visibility = View.VISIBLE
            }

            if (milestone.sequence == lastCompletedMilestoneSequence && nexttMilestonePercentageCompletion < 100) {
                showMap(milestone)
            } else {
                val mapUrl = milestone.mapImage
                if (!mapUrl.isNullOrEmpty()) {
                    Log.d(TAG, "hiding sequence=${milestone.sequence} mapUrl=" + mapUrl)
                }

                hideMap()
            }

            if (milestone.sequence <= lastCompletedMilestoneSequence) {
                Log.d(TAG, "setColorsActive ${milestone.sequence}")
                setColorsActive(milestone.sequence)
            } else {
                setColorsInactive()
            }

            Handler().post {
                showJourneyMarker(milestone, lastCompletedMilestoneSequence, nextMilestoneSequence, nexttMilestonePercentageCompletion, infoContainer)
            }

            if (milestone.sequence > 0 && milestone.sequence <= nextMilestoneSequence) {

                itemView.setOnClickListener { view ->
                    clickListener?.let {
                        val position = adapterPosition
                        it.onItemClick(view, position)
                    }
                }
            } else {
                itemView.setOnClickListener(null)
            }
        }

        private fun setColorsActive(sequence: Int) {
            milestoneSequenceLabel.setTextColor(black)
            line1.setBackgroundColor(green)
            line2.setBackgroundColor(green)
            line3.setBackgroundColor(disabledGreyColor)
            status.setBackgroundResource(R.drawable.bg_milestone_green)
            if (sequence == 0) {
                status.setImageResource(R.drawable.bg_milestone_green);
                milestoneName.setTextColor(black)
            } else {
                status.setImageResource(R.drawable.ic_check_white);
                milestoneName.setTextColor(blue)
            }
        }

        private fun setColorsInactive() {
            milestoneSequenceLabel.setTextColor(grey)
            milestoneName.setTextColor(black)
            line1.setBackgroundColor(disabledGreyColor)
            line2.setBackgroundColor(disabledGreyColor)
            status.setBackgroundResource(R.drawable.bg_milestone_grey)
            status.setImageResource(android.R.color.transparent);
        }

        private fun showMap(milestone: Milestone) {
            map.visibility = View.VISIBLE
            //cardView.visibility = View.VISIBLE

            val mapUrl = milestone.mapImage
            if (!mapUrl.isNullOrEmpty()) {
                Log.d(TAG, "sequence=${milestone.sequence} mapUrl=" + mapUrl)

                Glide.with(itemView.context)
                        .load(mapUrl)
                        .into(map)
            }
        }

        private fun hideMap() {
            map.visibility = View.GONE
            cardView.visibility = View.GONE
        }

        private fun showJourneyMarker(milestone: Milestone,
                                      lastCompletedMilestoneSequence: Int,
                                      nextMilestoneSequence: Int,
                                      nexttMilestonePercentageCompletion: Double,
                                      infoContainer: View) {

            val journeyMarker: ImageView = infoContainer.findViewById(R.id.journey_marker)
            val status: ImageView = infoContainer.findViewById(R.id.milestone_status)

            var visible = false
            var gone = false

            if (milestone.sequence < lastCompletedMilestoneSequence) {
                visible = false
            } else if (milestone.sequence == lastCompletedMilestoneSequence) {
                visible = true
            } else {
                gone = true
            }

            when {
                gone -> {
                    journeyMarker.visibility = View.GONE
                }
                visible -> {
                    journeyMarker.visibility = View.VISIBLE
                }
                else -> {
                    journeyMarker.visibility = View.INVISIBLE
                }
            }

            val params = journeyMarker.getLayoutParams() as MarginLayoutParams
            var markerTopMargin = 0;

            if (gone) {
                markerTopMargin = 0
            } else if (visible) {
                markerTopMargin = ((itemView.height * nexttMilestonePercentageCompletion) / 100).toInt()
                //our status marker is offset from the top, so try to position the journey marker by offseting it as well
                val statusOffset = (status.getLayoutParams() as MarginLayoutParams).topMargin
                if (markerTopMargin <= statusOffset + (status.height / 2)) {
                    markerTopMargin = statusOffset
                }
            } else {
                markerTopMargin = itemView.height
                //shrink the marker to 1x1 px so the journey path line touches the botton of the cell
                journeyMarker.layoutParams.height = 1
                journeyMarker.layoutParams.width = 1
            }

            params.topMargin = markerTopMargin
            journeyMarker.layoutParams = params
        }
    }

    interface AdapterViewListener {
        fun onItemClick(view: View, position: Int)
    }

    interface AdapterHost {
        fun onItemSelected(milestone: Milestone)
    }
}