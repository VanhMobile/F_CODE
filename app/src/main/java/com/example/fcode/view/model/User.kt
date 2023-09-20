package com.example.fcode.view.model

import com.example.fcode.view.newPassword

data class User(
    val userName: String,
    val password: String,
    val userInformation: UserInformation
){
    constructor() : this("","",UserInformation())
}
