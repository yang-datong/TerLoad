package com.rl.ff_face_detection_terload.presenter

import android.content.Context
import android.util.Log
import com.rl.ff_face_detection_terload.contract.RegisterContract
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.extensions.isValidPassword
import com.rl.ff_face_detection_terload.extensions.isValidUserName
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class RegisterPresenter(val view: RegisterContract.View) : RegisterContract.Presenter {
    private val TAG = "RegisterPresenter"
    override fun register(username: String, pswd: String, confirmPawd: String, context: Context) {
        if (username.isValidUserName() && pswd.isValidPassword() && pswd == confirmPawd) {
            view.onStartRegister()
            //注册到服务器
            registerEaseMob(username, confirmPawd, context)
        } else if (!username.isValidUserName()) view.onUserNameError()
        else if (!pswd.isValidPassword()) view.onPassWordError()
        else if (pswd != confirmPawd) view.onConfirmPassWordError()
    }

    private fun registerEaseMob(username: String, pswd: String, context: Context) {
        doAsync {
            try {
                //注册失败会抛出HyphenateException
                EMClient.getInstance().createAccount(username, pswd);//同步方法
                uiThread {
                    //注册到数据库
                    registerBmob(username, pswd, context)
                    view.onRegisterInSuccess()
                }
            } catch (e: HyphenateException) {
                uiThread {
                    Log.i("TAG", "注册失败: " + e.message)
                    view.onRegisterInFailed(e.message)
                }
            }
        }
    }

    private fun registerBmob(username: String, pswd: String, context: Context) {
//        val user = MyUser(username, confirmPawd)
//        user.save(object : SaveListener<String>() {
//            override fun done(objectId: String?, e: BmobException?) {
//                if (e == null) {
//                } else {
//                    view.onRegisterInFailed()
//                }
//            }
//        })

        GlobalScope.launch {
            val userDao = DB.getInstance(context).userDao()
            val user = userDao.getUserByUsername(username)
            if (user == null) {
                val ret = userDao.addUser(User(null, username, pswd))
                if (ret > 0L) {
                    Log.d(TAG, "用户账号已添加到数据库")
                } else {
                    Log.e(TAG, "用户账号添加到数据库错误，数据库添加:已${ret}个")
                }
            } else {
                val ret = userDao.updateUser(User(null, username, pswd))
                if (ret > 0) {
                    Log.d(TAG, "用户账号已更新到数据库")
                } else {
                    Log.e(TAG, "用户账号更新到数据库错误，数据库更新:已${ret}个")
                }
            }
        }

    }

}