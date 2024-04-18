package com.example.paysplit.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.paysplit.MainActivity
import com.example.paysplit.R
import com.example.paysplit.SplashActivity
import com.example.paysplit.fragments.HomeFragment
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.internal.notify


class FCMNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let{
            val notify = message.notification
            sendNotification(notify!!.body.toString(),notify.title.toString())

        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
    private fun sendNotification(message : String,title : String){
        val intent= if(FirestoreClass().getCurrentUserID().isNotEmpty()){
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, SplashActivity::class.java)
        }
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingintent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE)
        val channelid = this.resources.getString(R.string.channel_id)
        val defaultsounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationbuilder = NotificationCompat.Builder(
            this,channelid
        ).setSmallIcon(R.drawable.ic_notify)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(defaultsounduri)
            .setAutoCancel(true)
            .setContentIntent(pendingintent)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
        val notificationmanager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelid,"Channel Pay Split title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationmanager.createNotificationChannel(channel)
        }
        notificationmanager.notify(0,notificationbuilder.build())

    }
}