package com.android.wcf.home.challenge.journey

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.wcf.R
import com.android.wcf.model.Milestone

class JourneyMilestonesAdapter(context: Context, val adapterHost: AdapterHost) : RecyclerView.Adapter<JourneyMilestonesAdapter.MilestoneViewHolder>() {
    var milestoneList: List<Milestone> = listOf()
    var numMilestones = 0
    var currentMilestoneSequence: Int = 0
    var currentMilestonePercentageCompletion: Double = 0.0
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilestoneViewHolder {

        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_list_journey_milestone_item, parent, false)

        return MilestoneViewHolder(view, itemClickedListener, blackColor, greyColor, blueColor, greenColor, disabledGreyColor)
    }

    fun setData(data: List<Milestone>, currentMilestoneSequence: Int, currentMilestonePercentageCompletion: Double) {
        milestoneList = data
        numMilestones = milestoneList.size - 1
        this.currentMilestoneSequence = currentMilestoneSequence
        this.currentMilestonePercentageCompletion = currentMilestonePercentageCompletion

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return milestoneList.size
    }

    override fun onBindViewHolder(viewHolder: MilestoneViewHolder, position: Int) {
        viewHolder.bindView(milestoneList[position], currentMilestoneSequence, numMilestones)
    }

    class MilestoneViewHolder(itemView: View, val clickListener: AdapterViewListener?, val black: Int, val grey: Int, val blue: Int, val green: Int, val disabledGreyColor: Int) : RecyclerView.ViewHolder(itemView) {
        val startText: TextView = itemView.findViewById(R.id.milestone_start)
        val titleText: TextView = itemView.findViewById(R.id.milestone_title)
        val infoContainer: View = itemView.findViewById(R.id.milestone_info_container)
        val location: TextView = itemView.findViewById(R.id.milestone_location)
        val line: View = itemView.findViewById(R.id.milestone_line)
        val status: ImageView = itemView.findViewById(R.id.milestone_status)
        val map: ImageView = itemView.findViewById(R.id.milestone_map)
        val cardView: CardView = itemView.findViewById(R.id.milestone_cardview)

        fun bindView(milestone: Milestone, currentMilestone: Int, numMilestones: Int) {
            when {
                milestone.sequence == 0 -> {
                    infoContainer.visibility = View.INVISIBLE
                    map.visibility = View.GONE
                    startText.visibility = View.VISIBLE
                }
                (milestone.sequence < currentMilestone) -> {
                    populateData(milestone, numMilestones)
                    setColorsActive()
                    hideMap()
                }
                (milestone.sequence == currentMilestone) -> {
                    populateData(milestone, numMilestones)
                    setColorsActive()
                    showMap()
                    //todo progression bar for milestone
                }
                (milestone.sequence >= currentMilestone + 1) -> {
                    populateData(milestone, numMilestones)
                    setColorsInactive()
                    hideMap()
                }
            }
        }

        private fun setColorsActive() {
            titleText.setTextColor(black)
            location.setTextColor(blue)
            line.setBackgroundColor(green)
            status.setBackgroundResource(R.drawable.bg_milestone_green)
            status.setImageResource(android.R.color.transparent);

        }

        private fun setColorsInactive() {
            titleText.setTextColor(grey)
            location.setTextColor(black)
            line.setBackgroundColor(disabledGreyColor)
            status.setBackgroundResource(R.drawable.bg_milestone_grey)
            status.setImageResource(android.R.color.transparent);

        }

        private fun showMap() {
            map.visibility = View.VISIBLE
            cardView.visibility = View.VISIBLE
        }

        private fun hideMap() {
            map.visibility = View.GONE
            cardView.visibility = View.GONE
        }

        private fun populateData(milestone: Milestone, numMilestones: Int) {
            infoContainer.visibility = View.VISIBLE
            startText.visibility = View.GONE

            titleText.text = itemView.context.getString(R.string.milestone_title, milestone.sequence, numMilestones)
            location.text = milestone.name

            itemView.setOnClickListener { view ->
                clickListener?.let {
                    val position = adapterPosition
                    it.onItemClick(view, position)
                }
            }
        }
    }

    interface AdapterViewListener {
        fun onItemClick(view: View, position: Int)
    }

    interface AdapterHost {
        fun onItemSelected(milestone: Milestone)
    }
}