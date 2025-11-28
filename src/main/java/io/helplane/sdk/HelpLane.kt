package io.helplane.sdk

import android.content.Context
import android.content.Intent

/**
 * Main entry point for the HelpLane SDK
 */
object HelpLane {

    private var brandToken: String? = null
    private var baseUrl: String = "https://api.helplane.io"
    private var user: HelpLaneUser? = null

    /**
     * Configure the SDK with your brand token
     *
     * @param brandToken Your HelpLane brand token
     * @param baseUrl Optional custom API base URL (defaults to https://api.helplane.io)
     */
    @JvmStatic
    @JvmOverloads
    fun configure(brandToken: String, baseUrl: String? = null) {
        this.brandToken = brandToken
        baseUrl?.let { this.baseUrl = it }
    }

    /**
     * Identify the current user
     *
     * @param user User information for the chat session
     */
    @JvmStatic
    fun identify(user: HelpLaneUser) {
        this.user = user
    }

    /**
     * Identify the current user using builder pattern
     *
     * @param userId External user ID from your system
     * @param email User's email address
     * @param name User's display name
     * @param phone User's phone number
     * @param tier User tier/plan
     * @param meta Custom metadata
     */
    @JvmStatic
    @JvmOverloads
    fun identify(
        userId: String? = null,
        email: String? = null,
        name: String? = null,
        phone: String? = null,
        tier: String? = null,
        meta: Map<String, Any>? = null
    ) {
        this.user = HelpLaneUser(
            userId = userId,
            email = email,
            name = name,
            phone = phone,
            tier = tier,
            meta = meta
        )
    }

    /**
     * Clear the current user (for logout)
     */
    @JvmStatic
    fun clearUser() {
        this.user = null
    }

    /**
     * Show the chat widget
     *
     * @param context Android context
     */
    @JvmStatic
    fun show(context: Context) {
        val token = brandToken
        if (token == null) {
            android.util.Log.e("HelpLane", "SDK not configured. Call HelpLane.configure(brandToken) first.")
            return
        }

        val intent = Intent(context, HelpLaneChatActivity::class.java).apply {
            putExtra(HelpLaneChatActivity.EXTRA_BRAND_TOKEN, token)
            putExtra(HelpLaneChatActivity.EXTRA_BASE_URL, baseUrl)
            user?.let { putExtra(HelpLaneChatActivity.EXTRA_USER, it) }
        }
        context.startActivity(intent)
    }

    // Internal getters for SDK components
    @JvmStatic
    internal fun getBrandToken(): String? = brandToken

    @JvmStatic
    internal fun getBaseUrl(): String = baseUrl

    @JvmStatic
    internal fun getUser(): HelpLaneUser? = user
}
