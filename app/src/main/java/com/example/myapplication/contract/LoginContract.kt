package com.example.myapplication.contract

interface LoginContract {
    interface Presenter: BasePresenter{
        fun login(userName:String , passWord : String)
    }

    interface View{
        fun onUserNameError()
        fun onPassWordError()
        fun onStartLogin()
        fun onLoggedInSuccess()
        fun onLoggedInFailed()
    }


}