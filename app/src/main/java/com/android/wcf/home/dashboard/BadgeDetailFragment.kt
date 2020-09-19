package com.android.wcf.home.dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.android.wcf.R
import com.android.wcf.base.BaseFragment
import com.android.wcf.base.ErrorDialogCallback
import com.android.wcf.kotlinExtensions.bitmap
import com.android.wcf.kotlinExtensions.saveToInternalStorage
import com.android.wcf.model.Badge
import com.android.wcf.model.BadgeType
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_badge_detail.view.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class BadgeDetailFragment : BaseFragment(), BadgeDetailMvp.View {

    var host: BadgeDetailMvp.Host? = null
    var badge: Badge? = null
    lateinit var badgeView: View

    companion object {
        val TAG = BadgeDetailFragment::class.java.simpleName
        val ARG_BADGE = "arg_badge"

        fun newInstance(badge: Badge): BadgeDetailFragment {
            val fragment = BadgeDetailFragment()

            val args = Bundle()
            args.putParcelable(ARG_BADGE, badge)

            fragment.setArguments(args)
            return fragment
        }
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_share -> shareBadge()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BadgeDetailMvp.Host) {
            host = context
        } else {
            throw RuntimeException("$context must implement BadgeDetailMvp.Host")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_badge_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        host?.setToolbarTitle(getString(R.string.badge_detail_title), true)

        badge = arguments?.getParcelable(ARG_BADGE)

        setupView(view)

        showBadge()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var handled = super.onOptionsItemSelected(item)
        if (!handled) {
            when (item.itemId) {
                android.R.id.home -> {
                    closeView()
                    handled = true
                }
            }
        }
        return handled
    }


    override fun onDetach() {
        super.onDetach()
        host = null
    }

    override fun onStart() {
        super.onStart()

    }

    fun setupView(fragmentView: View) {
        badgeView = fragmentView.findViewById(R.id.badge_view)
        val shareBtn: Button = fragmentView.findViewById(R.id.btn_share)
        shareBtn.setOnClickListener(onClickListener)
    }

    fun showBadge() {
        badge?.let { badge ->
            val dateFormatter = SimpleDateFormat("MMMM d, yyyy")
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"))

            Glide.with(context!!).load(badge.type?.imageRes).into(badgeView.icon_badge)

            var title = badge.title
            if (title == null) {
                when (badge.type) {
                    BadgeType.DISTANCE_COMPLETED_50, BadgeType.DISTANCE_COMPLETED_100
                        , BadgeType.DISTANCE_COMPLETED_150, BadgeType.DISTANCE_COMPLETED_200
                        , BadgeType.DISTANCE_COMPLETED_250, BadgeType.DISTANCE_COMPLETED_300
                        , BadgeType.DISTANCE_COMPLETED_350, BadgeType.DISTANCE_COMPLETED_400
                        , BadgeType.DISTANCE_COMPLETED_450, BadgeType.DISTANCE_COMPLETED_500 ->
                        title = getString(badge.type.titleRes, "${badge.type.threshold} Miles")
                    else ->
                        title = getString(badge.type.titleRes)
                }
                badgeView.badge_description.text = title

                badgeView.badge_description.visibility = if (title.length ?: 0 > 0) View.VISIBLE else View.GONE

                badgeView.badge_date.text = dateFormatter.format((badge.date))

            }
        }
                ?: run {
                    Log.e(TAG, "No badge data, closing the fragment")
                    closeView()
                }
    }


    internal fun closeView() {
        activity?.onBackPressed()
    }

    fun shareBadge() {
        Log.d(TAG, "Sharing badge ${badge?.type?.name}")
        badge?.let { badge ->

            Log.d(TAG, "Sharing badge ${badge.type?.name}")

            context?.let { context ->

                //convert drawable resource to bitmap
                Toast.makeText(context, "Sharing badge ${badge.type?.name}", Toast.LENGTH_SHORT).show()
                val bitmap = badgeView.bitmap

                bitmap?.let {
                    //save bitmap to app cache folder
                    val uri =  it.saveToInternalStorage(context, "${badge.type?.name}.png");

                    //retrieve the content uri
                    val outputFile = File(context.getCacheDir(), "${badge.type.name}.png");
                    val outPutStream = FileOutputStream(outputFile)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream);
                    outPutStream.flush();
                    outPutStream.close();

                    val contentUri = FileProvider.getUriForFile(context, activity!!.packageName + ".provider", outputFile)

                    try {
                        val title = getString(R.string.badge_share_message_template)
                        val intentBuilder = ShareCompat.IntentBuilder.from(context as Activity)

                        val shareIntent = intentBuilder
                                .setType("image/*")
                                .setText(title)
                                .setSubject(title)
                                .setStream(contentUri)
                                .setChooserTitle("Share Via")
                                .createChooserIntent()

                        if (shareIntent.resolveActivity(activity!!.packageManager) != null) {
                            startActivity(shareIntent)
                        }
                        else {
                            showError(getString(R.string.badge_detail_title)
                                    , getString(R.string.no_app_to_handle_badge_sharing)
                                    , null)
                            Log.e(TAG, "No apps can handle sharing badge")
                        }

                    } catch (e: Exception) {
                        Log.e(tag, " Badge share error: " + e.message)
                    }
                }


            }
        }

    }
}
