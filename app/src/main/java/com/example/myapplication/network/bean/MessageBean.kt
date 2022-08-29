package com.example.myapplication.network.bean

data class MessageBean(
    var from:String?,
    var to:String?,
    var message:String?
){
    constructor():this(null,null,null)
}

