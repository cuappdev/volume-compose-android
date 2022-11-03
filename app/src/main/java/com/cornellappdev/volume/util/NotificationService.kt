package com.cornellappdev.volume.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.cornellappdev.volume.MainActivity
import com.cornellappdev.volume.R
import com.cornellappdev.volume.navigation.Routes
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Configures the NotificationService for Firebase Messaging
 */
class NotificationService : FirebaseMessagingService() {

    /**
     * Represents the keys for the fields of data in the notification
     * data bundle sent from the backend. The same keys are used
     * as keys for the intent bundle to the MainActivity.
     *
     * @property key key name
     * @see [NotificationRepo](https://github.com/cuappdev/volume-backend/blob/main/src/repos/NotificationRepo.ts)
     */
    enum class NotificationDataKeys(val key: String) {
        NOTIFICATION_TYPE("notificationType"),
        ARTICLE_ID("articleID"),
        ARTICLE_URL("articleURL"),
        MAGAZINE_ID("magazineID"),
        MAGAZINE_PDF("magazinePDF"),
        TITLE("title"),
        BODY("body")
    }

    /**
     * Notifications from the backend are identifiable from their type.
     *
     * @property type notification type
     */
    enum class NotificationType(val type: String) {
        WEEKLY_DEBRIEF("weekly_debrief"),
        NEW_ARTICLE("new_article"),
        NEW_MAGAZINE("new_magazine")
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) =
        sendNotification(remoteMessage.data)

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        // TODO If a new token is received outside of the app first being launched,
        //  a new user should technically be created. May be easier to have a backend route to update the device token for a User if there's a uuid in the user preferences.
        runBlocking {
            baseContext.userPreferencesStore.updateData { currentPreferences ->
                currentPreferences.toBuilder().setDeviceToken(token).build()
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private fun sendNotification(
        data: MutableMap<String, String>
    ) {
        Log.d(TAG, data.toString())
        // TODO test out notifications. It leverages Navigation Deep linking, not sure if it works
        // https://developer.android.com/jetpack/compose/navigation#deeplinks
        var deepLinkIntent: Intent? = null

        // What's sent back to the MainActivity depends on the type of the notification
        // received from Firebase. The type is embedded in the data sent for the notification.
        when (data[NotificationDataKeys.NOTIFICATION_TYPE.key]) {
            NotificationType.NEW_ARTICLE.type -> {
                deepLinkIntent = Intent(
                    Intent.ACTION_VIEW,
                    "volume://${Routes.OPEN_ARTICLE.route}/${data[NotificationDataKeys.ARTICLE_ID.key]}".toUri(),
                    this,
                    MainActivity::class.java
                )
//                intent.putExtra(
//                    NotificationDataKeys.NOTIFICATION_TYPE.key,
//                    NotificationType.NEW_ARTICLE.type
//                )
//                intent.putExtra(
//                    NotificationDataKeys.ARTICLE_ID.key,
//                    data[NotificationDataKeys.ARTICLE_ID.key]
//                )
//                intent.putExtra(
//                    NotificationDataKeys.ARTICLE_URL.key,
//                    data[NotificationDataKeys.ARTICLE_URL.key]
//                )
            }
            NotificationType.WEEKLY_DEBRIEF.type -> {
                // We simply just need to identify the type of the notification. The
                // WeeklyDebrief can be retrieved from the UserRepository#GetUser
                deepLinkIntent = Intent(
                    Intent.ACTION_VIEW,
                    "volume://${Routes.WEEKLY_DEBRIEF.route}".toUri(),
                    this,
                    MainActivity::class.java
                )
//                intent.putExtra(
//                    NotificationDataKeys.NOTIFICATION_TYPE.key,
//                    NotificationType.NEW_ARTICLE.type
//                )
            }
            NotificationType.NEW_MAGAZINE.type -> {
                deepLinkIntent = Intent(
                    Intent.ACTION_VIEW,
                    "volume://${Routes.OPEN_MAGAZINE.route}/${data[NotificationDataKeys.MAGAZINE_ID.key]}".toUri(),
                    this,
                    MainActivity::class.java
                )
            }
        }

        val pendingIntent =
            PendingIntent.getActivity(this, 0, deepLinkIntent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString((R.string.default_notification_channel_id))
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.volume_icon)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.volume_icon
                )
            )
            .setContentTitle(data[NotificationDataKeys.TITLE.key])
            .setContentText(data[NotificationDataKeys.BODY.key])
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)

//        if (data.imageUrl != null) {
//            val bitmap: Bitmap? = getBitmapFromUrl(data.imageUrl.toString())
//            notificationBuilder.setStyle(
//                NotificationCompat.BigPictureStyle()
//                    .bigPicture(bitmap)
//            )
//        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        val channel = NotificationChannel(
            channelId,
            packageName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e(TAG, "Error in getting notification image: " + e.localizedMessage)
            null
        }
    }

    companion object {
        private const val TAG = "NotificationService"
    }
}
