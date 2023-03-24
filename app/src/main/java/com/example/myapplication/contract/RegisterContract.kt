package com.example.myapplication.contract

interface RegisterContract {
    interface Presenter : BasePresenter{
        fun register(username: String, pswd: String, confirmPawd: String)
    }

    interface View{
        fun onUserNameError()
        fun onPassWordError()
        fun onConfirmPassWordError()
        fun onStartRegister()
        fun onRegisterInSuccess()
        fun onRegisterInFailed()
    }
}