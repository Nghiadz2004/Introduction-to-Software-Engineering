package com.example.librarymanagementsystem.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.librarymanagementsystem.R
import android.widget.Button
import android.widget.TextView

class ErrorDialog(context: Context, message: String,
                  private val onDismissCallback: (() -> Unit)? = null) {

    private val dialog: Dialog = Dialog(context)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_error, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

        val tvMessage = view.findViewById<TextView>(R.id.tvErrorMessage)
        val btnOk = view.findViewById<Button>(R.id.btnOk)

        tvMessage.text = message

        btnOk.setOnClickListener {
            dialog.dismiss()
            onDismissCallback?.invoke()
        }

        dialog.setOnDismissListener {
            onDismissCallback?.invoke()
        }
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}