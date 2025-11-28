package io.helplane.sdk

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * User information for HelpLane chat sessions
 */
@Parcelize
data class HelpLaneUser(
    /** External user ID from your system */
    val userId: String? = null,

    /** User's email address */
    val email: String? = null,

    /** User's display name */
    val name: String? = null,

    /** User's phone number */
    val phone: String? = null,

    /** User tier/plan (e.g., "free", "pro", "enterprise") */
    val tier: String? = null,

    /** Custom metadata */
    val meta: @RawValue Map<String, Any>? = null
) : Parcelable {

    /**
     * Builder pattern for Java interoperability
     */
    class Builder {
        private var userId: String? = null
        private var email: String? = null
        private var name: String? = null
        private var phone: String? = null
        private var tier: String? = null
        private var meta: Map<String, Any>? = null

        fun userId(userId: String?) = apply { this.userId = userId }
        fun email(email: String?) = apply { this.email = email }
        fun name(name: String?) = apply { this.name = name }
        fun phone(phone: String?) = apply { this.phone = phone }
        fun tier(tier: String?) = apply { this.tier = tier }
        fun meta(meta: Map<String, Any>?) = apply { this.meta = meta }

        fun build() = HelpLaneUser(
            userId = userId,
            email = email,
            name = name,
            phone = phone,
            tier = tier,
            meta = meta
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
