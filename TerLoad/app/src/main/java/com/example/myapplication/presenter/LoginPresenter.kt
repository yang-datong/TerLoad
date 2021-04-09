package com.example.myapplication.presenter

import android.util.Log
import com.example.myapplication.contract.LoginContract
import com.example.myapplication.extensions.isValidPassword
import com.example.myapplication.extensions.isValidUserName
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient

class LoginPresenter(val view:LoginContract.View) : LoginContract.Presenter {
    override fun login(userName: String, passWord: String) {
        if (userName.isValidUserName()&&passWord.isValidPassword()){
            view.onStartLogin()
            loginEaseMob(userName, passWord,true )  //登录到服务器
        }
        else if (userName.isValidUserName()||userName.isEmpty())
            view.onPassWordError()
        else if (passWord.isValidPassword()||passWord.isEmpty())
            view.onUserNameError()
    }

    private fun loginEaseMob(userName: String, passWord: String,isAutoLogin :Boolean) {
        EMClient.getInstance().login(userName,passWord,object : EMCallBack{
            override fun onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups()
                EMClient.getInstance().chatManager().loadAllConversations()//这是子线程
//                if (isAutoLogin)options.setAutoLogin(false)
                //返回主线程控制UI
                uiThread { view.onLoggedInSuccess() }
            }

            override fun onProgress(progress: Int, status: String?) {}

            override fun onError(code: Int, error: String?) {
                //返回主线程控制UI
                uiThread { view.onLoggedInFailed() }
                Log.i("TAG", "登录失败 "+code+error)
            }
        })
    }
}