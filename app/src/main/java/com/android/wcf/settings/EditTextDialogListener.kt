package com.android.wcf.settings

interface EditTextDialogListener {
    fun onDialogDone(editedValue: String)
    fun onDialogCancel()
}