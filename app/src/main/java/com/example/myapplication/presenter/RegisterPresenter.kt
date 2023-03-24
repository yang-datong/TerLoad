package com.example.myapplication.presenter

import android.util.Log
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.myapplication.contract.RegisterContract
import com.example.myapplication.emp.MyUser
import com.example.myapplication.extensions.isValidPassword
import com.example.myapplication.extensions.isValidUserName
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class RegisterPresenter(val view:RegisterContract.View) : RegisterContract.Presenter {
    override fun register(username: String, pswd: String, confirmPawd: String) {
        if (username.isValidUserName()&&pswd.isValidPassword()&&pswd.equals(confirmPawd)){
            view.onStartRegister()
            //注册到数据库
            registerBmob(username, pswd, confirmPawd)

        }
        else if (!username.isValidUserName())view.onUserNameError()
        else if (!pswd.isValidPassword())view.onPassWordError()
        else if (!pswd.equals(confirmPawd))view.onConfirmPassWordError()
    }

    private fun registerEaseMob(username: String, pswd: String, confirmPawd: String) {
       doAsync {
           try {

               //注册失败会抛出HyphenateException
               EMClient.getInstance().createAccount(username, confirmPawd);//同步方法
               uiThread {
                   view.onRegisterInSuccess()
               }
           } catch (e: HyphenateException) {
               uiThread {
                   Log.i("TAG", "注册失败: "+e.message)
                   view.onRegisterInFailed()
               }
           }
       }
    }

    private fun registerBmob(username: String, pswd: String, confirmPawd: String) {
        val user = MyUser(username,confirmPawd)
        user.save(object : SaveListener<String>(){
            override fun done(objectId: String?, e: BmobException?) {
                if(e==null){
                    //注册到服务器
                    registerEaseMob(username, pswd, confirmPawd)
                }else{
                    view.onRegisterInFailed()
                }
            }
        })
    }

}