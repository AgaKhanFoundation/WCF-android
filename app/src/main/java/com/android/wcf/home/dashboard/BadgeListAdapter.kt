package com.android.wcf.home.dashboard

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wcf.R
import com.android.wcf.model.Badge
import com.android.wcf.model.BadgeType
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_list_badge_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class BadgeListAdapter(val adapterHost: AdapterHost) : RecyclerView.Adapter<BadgeListAdapter.BadgeViewHolder>(), BadgeListAdapterMvp.View {

    var badgeList: List<Badge> = arrayListOf()

    val itemClickedListener = object : AdapterViewListener {
        override fun onItemClick(view: View, position: Int) {
            when (view.id) {
                R.id.icon_badge -> {
                }
                R.id.badge_description -> {
                }
                R.id.badge_date -> {
                }
                else -> {
                    val pos = view.getTag(R.integer.badge_row_num_tag) as Int
                    adapterHost?.onItemSelected(badgeList.get(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {

        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_list_badge_item, parent, false)

        return BadgeViewHolder(view, itemClickedListener)
    }

    fun setBadgeData(badgeData: List<Badge>) {
        badgeList = badgeData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return badgeList.size
    }

    override fun onBindViewHolder(viewHolder: BadgeViewHolder, position: Int) {
        viewHolder.bindView(badgeList[position], position)
    }

    class BadgeViewHolder(itemView: View, val clickListener: AdapterViewListener?) : RecyclerView.ViewHolder(itemView) {

        companion object {
            val dateFormatter = initDateFormatter();

            fun initDateFormatter(): SimpleDateFormat {
                val dateFormatter =
                        SimpleDateFormat("MMM d, yyyy")
                dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"))
                return dateFormatter
            }
        }

        val res: Resources = itemView.resources

        fun bindView(badge: Badge, position: Int) {
            var title = badge.title
            if (title == null) {
                when (badge.type) {
                    BadgeType.DISTANCE_COMPLETED_50, BadgeType.DISTANCE_COMPLETED_100
                        , BadgeType.DISTANCE_COMPLETED_150, BadgeType.DISTANCE_COMPLETED_200
                        , BadgeType.DISTANCE_COMPLETED_250, BadgeType.DISTANCE_COMPLETED_300
                        , BadgeType.DISTANCE_COMPLETED_350, BadgeType.DISTANCE_COMPLETED_400
                        , BadgeType.DISTANCE_COMPLETED_450, BadgeType.DISTANCE_COMPLETED_500 ->
                        title = res.getString(badge.type.titleRes, "${badge.type.threshold} Miles")
                    else ->
                        title = res.getString(badge.type.titleRes)
                }
            }
            itemView.badge_description.text = title
            itemView.badge_description.visibility = if (title?.length ?: 0 > 0) View.VISIBLE else View.GONE

            if (badge.date != null) {
                itemView.badge_date.text = dateFormatter.format((badge.date))
                itemView.badge_date.visibility = View.VISIBLE
            }
            else {
                itemView.badge_date.visibility = View.INVISIBLE
            }
            when (badge.type) {
                BadgeType.LEVEL_SILVER, BadgeType.LEVEL_GOLD, BadgeType.LEVEL_PLATINUM, BadgeType.LEVEL_CHAMPION ->
                    itemView.icon_badge.getLayoutParams().height = res.getDimensionPixelSize(R.dimen.level_badge_image_medium_width)
                else ->
                    itemView.icon_badge.getLayoutParams().height = res.getDimensionPixelSize(R.dimen.badge_image_small_width)

            }
            Glide.with(itemView.context).load(badge.type.imageRes).into(itemView.icon_badge)
            itemView.setOnClickListener({ view ->
                clickListener?.let {
                    val position = adapterPosition
                    it.onItemClick(view, position)
                }
            })

            itemView.setTag(R.integer.badge_row_num_tag, position)

        }
    }

    interface AdapterViewListener {
        fun onItemClick(view: View, position: Int)
    }

    interface AdapterHost {
        fun onItemSelected(badge: Badge)
    }
}