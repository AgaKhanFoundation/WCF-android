package com.android.wcf.login

import com.android.wcf.base.BaseMvp

public abstract class WCFActivityPresenter<ViewType : BaseMvp.BaseView> {

    protected var view: ViewType?=null

    open fun onCreate(view: ViewType) {
        this.view = view
    }

    open fun onPause() {}

    open fun onResume() {}

    open fun onDestroy() {
        this.view = null
    }

    open fun onBackPressed() {}

    protected fun haveView(): Boolean {
        return view != null
    }

    class Default () : WCFActivityPresenter<BaseMvp.BaseView>()

    companion object {

        fun createDefault(): Default {
            return Default()
        }
    }
}