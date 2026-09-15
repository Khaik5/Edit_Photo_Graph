package com.example.drawcanvas_v2.utils

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

object DialogUtils {

    fun confirm(
        context: Context,
        title: String,
        message: String,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Confirm"
            ) { _, _ ->
                onConfirm()
            }
            .show()
    }

    fun textInput(
        context: Context,
        initial: String,
        onResult: (String) -> Unit,
        onCancel: () -> Unit = {}
    ) {

        val input =
            EditText(context).apply {
                setText(initial)
                setSelection(text.length)
            }

        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit text")
            .setView(input)
            .setNegativeButton("Cancel") { _, _ ->
                onCancel()
            }
            .setPositiveButton(
                "Done"
            ) { _, _ ->

                onResult(
                    input.text
                        .toString()
                        .trim()
                )
            }
            .create()

        dialog.setOnCancelListener { onCancel() }
        dialog.show()
    }
}
