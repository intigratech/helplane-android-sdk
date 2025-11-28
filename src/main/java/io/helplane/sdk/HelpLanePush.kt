package io.helplane.sdk

import android.util.Log

/**
 * Push notification manager for HelpLane SDK
 *
 * HelpLane uses OneSignal for push notifications. This class provides helpers
 * for integrating OneSignal with HelpLane contacts.
 *
 * ## Setup
 * 1. Add OneSignal SDK to your app
 * 2. Initialize OneSignal in your Application class
 * 3. After chat session starts, call `HelpLanePush.login(contactUUID)`
 *
 * ## Example
 * ```kotlin
 * // In Application.onCreate()
 * OneSignal.initWithContext(this, "YOUR_ONESIGNAL_APP_ID")
 *
 * // After chat session (you'll get contactUUID from the WebSocket)
 * HelpLanePush.login("contact-uuid-from-session")
 *
 * // On logout
 * HelpLanePush.logout()
 * ```
 */
object HelpLanePush {

    private const val TAG = "HelpLanePush"

    private var contactUUID: String? = null

    /**
     * Login to OneSignal with the contact UUID
     * Call this after a chat session is established to receive push notifications
     *
     * This sets the OneSignal external_id to "contact_{uuid}" which allows
     * the HelpLane backend to send targeted notifications.
     *
     * IMPORTANT: You must call OneSignal.login() yourself with the returned external ID:
     * ```kotlin
     * val externalId = HelpLanePush.login(contactUUID)
     * OneSignal.login(externalId)
     * ```
     *
     * @param contactUUID The contact's UUID from HelpLane
     * @return The external ID to use with OneSignal.login()
     */
    @JvmStatic
    fun login(contactUUID: String): String {
        this.contactUUID = contactUUID
        val externalId = "contact_$contactUUID"

        Log.d(TAG, "Login with external ID: $externalId")
        Log.d(TAG, "Call OneSignal.login(\"$externalId\") in your app")

        return externalId
    }

    /**
     * Logout from OneSignal
     * Call this when the user logs out to stop receiving push notifications
     *
     * IMPORTANT: You must call OneSignal.logout() yourself:
     * ```kotlin
     * HelpLanePush.logout()
     * OneSignal.logout()
     * ```
     */
    @JvmStatic
    fun logout() {
        contactUUID = null
        Log.d(TAG, "Logged out - call OneSignal.logout() in your app")
    }

    /**
     * Get the OneSignal external ID for the current contact
     * Use this to call OneSignal.login() in your app
     *
     * @return The external ID string, or null if not logged in
     */
    @JvmStatic
    fun getExternalId(): String? {
        return contactUUID?.let { "contact_$it" }
    }

    /**
     * Check if a push notification is from HelpLane
     *
     * @param data The notification data payload
     * @return True if this is a HelpLane notification
     */
    @JvmStatic
    fun isHelpLaneNotification(data: Map<String, String>): Boolean {
        if (data["helplane"] == "true") return true
        if (data["type"] == "new_message") return true
        return false
    }

    /**
     * Get the conversation ID from a HelpLane notification
     *
     * @param data The notification data payload
     * @return The conversation ID, or null if not present
     */
    @JvmStatic
    fun getConversationId(data: Map<String, String>): String? {
        return data["conversation_id"]
    }

    /**
     * Get the message ID from a HelpLane notification
     *
     * @param data The notification data payload
     * @return The message ID, or null if not present
     */
    @JvmStatic
    fun getMessageId(data: Map<String, String>): String? {
        return data["message_id"]
    }
}
