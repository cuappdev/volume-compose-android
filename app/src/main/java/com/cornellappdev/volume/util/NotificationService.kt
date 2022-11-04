package com.cornellappdev.volume.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.cornellappdev.volume.MainActivity
import com.cornellappdev.volume.R
import com.cornellappdev.volume.navigation.Routes
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


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
     * Obtains a unique notification ID from the data store and stores the next integer.
     */
    private fun getNextNotifId(): Int = runBlocking {
        val id = userPreferencesStore.data.first().notificationId
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setNotificationId((id + 1) % Int.MAX_VALUE).build()
        }
        return@runBlocking id
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private fun sendNotification(
        data: MutableMap<String, String>
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = getString((R.string.default_notification_channel_id))
        val channel = NotificationChannel(
            channelId,
            packageName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        // What's sent back to the MainActivity depends on the type of the notification
        // received from Firebase. The type is embedded in the data sent for the notification from the backend.
        //
        // Volume leverages deep links to send users to the proper composable from the notification. New
        // deep links must be added to the Android Manifest.
        // See: https://developer.android.com/jetpack/compose/navigation#deeplinks
        val deepLinkIntent: Intent = when (data[NotificationDataKeys.NOTIFICATION_TYPE.key]) {
            NotificationType.NEW_ARTICLE.type -> {
                Intent(
                    Intent.ACTION_VIEW,
                    "volume://${Routes.OPEN_ARTICLE.route}/${data[NotificationDataKeys.ARTICLE_ID.key]}".toUri(),
                    this,
                    MainActivity::class.java
                )
            }
            NotificationType.WEEKLY_DEBRIEF.type -> {
                Intent(
                    Intent.ACTION_VIEW,
                    "volume://${Routes.WEEKLY_DEBRIEF.route}".toUri(),
                    this,
                    MainActivity::class.java
                )
            }
            NotificationType.NEW_MAGAZINE.type -> {
                Intent(
                    Intent.ACTION_VIEW,
                    "volume://${Routes.OPEN_MAGAZINE.route}/${data[NotificationDataKeys.MAGAZINE_ID.key]}".toUri(),
                    this,
                    MainActivity::class.java
                )
            }
            else -> {
                Intent(this, MainActivity::class.java)
            }
        }

        val volumeNotification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_volume_v)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_volume_v
                )
            )
            .setContentTitle(data[NotificationDataKeys.TITLE.key])
            .setContentText(data[NotificationDataKeys.BODY.key])
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    deepLinkIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setGroup(channelId)

        val summaryNotification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Volume")
            .setSmallIcon(R.drawable.ic_volume_v)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setSummaryText("New content on Volume!")
            )
            .setGroup(channelId)
            .setGroupSummary(true)
            .build()

        notificationManager.apply {
            notify(
                getNextNotifId(),
                volumeNotification.build()
            )
            notify(
                -1,
                summaryNotification
            )
        }
    }

    companion object {
        private const val TAG = "NotificationService"
    }
}
