package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.network.UserNetWork
import com.example.myapplication.repository.Repository
import com.example.myapplication.spread.showToast
import com.example.myapplication.spread.startActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityLoginBinding
    private lateinit var sp:SharedPreferences
    companion object{
        lateinit var context: LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context=this
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        sp = getSharedPreferences("TOKEN",Context.MODE_PRIVATE)
        val token = sp.getString("TOKEN","SB")
        if(token!="SB"){
            this.startActivity<MainActivity> {  }
        }
        mBinding.apply {
            button.setOnClickListener {
                val email = editTextTextEmailAddress.text.toString()
                val pwd  = editTextTextPassword.text.toString()
                if(email.isNullOrEmpty()||pwd.isNullOrEmpty()){
                    "用户名或者密码为空".showToast()
                }else{
                    lifecycleScope.launch{
                       val response = withContext(Dispatchers.IO){
                           val result=try {
                               val responseBody = UserNetWork.getUserId(email,pwd).string()
                               Result.success(responseBody)
                           }catch (e:Exception){
                               Result.failure(e)
                           }
                           result.getOrNull()
                        }
                        if(response!=null){
                            when(response){
                                "登陆成功"->{
                                    val intent =
                                        this@LoginActivity.startActivity<MainActivity> {
                                            sp.edit().apply{
                                                putString("TOKEN",email)
                                                apply()
                                            }
                                        }
                                }
                                else->{
                                    response.showToast()
                                }
                            }
                        }else{
                            "网络错误".showToast()
                        }

                    }

                }
            }
        }

    }



}