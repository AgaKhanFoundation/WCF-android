package com.android.wcf.home.challenge.journey

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.wcf.R
import com.android.wcf.model.MediaType
import com.android.wcf.model.Milestone
import com.bumptech.glide.Glide

class MilestoneContentAdapter(context: Context?, val adapterHost: MilestoneContentAdapter.AdapterHost) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_PHOTO = 1
        const val VIEW_TYPE_ARTICLE = 2
        const val VIEW_TYPE_VIDEO = 3
    }

    var mediaList = mutableListOf<MediaContentViewModel>()

    fun setData(milestone: Milestone): Int {
        mediaList.clear()
        if (milestone.content != null && milestone.content.isNotEmpty()) {
            mediaList.add(MediaContentViewModel(VIEW_TYPE_ARTICLE, null, milestone.content))
        }
        for (media in milestone.getMediaContent()) {
            if (media.type == MediaType.ARTICLE) {
                mediaList.add(MediaContentViewModel(VIEW_TYPE_ARTICLE, media.url, null))
            } else if (media.type == MediaType.PHOTO) {
                mediaList.add(MediaContentViewModel(VIEW_TYPE_PHOTO, media.url, null))
            } else if (media.type == MediaType.VIDEO) {
                mediaList.add(MediaContentViewModel(VIEW_TYPE_VIDEO, media.url, null))
            }
        }

        // mediaList.addAll(generateMediaList(milestone))
        notifyDataSetChanged()
        return mediaList.size
    }

    private fun generateMediaList(milestone: Milestone): List<MediaContentViewModel> {
        val mediaList = mutableListOf<MediaContentViewModel>()
        if (milestone.content != null && milestone.content.isNotEmpty()) {
            mediaList.add(MediaContentViewModel(VIEW_TYPE_ARTICLE, null, milestone.content))
        }
        for (media in milestone.getMediaContent()) {
            if (media.type == MediaType.ARTICLE) {
                mediaList.add(MediaContentViewModel(VIEW_TYPE_ARTICLE, media.url, null))
            } else if (media.type == MediaType.PHOTO) {
                mediaList.add(MediaContentViewModel(VIEW_TYPE_PHOTO, media.url, null))
            } else if (media.type == MediaType.VIDEO) {
                mediaList.add(MediaContentViewModel(VIEW_TYPE_PHOTO, media.url, null))
            }
        }
        return mediaList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_PHOTO) {
            return PhotoViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.view_pager_image, parent, false)
            )
        } else if (viewType == VIEW_TYPE_VIDEO) {
            return VideoViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.view_pager_video, parent, false)
            )
        } else {
            return ArticleViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.view_pager_webview, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (mediaList[position].type === VIEW_TYPE_ARTICLE) {
            (holder as ArticleViewHolder).bind(position)
        } else if (mediaList[position].type === VIEW_TYPE_PHOTO) {
            (holder as PhotoViewHolder).bind(position)
        } else if (mediaList[position].type === VIEW_TYPE_VIDEO) {
            (holder as VideoViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mediaList[position].type
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    private inner class PhotoViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

        var photoImageView: ImageView = itemView.findViewById(R.id.media_image)

        fun bind(position: Int) {
            val mediaContentViewModel = mediaList.get(position)
            if (!mediaContentViewModel.url.isNullOrEmpty()) {

                Glide.with(itemView.context)
                        .load(mediaContentViewModel.url)
                        .placeholder(R.drawable.avatar_team)
                        .error(R.drawable.avatar_team)
                        .into(photoImageView)
            }
        }
    }

    private inner class ArticleViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var webview: WebView = itemView.findViewById(R.id.media_webview)

        fun bind(position: Int) {
            webview?.getSettings()?.setJavaScriptEnabled(true)
            webview.setWebChromeClient(WebChromeClient())

            val mediaContentViewModel = mediaList.get(position)
            if (!mediaContentViewModel.url.isNullOrEmpty()) {
                webview.loadUrl(mediaContentViewModel.url)
            } else {
                webview.loadData(mediaContentViewModel.content, "text/html", null)
            }
        }
    }

    private inner class VideoViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var webview: WebView = itemView.findViewById(R.id.media_webview)
//        webview.setWebViewClient( myWebClient());
//        webview.setWebChromeClient( WebChromeClient());
//        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webview.getSettings().setJavaScriptEnabled(true);
//        webview.getSettings().setPluginState(PluginState.ON);
//        webview.getSettings().setDefaultFontSize(18);

        fun bind(position: Int) {
            webview?.getSettings()?.setJavaScriptEnabled(true)
            webview.setWebChromeClient(WebChromeClient())
            val mediaContentViewModel = mediaList.get(position)
            if (!mediaContentViewModel.url.isNullOrEmpty()) {
                webview.loadUrl(mediaContentViewModel.url)
            }
        }
    }

    data class MediaContentViewModel(val type: Int, val url: String?, val content: String?)

    interface AdapterViewListener {
        fun onItemClick(view: View, position: Int)
    }

    interface AdapterHost {
        fun onItemSelected(milestone: Milestone)
    }


}