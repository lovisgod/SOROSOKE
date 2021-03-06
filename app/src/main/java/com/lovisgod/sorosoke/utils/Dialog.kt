package com.lovisgod.sorosoke.utils

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.lovisgod.sorosoke.R
import com.lovisgod.sorosoke.viewmodel.AppViewModel

class Dialog {
    @RequiresApi(Build.VERSION_CODES.M)
    fun makeSnack(view: View, message:String, context: Context) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(context.getColor(R.color.colorAccent))
            .show()
    }

    fun makeToast(message: String, context: Context) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        val view = toast.view
        view.background = context.getDrawable(R.drawable.comment_bg)
        toast.show()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun displayInputContactDialog(context: Context, viewmodel: AppViewModel): AlertDialog? {
        val builder = AlertDialog.Builder(context)
        var inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView: View = inflater.inflate( R.layout.data_capture, null )
        val userName = mView.findViewById<TextInputEditText>(R.id.name_input)
        val userEmail = mView.findViewById<TextInputEditText>(R.id.email_input)
        val saveBtn = mView.findViewById<MaterialTextView>(R.id.save_details)

        saveBtn.setOnClickListener {
            if ( userName.text.toString().isNotBlank() || userEmail.text.toString().isNotBlank() ) {
                println(userName.text.toString())
                println(userEmail.text.toString())
                viewmodel.submitUserData(name = userName.text.toString(), email = userEmail.text.toString())
            }
        }

        builder.setView(mView)
        return builder.create()
    }

    fun displayComingSoon(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        var inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView: View = inflater.inflate( R.layout.coming_soon, null )
        builder.setView(mView)
        return builder.create()
    }
}