package com.lovisgod.sorosoke.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.lovisgod.sorosoke.models.ErrorEvent
import com.lovisgod.sorosoke.repo.AppRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject

class AppViewModel(application: Application): ViewModel() {

    private val commentlist : MutableLiveData<ArrayList<com.lovisgod.sorosoke.models.Comment>> = MutableLiveData()
    val _commentList: LiveData<ArrayList<com.lovisgod.sorosoke.models.Comment>> get() = commentlist
    private val installCount : MutableLiveData<Int> = MutableLiveData()
    val _installCount: LiveData<Int> get() = installCount
    var commentString = ""
    private val _aboutString : MutableLiveData<String> = MutableLiveData()
    val aboutString: LiveData<String> get() = _aboutString

    val easyLoader = com.lovisgod.easyhelper.HtmlLoader()
    private val _priceString : MutableLiveData<String> = MutableLiveData()
    val priceString: LiveData<String> get() = _priceString

   val appRepo by application.inject<AppRepo> ()

    init {
        getAllComments()
        registerInstalled()
        getInstalledCount()
    }


     fun loadHtml(name: String, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val html = easyLoader.loader(context, name)
                    if (name == "about.html") {

                        _aboutString.postValue(html)
                    }

                    if (name == "price.html") {
                        _priceString.postValue(html)
                    }
                } catch(e: Exception) {
                    println(e.localizedMessage)
                }
            }
        }
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
                            val commentList = ArrayList<com.lovisgod.sorosoke.models.Comment>()
                            for (document in it) {
                                val data = document.data
                                val name = data.get("name")
                                val comment = data.get("comment")
                                val time = data.get("time")
                                commentList.add(
                                    com.lovisgod.sorosoke.models.Comment(
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
                        val commentList = ArrayList<com.lovisgod.sorosoke.models.Comment>()
                        for (document in querySnapShot) {
                            val data = document.data
                            val name = data.get("name")
                            val comment = data.get("comment")
                            val time = data.get("time")
                            commentList.add(
                                com.lovisgod.sorosoke.models.Comment(
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
                        .post(ErrorEvent(event = "commentListError", message = it.message!!))
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