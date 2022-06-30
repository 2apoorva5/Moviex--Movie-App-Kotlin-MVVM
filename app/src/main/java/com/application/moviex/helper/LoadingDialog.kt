package com.application.moviex.helper

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import com.application.moviex.R

class LoadingDialog(var activity: Activity) {
    private lateinit var dialog: AlertDialog

    fun startDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_progress, null))
        builder.setCancelable(false)
        dialog = builder.create()
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}