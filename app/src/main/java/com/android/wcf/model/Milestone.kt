package com.android.wcf.model

import android.util.Log
import com.google.gson.annotations.SerializedName
import java.util.*

data class Milestone(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("sequence") var sequence: Int = 0,
        @SerializedName("distance") var steps: Int = 999999999,
        @SerializedName("name") var name: String = "",
        @SerializedName("description") var description: String = "",
        @SerializedName("flag_name") var flag_name: String = "",
        @SerializedName("journey_map") var journey_map: String = "",
        @SerializedName("title") var title: String = "",
        @SerializedName("subtitle") var subtitle: String = "",
        @SerializedName("content") var content: String = "",
        @SerializedName("media") var media: String = "") {

    var reached:Boolean = false
    var reachedOn: Date? = null

    fun hasReached(stepsCompleted:Int):Boolean {
        reached = stepsCompleted >= steps
        return reached
    }

    fun getMediaContent():List<Media> {
        val mediaList:MutableList<Media> = mutableListOf()
        val parsedMedia:List<String> = media.split(" ".toRegex(), 2)
        var seq = 0
        for (item in parsedMedia) {
            val parsedItem:List<String> = item.split(":".toRegex(), 2)
            if (parsedItem.size == 2) {
                val mediaType:MediaType =
                        if (parsedItem.get(0).toLowerCase().equals("video")) MediaType.VIDEO
                            else MediaType.PHOTO

                val media = Media(0, seq++, mediaType, parsedItem.get(1)  )
                mediaList.add(media)
            }
            else {
                Log.e(TAG, "media ignored - incorrect format for media item: $item")
            }
        }
        return mediaList
    }

    companion object {
        val TAG = Milestone::class.java.simpleName
    }
}