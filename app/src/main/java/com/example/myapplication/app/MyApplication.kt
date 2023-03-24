package com.example.myapplication.app

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import cn.bmob.v3.Bmob
import com.example.myapplication.R
import com.example.myapplication.adapter.MessageListenerAdapter
import com.example.myapplication.ui.activity.ChatActivity
import com.hyphenate.chat.*
import org.litepal.LitePal

class MyApplication : Application() {
    private val TAG = "HUNANHUANGJINGYU"
    private var isBackground = true
    private val sp by lazy {
        getSharedPreferences("isAutoLogin", Context.MODE_PRIVATE)
    }

    private val mCallbacks = object : ActivityLifecycleCallbacks{
        override fun onActivityPaused(activity: Activity) {
            Log.i(TAG, "onActivityPaused: ")
            isBackground = true
        }

        override fun onActivityStarted(activity: Activity) {
            Log.i(TAG, "onActivityStarted: ")
        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.i(TAG, "onActivityDestroyed: ")
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            Log.i(TAG, "onActivitySaveInstanceState: ")
        }

        override fun onActivityStopped(activity: Activity) {
            Log.i(TAG, "onActivityStopped: ")
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.i(TAG, "onActivityCreated: ")
        }

        override fun onActivityResumed(activity: Activity) {
            Log.i(TAG, "onActivityResumed: ")
            isBackground = false
        }
    }

    private val msgListener = object : MessageListenerAdapter(){
        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            if (isBackground) {
//                playMediaPlayer()//播放音频
                showNotification(messages)//显示通知栏
            } else {
                playVibrator() //震动
            }
        }
    }

    private fun showNotification(messages: MutableList<EMMessage>?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(NotificationChannel("id", "name", NotificationManager.IMPORTANCE_HIGH))
            var contentText = "非文本消息！"
            var userName = ""
            messages?.forEach {
                if (it.type == EMMessage.Type.TXT) {
                     userName = it.userName
                    contentText = (it.body as EMTextMessageBody).message
                }
            }
            val intent = Intent(applicationContext, ChatActivity::class.java)
            intent.putExtra("username", userName)

            val addNextIntent = TaskStackBuilder.create(applicationContext).addParentStack(ChatActivity::class.java).addNextIntent(intent)
            val pendingIntent = addNextIntent.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            val build = NotificationCompat.Builder(applicationContext, "id")
                    .setContentTitle(userName)
                    .setContentText("$contentText")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_login_3party_wechat)
                    .setChannelId("id").build()
            notificationManager.notify(1,build)
        }
    }

    private fun playVibrator() {
        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500)
    }

    private fun playMediaPlayer() {
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        RingtoneManager.getRingtone(applicationContext,defaultUri).play()
    }

    override fun onCreate() {
        super.onCreate()
        val isAuto = sp.getBoolean("isAuto", true)
        val emOptions = EMOptions()
        emOptions.autoLogin = isAuto
        EMClient.getInstance().init(applicationContext, emOptions)
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG)
        Bmob.initialize(applicationContext, "c063550ad7c3587f4fae8ff7f68deef1");
        LitePal.initialize(applicationContext)
        registerActivityLifecycleCallbacks(mCallbacks)
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }
}

