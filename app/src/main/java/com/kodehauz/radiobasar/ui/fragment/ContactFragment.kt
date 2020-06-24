package com.kodehauz.radiobasar.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.kodehauz.radiobasar.R
import com.kodehauz.radiobasar.viewmodel.AppViewModel

class ContactFragment : Fragment() {

    private val viewModel: AppViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, AppViewModel.Factory(activity.application))
            .get(AppViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact, container, false)
        val userName = view.findViewById<TextInputEditText>(R.id.fullName)
        val userEmail = view.findViewById<TextInputEditText>(R.id.email)
        val usermessage = view.findViewById<TextInputEditText>(R.id.message)
        val sendBtn = view.findViewById<Button>(R.id.sendBtn)

        sendBtn.setOnClickListener {
            if (userEmail.text.toString().isEmpty() ||
                userName.text.toString().isEmpty() || usermessage.text.toString().isEmpty()) {
              setError(arrayOf(userName, userEmail, usermessage))
            } else {
                sendEmail(userName.text.toString(), usermessage.text.toString())
                setEmpty(arrayOf(userEmail, userName, usermessage))
            }
        }
        return view
    }

    private fun sendEmail(name: String, message: String) {
        val to = arrayOf("Basarradio@gmail.com")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, name)
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here")
        try {
          startActivity(Intent.createChooser(emailIntent, "Send mail..."))
        } catch (ex: Exception) {
            Toast.makeText(
                this.requireActivity(),
                "There is no email client installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setError(elements: Array<TextInputEditText>) {
        for (element in elements) {
            element.requestFocus()
            element.error = "This cannot be empty"
        }

    }

    private fun setEmpty(elements: Array<TextInputEditText>) {
        for (x in elements) {
            x.setText("")
        }
    }

}