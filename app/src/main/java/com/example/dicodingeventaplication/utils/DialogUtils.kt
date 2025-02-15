package com.example.dicodingeventaplication.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.TextView
import com.example.dicodingeventaplication.R

object DialogUtils {

    // puo op tengah layar
    fun showPopUpErrorDialog(context: Context, message: String?){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.item_error_popup , null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val tvMessege = dialogView.findViewById<TextView>(R.id.tv_error_msg)
        tvMessege.text = message

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // auto dismis setelah 2 detrik
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 2000)
    }
}