package com.example.fcode.view.model

data class UserInformation(
    val fullName: String,
    val avtImg: String,
    val birthDay: String,
    val email: String,
    val numberPhone: String,
    val sex:String
){
    constructor():this("","","","","","")
}
