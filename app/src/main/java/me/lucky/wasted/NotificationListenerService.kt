package me.lucky.wasted

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationListenerService : NotificationListenerService() {
    private lateinit var prefs: Preferences
    private lateinit var admin: DeviceAdminManager

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        prefs = Preferences(this)
        admin = DeviceAdminManager(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return
        val code = prefs.code
        if (!prefs.isServiceEnabled ||
            code == "" ||
            sbn.notification.extras.getString(Notification.EXTRA_TEXT) != code) return
        cancelAllNotifications()
        try {
            admin.lockNow()
            if (prefs.isWipeData) admin.wipeData()
        } catch (exc: SecurityException) {}
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        migrateNotificationFilter(0, null)
    }
}