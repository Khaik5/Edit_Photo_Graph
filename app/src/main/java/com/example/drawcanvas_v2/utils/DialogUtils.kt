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
        onResult: (String) -> Unit
    ) {

        val input =
            EditText(context).apply {
                setText(initial)
                setSelection(text.length)
            }

        AlertDialog.Builder(context)
            .setTitle("Edit text")
            .setView(input)
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Done"
            ) { _, _ ->

                onResult(
                    input.text
                        .toString()
                        .trim()
                )
            }
            .show()
    }
}