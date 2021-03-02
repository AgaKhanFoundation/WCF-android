package com.android.wcf.helper.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.viewpager2.widget.ViewPager2

private const val DEFAULT_WIDTH_DP = 12
private const val DEFAULT_HEIGHT_DP = 12
private const val DEFAULT_SPACING_DP = 12

class ViewPager2ChangeHandler(private val context: Context?,
                              private val container: LinearLayout?,
                              private val viewPager: ViewPager2?,
                              @param:DrawableRes private var drawable: Int) {
    private var spacing: Int = 0
    private var width: Int = 0
    private var height: Int = 0
    private var pageCount: Int = 0
    private var initialPage = 0

    init {
        requireNotNull(viewPager?.adapter) { "ViewPager does not have an adapter set on it." }
    }

    fun setPageCount(pageCount: Int) {
        this.pageCount = pageCount
    }

    fun setInitialPage(page: Int) {
        initialPage = page
    }

    fun setDrawable(@DrawableRes drawable: Int) {
        this.drawable = drawable
    }

    fun setSpacingRes(@DimenRes spacingRes: Int) {
        spacing = spacingRes
    }

    fun setIndicatorItemWidth(@DimenRes dimenRes: Int) {
        width = dimenRes
    }

    fun setIndicatorItemHeight(@DimenRes dimenRes: Int) {
        height = dimenRes
    }

    fun show(pageIndex: Int? = null) {
        initIndicators()
        val count = viewPager?.adapter?.itemCount
        val currentPage = if (pageIndex != null && count != null && pageIndex in 0 until count) {
            pageIndex
        } else {
            initialPage
        }
        setIndicatorAsSelected(currentPage)
    }

    private fun initIndicators() {
        if (pageCount <= 0) {
            return
        }
        val res = context?.resources
        container?.removeAllViews()
        for (i in 0 until pageCount) {
            val view = View(context)
            val width = if (this.width != 0) res?.getDimensionPixelSize(this.width) else (res?.displayMetrics?.density?.toInt())?.times(DEFAULT_WIDTH_DP)
            val height = if (this.height != 0) res?.getDimensionPixelSize(this.height) else (res?.displayMetrics?.density?.toInt())?.times(DEFAULT_HEIGHT_DP)
            val margin = if (spacing != 0) res?.getDimensionPixelSize(spacing) else (res?.displayMetrics?.density?.toInt())?.times(DEFAULT_SPACING_DP)

            if (width != null && height != null && margin != null) {
                val lp = LinearLayout.LayoutParams(width, height)
                lp.setMargins(if (i == 0) 0 else margin, 0, 0, 0)
                view.layoutParams = lp
            }
            view.setBackgroundResource(drawable)
            view.isSelected = i == 0
            container?.addView(view)
        }
        showIndicators()
    }

    private fun setIndicatorAsSelected(index: Int) {
        container?.let { ll ->
            for (i in 0 until ll.childCount) {
                val view = ll.getChildAt(i)
                view.isSelected = i == index
            }
        }
    }

    fun onPageSelected(position: Int) {
        val index = position % pageCount
        setIndicatorAsSelected(index)
    }

    fun hideIndicators() {
        if (container?.visibility != View.INVISIBLE) {
            container?.visibility = View.INVISIBLE
        }
    }

    fun showIndicators() {
        if (container?.visibility != View.VISIBLE) {
            container?.visibility = View.VISIBLE
        }
    }
}