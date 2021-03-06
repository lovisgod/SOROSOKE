package com.lovisgod.sorosoke.repo

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.lovisgod.sorosoke.models.AppEvent
import com.lovisgod.sorosoke.models.Comment
import com.lovisgod.sorosoke.models.ErrorEvent
import com.pixplicity.easyprefs.library.Prefs
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppRepo() {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val userCountRef = db.collection("installationCount").document("userNumber")
    val commentRef = db.collection("comments")

    fun getUserDetails(name: String, email: String) {
        auth.createUserWithEmailAndPassword(email, name)
            .addOnSuccessListener {
                EventBus.getDefault().post(AppEvent(event = "dataCaptureSuccess"))
            }
            .addOnFailureListener { message ->
                message.localizedMessage.let {
                    if (it!!.contains("The email address is already in use by another account.")) {
                        loginUser(name, email)
                    } else {
                        EventBus.getDefault()
                            .post(ErrorEvent(event = "dataCaptureError", message = message.message!!))
                        println(message.localizedMessage)
                    }
                }

            }
    }

    fun loginUser(name: String, email: String) {
        auth.signInWithEmailAndPassword(email, name)
            .addOnSuccessListener {
                EventBus.getDefault().post(AppEvent(event = "dataCaptureSuccess"))
            }
            .addOnFailureListener {
                EventBus.getDefault()
                    .post(ErrorEvent(event = "dataCaptureError", message = it.message!!))
                println(it.localizedMessage)
            }
    }

    fun registerUserCount() {
        if (!Prefs.getBoolean("installed", false)) {

                val response  =userCountRef.update("count", FieldValue.increment(1))
                response.addOnSuccessListener {
                    Prefs.putBoolean("installed", true)
                }

                response.addOnFailureListener {
                    println(it.localizedMessage)
                }


        }
    }

    fun getUserCount(): Task<DocumentSnapshot> {
        return userCountRef.get()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveComment(comment: String) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val name = auth.currentUser?.email
        val time = current.format(formatter)
        var userComment = Comment(
            name = name!!,
            time = time,
            comment = comment
        )
        commentRef.add(userComment)
            .addOnSuccessListener {
                EventBus.getDefault().post(AppEvent(event = "commentSubmitSuccess"))
            }
            .addOnFailureListener {
                EventBus.getDefault()
                    .post(ErrorEvent(event = "commentSaveError", message = it.message!!))
            }
    }

    fun getUserComments(): CollectionReference {
        return commentRef
    }

}