package com.kodehauz.radiobasar.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.github.loadingview.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.kodehauz.radiobasar.R
import com.kodehauz.radiobasar.viewmodel.AppViewModel

class Dialog {
    @RequiresApi(Build.VERSION_CODES.M)
    fun makeSnack(view: View, message:String, context: Context) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(context.getColor(R.color.colorAccent))
            .show()
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    fun displayInputContactDialog(context: Context, viewmodel: AppViewModel): AlertDialog? {
        val builder = AlertDialog.Builder(context)
        var inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView: View = inflater.inflate( R.layout.data_capture, null )
        val layoutTitle = mView.findViewById<TextView>(R.id.contact_dialog_title)
        val userName = mView.findViewById<TextInputEditText>(R.id.name_input)
        val userEmail = mView.findViewById<TextInputEditText>(R.id.email_input)
        val saveBtn = mView.findViewById<MaterialTextView>(R.id.save_details)

        saveBtn.setOnClickListener {
            if ( userName.text.toString().isNotBlank() || userEmail.text.toString().isNotBlank() ) {
                println(userName.text.toString())
                println(userEmail.text.toString())
                viewmodel.submitUserData(name = userName.text.toString(), email = userEmail.text.toString())
                Snackbar.make(mView, "Save clicked", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(context.getColor(R.color.colorAccent))
                    .show()
            }
        }

        builder.setView(mView)
        val alertDialog = builder.create()
        return alertDialog
    }
}