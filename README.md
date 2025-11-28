# HelpLane Android SDK

Add live chat support to your Android app with the HelpLane SDK.

## Requirements

- Android API 21+ (Android 5.0)
- Kotlin 1.5+

## Installation

### Gradle (JitPack)

Add JitPack to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.intigratech:helplane-android-sdk:1.0.0")
}
```

## Quick Start

### 1. Initialize the SDK

```kotlin
import io.helplane.sdk.HelpLane

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        HelpLane.configure(
            context = this,
            brandToken = "your-brand-token"
        )
    }
}
```

### 2. Identify Users (Optional)

```kotlin
val user = HelpLaneUser(
    id = "user-123",
    name = "John Doe",
    email = "john@example.com"
)
HelpLane.identify(user)
```

### 3. Show the Chat

```kotlin
// Open chat activity
HelpLane.openChat(context)

// Or with a custom theme
HelpLane.openChat(context, themeResId = R.style.MyCustomTheme)
```

## Push Notifications

HelpLane uses OneSignal for push notifications. If you're already using OneSignal:

```kotlin
import io.helplane.sdk.HelpLanePush

// After OneSignal initialization
HelpLanePush.registerForPushNotifications(context)

// Handle notification click
OneSignal.setNotificationOpenedHandler { result ->
    if (HelpLanePush.isHelpLaneNotification(result.notification)) {
        HelpLane.openChat(context)
    }
}
```

## Configuration Options

```kotlin
HelpLane.configure(
    context = this,
    brandToken = "your-brand-token",
    baseURL = "https://your-instance.helplane.io"  // Optional: custom instance
)
```

## ProGuard Rules

If you're using ProGuard, add these rules:

```proguard
-keep class io.helplane.sdk.** { *; }
-keepclassmembers class io.helplane.sdk.** { *; }
```

## API Reference

### HelpLane

| Method | Description |
|--------|-------------|
| `configure(context, brandToken, baseURL)` | Initialize the SDK |
| `identify(user)` | Set the current user |
| `clearUser()` | Clear user data |
| `openChat(context)` | Open chat activity |
| `getChatIntent(context)` | Get intent for custom navigation |

### HelpLaneUser

| Property | Type | Description |
|----------|------|-------------|
| `id` | String | Unique user identifier |
| `name` | String? | Display name |
| `email` | String? | Email address |
| `avatarURL` | String? | Profile image URL |
| `customAttributes` | Map<String, Any>? | Custom data |

## Support

- Issues: [GitHub Issues](https://github.com/intigratech/helplane-android-sdk/issues)

## License

MIT License - see [LICENSE](LICENSE) for details.
