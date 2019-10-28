package com.android.wcf.home.dashboard

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wcf.R
import com.android.wcf.helper.DistanceConverter
import com.android.wcf.model.Badge
import com.android.wcf.model.BadgeType
import java.text.SimpleDateFormat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_list_badge_item.view.*

class BadgeListAdapter : RecyclerView.Adapter<BadgeListAdapter.BadgeViewHolder>(), BadgeListAdapterMvp.View {

    var badgeList:List<Badge> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeListAdapter.BadgeViewHolder {

        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_list_badge_item, parent, false)

        return BadgeViewHolder(view)
    }

    fun setBadgeData(badgeData: List<Badge>) {
        badgeList = badgeData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return badgeList.size
    }

    override fun onBindViewHolder(viewHolder: BadgeListAdapter.BadgeViewHolder, position: Int) {
        val badgeViewHolder = viewHolder as BadgeViewHolder
        badgeViewHolder.bindView(badgeList[position])

    }

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dateFormatter = SimpleDateFormat("MMM d, yyyy")

        val res:Resources = itemView.resources

        fun bindView(badge: Badge) {
            var title = badge.title
            if (title == null) {
                when (badge.type) {
                    BadgeType.DISTANCE_COMPLETED_50,BadgeType.DISTANCE_COMPLETED_100
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
            itemView.badge_description.visibility = if (title?.length ?:0 > 0) View.VISIBLE else View.GONE

            itemView.badge_date.text = dateFormatter.format((badge.date))
            when (badge.type) {
                BadgeType.LEVEL_SILVER, BadgeType.LEVEL_GOLD, BadgeType.LEVEL_PLATINUM, BadgeType.LEVEL_CHAMPION ->
                    itemView.icon_badge.getLayoutParams().height = res.getDimensionPixelSize(R.dimen.level_badge_image_width)
                else ->
                    itemView.icon_badge.getLayoutParams().height = res.getDimensionPixelSize(R.dimen.badge_image_width)

            }
                Glide.with(itemView.context).load(badge.type.imageRes).into(itemView.icon_badge)
        }
    }
}