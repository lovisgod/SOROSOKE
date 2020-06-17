package com.kodehauz.radiobasar.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.kodehauz.radiobasar.models.ErrorEvent
import com.kodehauz.radiobasar.repo.AppRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.w3c.dom.Comment

class AppViewModel(application: Application): ViewModel() {

    private val commentlist : MutableLiveData<ArrayList<com.kodehauz.radiobasar.models.Comment>> = MutableLiveData()
    val _commentList: LiveData<ArrayList<com.kodehauz.radiobasar.models.Comment>> get() = commentlist
    private val installCount : MutableLiveData<Int> = MutableLiveData()
    val _installCount: LiveData<Int> get() = installCount
    var commentString = ""

   val appRepo by application.inject<AppRepo> ()

    init {
        getAllComments()
        registerInstalled()
        getInstalledCount()
    }


    fun setCommentString() {
        commentString = ""
    }

    fun submitUserData(name: String, email:String) = appRepo.getUserDetails(name, email)

    private fun registerInstalled() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            appRepo.registerUserCount()
        }
    }

    private fun getInstalledCount() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val response = appRepo.getUserCount()
            response.addOnSuccessListener {
                it.data?.let {
                    val count  =  it["count"].toString().toInt(10)
                    installCount.postValue(count + 200)
                }
            }

            response.addOnFailureListener{
                println(it.message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun submitComment(comment: String) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                appRepo.saveComment(comment =  comment)
            }
        }


    private fun getAllComments() =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val comments   = appRepo.getUserComments()
                    comments.get().addOnSuccessListener {
                        if (it != null) {
                            val commentList = ArrayList<com.kodehauz.radiobasar.models.Comment>()
                            for (document in it) {
                                val data = document.data
                                val name = data.get("name")
                                val comment = data.get("comment")
                                val time = data.get("time")
                                commentList.add(
                                    com.kodehauz.radiobasar.models.Comment(
                                        name = name.toString(),
                                        comment = comment.toString(),
                                        time = time.toString()
                                    )
                                )
                            }
                            commentlist.postValue(commentList)
                        }
                    }
                comments.addSnapshotListener { querySnapShot, exception ->
                    if (querySnapShot != null) {
                        val commentList = ArrayList<com.kodehauz.radiobasar.models.Comment>()
                        for (document in querySnapShot) {
                            val data = document.data
                            val name = data.get("name")
                            val comment = data.get("comment")
                            val time = data.get("time")
                            commentList.add(
                                com.kodehauz.radiobasar.models.Comment(
                                    name = name.toString(),
                                    comment = comment.toString(),
                                    time = time.toString()
                                )
                            )
                        }
                        commentlist.postValue(commentList)
                    }
                }

                comments.get().addOnFailureListener {
                    EventBus.getDefault()
                        .post(ErrorEvent(event = "commentListError", message = "${it.message!!}, Please Input your permission to have access"))
                }
            }
        }



    /**
     * Factory for constructing AppViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}