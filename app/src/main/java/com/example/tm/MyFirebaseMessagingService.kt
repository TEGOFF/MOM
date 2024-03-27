package com.example.tm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.messaging.RemoteMessage
   
const val channelId="notification_channel"
const val channelName="com.example.tm"
class MyFirebaseMessagingService :FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage){
        if(remoteMessage.notification!=null){
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }
    }

    fun generateNotification(title:String, description: String){
        val intent =Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)



        val pendingIntent = PendingIntent.getActivities(this,0, arrayOf(intent),
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        var builder:NotificationCompat.Builder=NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.date_change_task_image)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title,description))

        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager?

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel=NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }

        notificationManager!!.notify(0, builder.build())


    }

    private fun getRemoteView(title: String, description: String): RemoteViews {
        val remoteView = RemoteViews(ApplicationProvider.getApplicationContext<Context>().packageName,
            R.layout.notification)

        remoteView.setTextViewText(R.id.title,title )
        remoteView.setTextViewText(R.id.description,description)
        remoteView.setImageViewResource(R.id.Logo, R.drawable.date_change_task_image)
        return remoteView

    }
}