package com.android.wcf.home.challenge.journey

import android.content.Context
import android.os.Handler
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


class JourneyMilestonesAdapter(context: Context, val adapterHost: AdapterHost) : RecyclerView.Adapter<JourneyMilestonesAdapter.MilestoneViewHolder>() {
    var milestoneList: List<Milestone> = listOf()
    var numMilestones = 0
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

    fun setData(data: List<Milestone>, nextMilestoneSequence: Int, nextMilestonePercentageCompletion: Double) {
        milestoneList = data
        numMilestones = milestoneList.size - 1
        this.nextMilestoneSequence = nextMilestoneSequence
        this.nextMilestonePercentageCompletion = nextMilestonePercentageCompletion

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return milestoneList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilestoneViewHolder {

        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_list_journey_milestone_item, parent, false)

        return MilestoneViewHolder(view, itemClickedListener, blackColor, greyColor, blueColor, greenColor, disabledGreyColor,
                nextMilestoneSequence, nextMilestonePercentageCompletion)
    }

    override fun onBindViewHolder(viewHolder: MilestoneViewHolder, position: Int) {
        viewHolder.bindView(milestoneList[position], nextMilestoneSequence, numMilestones)
    }

    class MilestoneViewHolder(itemView: View, val clickListener: AdapterViewListener?,
                              val black: Int, val grey: Int, val blue: Int, val green: Int, val disabledGreyColor: Int,
                              val nextMilestoneSequence: Int,
                              val currentMilestonePercentageCompletion: Double) : RecyclerView.ViewHolder(itemView) {
        val milestoneSequenceLabel: TextView = itemView.findViewById(R.id.milestone_sequence_label)
        val infoContainer: View = itemView.findViewById(R.id.milestone_info_container)
        val milestoneName: TextView = itemView.findViewById(R.id.milestone_name)
        val line1: View = itemView.findViewById(R.id.milestone_line_1)
        val line2: View = itemView.findViewById(R.id.milestone_line_2)
        val status: ImageView = itemView.findViewById(R.id.milestone_status)
        val journeyMarker: ImageView = itemView.findViewById(R.id.journey_marker)
        val map: ImageView = itemView.findViewById(R.id.milestone_map)
        val cardView: CardView = itemView.findViewById(R.id.milestone_cardview)

        fun bindView(milestone: Milestone, nextMilestoneSequence: Int, numMilestones: Int) {
            updateView(milestone, nextMilestoneSequence, numMilestones)
        }

        private fun updateView(milestone: Milestone, nextMilestoneSequence: Int, numMilestones: Int) {
            milestoneSequenceLabel.text = itemView.context.getString(R.string.milestone_title, milestone.sequence, numMilestones)
            milestoneName.text = milestone.name

            if (milestone.sequence == 0) {
                milestoneSequenceLabel.visibility = View.GONE
                line1.visibility = View.GONE
            } else {
                line1.visibility = View.VISIBLE
                milestoneSequenceLabel.visibility = View.VISIBLE
            }

            if (milestone.sequence > 0 && milestone.sequence < nextMilestoneSequence) {

                itemView.setOnClickListener { view ->
                    clickListener?.let {
                        val position = adapterPosition
                        it.onItemClick(view, position)
                    }
                }
            } else {
                itemView.setOnClickListener(null)
            }

            if (milestone.sequence == nextMilestoneSequence) {
                showJourneyMarker(currentMilestonePercentageCompletion)
            } else {
                hideJourneyMarker()
            }

            if (milestone.sequence == nextMilestoneSequence && currentMilestonePercentageCompletion < 100) {
                showMap()
            } else {
                hideMap()
            }

            if (milestone.sequence <= nextMilestoneSequence) {
                setColorsActive(milestone.sequence)
            } else {
                setColorsInactive()
            }
        }

        private fun setColorsActive(sequence: Int) {
            milestoneSequenceLabel.setTextColor(black)
            line1.setBackgroundColor(green)
            line2.setBackgroundColor(green)
            status.setBackgroundResource(R.drawable.bg_milestone_green)
            if (sequence == 0) {
                status.setImageResource(android.R.color.transparent);
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

        private fun showMap() {
            map.visibility = View.VISIBLE
            //cardView.visibility = View.VISIBLE
        }

        private fun hideMap() {
            map.visibility = View.GONE
            cardView.visibility = View.GONE
        }

        private fun showJourneyMarker(pct: Double) {
            Handler().post({
                journeyMarker.visibility = View.VISIBLE
                val params = journeyMarker.getLayoutParams() as MarginLayoutParams
                params.topMargin = (itemView.height * pct / 100 - journeyMarker.height * 1.0).toInt()
                journeyMarker.layoutParams = params
                journeyMarker.requestLayout()
            })
        }

        private fun hideJourneyMarker() {
            journeyMarker.visibility = View.GONE
        }

    }

    interface AdapterViewListener {
        fun onItemClick(view: View, position: Int)
    }

    interface AdapterHost {
        fun onItemSelected(milestone: Milestone)
    }
}