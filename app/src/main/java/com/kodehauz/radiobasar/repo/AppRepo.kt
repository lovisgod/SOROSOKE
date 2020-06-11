package com.kodehauz.radiobasar.repo

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.kodehauz.radiobasar.models.AppEvent
import com.kodehauz.radiobasar.models.Comment
import com.kodehauz.radiobasar.models.ErrorEvent
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
                println(it.user?.email)
                EventBus.getDefault().post(AppEvent(event = "dataCaptureSuccess"))
            }
            .addOnFailureListener {
                EventBus.getDefault()
                    .post(ErrorEvent(event = "dataCaptureError", message = it.message!!))
            }
    }

    fun registerUserCount() {
        if (!Prefs.getBoolean("installed", false)) {
            val response  =userCountRef.update("count", FieldValue.increment(1))
            println(response)
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