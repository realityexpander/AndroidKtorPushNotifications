package com.realityexpander.ktorpushnotifications

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.app.NotificationCompat
import com.onesignal.*
import java.math.BigInteger


class PushApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        OneSignal.setNotificationOpenedHandler(MyNotificationOpenedHandler())

        OneSignal.OSNotificationOpenedHandler {
            println("OneSignalNotificationOpened: $it")

            // This fires when a notification is opened by tapping on it.
            OneSignal.clearOneSignalNotifications()

            // Return true to display the notification.
            // Return false to hide the notification.
            true
        }

        OneSignal.OSRemoteNotificationReceivedHandler(MyNotificationServiceExtension())

        OneSignal.OSNotificationWillShowInForegroundHandler {
            println("OSNotificationWillShowInForegroundHandler: $it")

            // This fires when a notification is shown while the app is in the foreground.
            OneSignal.clearOneSignalNotifications()

            // Return true to display the notification.
            // Return false to hide the notification.
            true
        }

        OneSignal.OSRemoteNotificationReceivedHandler { context, osNotificationReceivedEvent ->
            println("OSRemoteNotificationReceivedHandler: $osNotificationReceivedEvent")
        }

        OneSignal.OSNotificationWillShowInForegroundHandler {
            println("OSNotificationWillShowInForegroundHandler: $it")
        }

        OneSignal.setInAppMessageClickHandler {
            println("setInAppMessageClickHandler: $it")
        }

        OneSignal.clearOneSignalNotifications()

        val device = OneSignal.getDeviceState()
        val notificationPermissionStatus = device?.areNotificationsEnabled()


        // Changes the notification visual attributes before it is displayed.
        OneSignal.setNotificationWillShowInForegroundHandler { notificationReceivedEvent ->
            val notification = notificationReceivedEvent.notification

            // Example of modifying the notification's accent color
            val mutableNotification = notification.mutableCopy()
            mutableNotification.setExtender { builder: NotificationCompat.Builder ->

                val title = notification.title
                val body = notification.body

                builder.setSmallIcon(R.drawable.ic_notification)
                    //.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))
//                    .setColor(Color.RED) // set color of the icon on some Android versions
//                    .setContentTitle(title)
//                    .setTicker(title+title+title+title+title)
//                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setGroup("group")
//                    .setGroupSummary(true)
//                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
//                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)

//                builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))

//                // Sets the accent color to Green on Android 5+ devices.
//                // Accent color controls icon and action buttons on Android 5+.
//                // Accent color does not change app title on Android 10+
//                builder.color = BigInteger("FF00FF00", 16).toInt()
//                // Sets the notification Title to Red
//                val spannableTitle: Spannable = SpannableString(notification.title + " Modified")
//                spannableTitle.setSpan(
//                    ForegroundColorSpan(Color.RED),
//                    0,
//                    notification.title.length,
//                    0
//                )
//                builder.setContentTitle(spannableTitle)
//
//                // Sets the notification Body to Blue
//                val spannableBody: Spannable = SpannableString(notification.body + " Modified")
//                spannableBody.setSpan(
//                    ForegroundColorSpan(Color.BLUE),
//                    0,
//                    notification.body.length,
//                    0
//                )
//                builder.setContentText(spannableBody)

                // Force remove push from Notification Center after 30 seconds
                builder.setTimeoutAfter(30000)

                val notifyIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val notifyPendingIntent = PendingIntent.getActivity(
                    this, 0, notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Add an action
                builder.addAction(
                    NotificationCompat.Action.Builder
//                        (android.R.drawable.arrow_up_float, "EXTRA", null)
                        (R.drawable.ic_notification, "EXTRA", notifyPendingIntent)
                        .build()
                )

                builder
            }

            // Get custom additional data you sent with the notification
            val data = notification.additionalData


            if (true) {
                // Complete with a notification means it will show
                notificationReceivedEvent.complete(mutableNotification);
            } else {
                // Complete with null means don't show a notification.
                notificationReceivedEvent.complete(null);
            }
        }
    }



    companion object {
        private const val ONESIGNAL_APP_ID = "01206360-f412-41b8-b4ed-830f026b4660"
    }
}

// Responds to clicks/swipes from the user on the notification.
class MyNotificationOpenedHandler : OneSignal.OSNotificationOpenedHandler {
    override fun notificationOpened(result: OSNotificationOpenedResult?) {
        println("Notification opened handler: $result")

        val actionId: String = result?.action?.actionId ?: "UNKNOWN"
        val type: String = result?.action?.type?.name ?: "UNKNOWN" // "ActionTaken" | "Opened"

        val title: String = result?.notification?.title ?: "UNKNOWN"

        println("actionId: $actionId, type: $type, title: $title")
    }
}

// Intercepts the notification before it is shown to the user.
class MyNotificationServiceExtension : OneSignal.OSRemoteNotificationReceivedHandler,
        (Context, OSNotificationReceivedEvent) -> Unit {
    override fun remoteNotificationReceived(
        context: Context,
        notificationReceivedEvent: OSNotificationReceivedEvent
    ) {
        // CAN ONLY BE USED TO READ THE VALUES FROM THE NOTIFICATION
        val notification = notificationReceivedEvent.notification

        notificationReceivedEvent.complete(notification)
    }

    override fun invoke(p1: Context, p2: OSNotificationReceivedEvent) {
        println("MyNotificationServiceExtension invoke: $p2")

        this.remoteNotificationReceived(p1, p2)
    }
}
