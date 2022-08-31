package com.example.myapplication

import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.myapplication.network.bean.MessageBean
import com.example.myapplication.network.bean.UserInfo
import com.example.myapplication.repository.Repository
import com.example.myapplication.room.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class MyViewModel: ViewModel() {

    private val database by lazy { UserDatabase.getDataBase(TestApplication.instance) }
    private val userRepository by lazy { UserRepository(database.userDao()) }
    private val messageRepository by lazy { MessageRepository(database.MessageDao()) }

    private val _userInfo = MutableLiveData<String>()

    val userInfo:LiveData<Users> = Transformations.switchMap(_userInfo){ name->
        userRepository.getUser(name)
    }
    fun getUser(name:String){
        _userInfo.value = name
    }

    private val _userNetInfo = MutableLiveData<UserInfo>()

    fun getUserId(name:String,pwd:String){
        _userNetInfo.value = UserInfo(name,pwd)
    }

    val userNetInfo= Transformations.switchMap(_userNetInfo){
        it->  Repository.getUserId(it.username,it.password)
    }


    val message = MutableLiveData<String>()

    val messageList = ArrayList<Message>()

    val netState = MutableLiveData<Int>()

    private val _messageInfo = MutableLiveData<String>()

    fun getMessage(sendEmail:String){
        _messageInfo.value = sendEmail
    }

    val messageInfo = Transformations.switchMap(_messageInfo){
        messageRepository.getMessage(it)
    }


    val chatList = ArrayList<String>()



}