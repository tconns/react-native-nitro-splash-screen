package com.margelo.nitro.splashscreen

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import java.lang.ref.WeakReference

class NitroSplashScreenManager {
    companion object {
        private const val TAG = "NitroSplashManager"

        @Volatile
        private var INSTANCE: NitroSplashScreenManager? = null

        val instance: NitroSplashScreenManager
            get() {
                return INSTANCE ?: synchronized(this) {
                    INSTANCE ?: NitroSplashScreenManager().also {
                        INSTANCE = it
                        Log.d(TAG, "✅ NitroSplashScreenManager singleton created")
                    }
                }
            }
    }

    // Use WeakReference to prevent memory leaks
    private var activityRef: WeakReference<Activity>? = null
    private var _dialog: Dialog? = null
    private var logoResId: Int = 0
    private var isInitialized = false
    private val mainHandler = Handler(Looper.getMainLooper())

    // Custom setter with tracking
    private var dialog: Dialog?
        get() = _dialog
        set(value) {
            val oldValue = _dialog
            _dialog = value

            // Track all dialog changes with stack trace
            val stackTrace = Thread.currentThread().stackTrace
            val caller = stackTrace.getOrNull(3)?.let { "${it.className}.${it.methodName}:${it.lineNumber}" } ?: "unknown"

            Log.d(TAG, "🔄 Dialog changed: $oldValue -> $value")
            Log.d(TAG, "🔄 Changed by: $caller")
            Log.d(TAG, "🔄 Thread: ${Thread.currentThread().name}")

            if (value == null && oldValue != null) {
                Log.w(TAG, "⚠️ DIALOG SET TO NULL! Previous: $oldValue")
                Log.w(TAG, "⚠️ Stack trace:")
                stackTrace.take(10).forEach { element ->
                    Log.w(TAG, "   at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})")
                }
            }
        }

    /**
     * Set the current activity with proper lifecycle management
     */
    fun setActivity(activity: Activity?) {
        Log.d(TAG, "🏗️ Setting activity: ${activity?.javaClass?.simpleName}")
        Log.d(TAG, "🏗️ Instance hashCode: ${this.hashCode()}")
        Log.d(TAG, "🏗️ Current dialog before setActivity: $dialog")

        // Clear previous activity reference
        activityRef?.clear()

        // Set new activity reference
        activityRef = activity?.let { WeakReference(it) }

        Log.d(TAG, "✅ Activity reference updated")
        Log.d(TAG, "✅ Current dialog after setActivity: $dialog")
    }

    /**
     * Get current activity safely
     */
    private fun getActivitySafely(): Activity? {
        return activityRef?.get()?.takeIf { !it.isFinishing && !it.isDestroyed }
    }

    /**
     * Public method to get current activity (for lifecycle checking)
     */
    fun getCurrentActivity(): Activity? {
        return getActivitySafely()
    }

    /**
     * Load logo resource with error handling
     */
    private fun getLogoResource() {
        try {
            val currentActivity = getActivitySafely()
            if (currentActivity == null) {
                Log.w(TAG, "⚠️ Cannot get logo resource - no current activity")
                logoResId = 0
                return
            }

            logoResId = currentActivity.resources.getIdentifier(
                "splash_logo",
                "drawable",
                currentActivity.packageName
            )

            Log.d(TAG, "🖼️ Logo resource ID: $logoResId")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get logo resource", e)
            logoResId = 0
        }
    }

    /**
     * Resolve theme with validation
     */
    private fun resolveTheme(theme: String?): String {
        val resolvedTheme = when (theme?.lowercase()) {
            "dark" -> "dark"
            "light" -> "light"
            null -> "light" // default
            else -> {
                Log.w(TAG, "⚠️ Unknown theme '$theme', using 'light' as default")
                "light"
            }
        }
        Log.d(TAG, "🎨 Theme resolved: '$theme' -> '$resolvedTheme'")
        return resolvedTheme
    }

    /**
     * Public method for manual splash screen display
     */
    fun showSplashScreen(activity: Activity, theme: String? = null) {
        Log.d(TAG, "📱 showSplashScreen called manually")
        setActivity(activity)
        show(theme)
    }

    /**
     * Show splash screen with proper error handling and UI thread management
     */
    fun show(theme: String?) {
        Log.d(TAG, "🟢 show() called with theme: $theme")
        debugState()

        val currentActivity = getActivitySafely()
        if (currentActivity == null) {
            Log.w(TAG, "⚠️ Cannot show splash - no valid activity")
            return
        }

        try {
            getLogoResource()
            val themeToUse = resolveTheme(theme)

            // Ensure we're on main thread
            Log.d(TAG, "Posting to main handler...")
            mainHandler.post {
                Log.d(TAG, "🧵 Now executing on main thread")
                try {
                    showDialogOnMainThread(currentActivity, themeToUse)
                    // Verify dialog was created
                    Log.d(TAG, "After showDialogOnMainThread - dialog: $dialog")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Failed to show dialog on main thread", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to show splash screen", e)
        }
    }

    /**
     * Create and show dialog on main thread
     */
    private fun showDialogOnMainThread(activity: Activity, theme: String) {
        Log.d(TAG, "🎭 showDialogOnMainThread() called")

        // Dismiss existing dialog first
        dismissDialog()

        if (activity.isFinishing || activity.isDestroyed) {
            Log.w(TAG, "⚠️ Activity is finishing/destroyed, cannot show dialog")
            return
        }

        Log.d(TAG, "🎭 Creating splash dialog with theme: $theme")

        try {
            // Dùng style fullscreen để chắc chắn dialog chiếm toàn màn
            dialog = Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)

                // Inflate layout nếu có
                val splashLayoutId = activity.resources.getIdentifier(
                    "activity_splash",
                    "layout",
                    activity.packageName
                )

                if (splashLayoutId != 0) {
                    val splashView = activity.layoutInflater.inflate(splashLayoutId, null).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        // ⚠️ Không dùng fitsSystemWindows để tránh bị co layout
                        fitsSystemWindows = false
                    }
                    setContentView(splashView)
                    Log.d(TAG, "✅ Splash layout inflated with MATCH_PARENT params")
                } else {
                    Log.w(TAG, "⚠️ splash_screen.xml not found, using programmatic layout")

                    val layout = LinearLayout(activity).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = android.view.Gravity.CENTER
                        setBackgroundColor(
                            if (theme == "dark") Color.BLACK else Color.WHITE
                        )
                    }

                    val logo = ImageView(activity).apply {
                        if (logoResId != 0) {
                            try {
                                setImageResource(logoResId)
                                Log.d(TAG, "✅ Logo image set successfully")
                            } catch (e: Exception) {
                                Log.e(TAG, "❌ Failed to set logo image", e)
                            }
                        } else {
                            Log.w(TAG, "⚠️ No logo resource found")
                        }

                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = android.view.Gravity.CENTER
                        }
                    }

                    layout.addView(logo)
                    setContentView(layout)
                }

                setCancelable(false)

                // Cấu hình window sau khi setContentView
                window?.apply {
                    setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
//                    setBackgroundDrawable(ColorDrawable(Color.BLACK))

                    // Ẩn status + navigation bar
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                    addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        attributes.layoutInDisplayCutoutMode =
                            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                    }
                }
            }

            dialog?.show()
            Log.d(TAG, "✅ Splash dialog shown successfully")
            Log.d(TAG, "Dialog isShowing after show(): ${dialog?.isShowing}")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to create/show dialog", e)
            dialog = null
        }
    }


    /**
     * Hide splash screen with proper cleanup
     */
    fun hide() {
        Log.d(TAG, "🔴 hide() called")
        Log.d(TAG, "Current dialog state: $dialog")
        Log.d(TAG, "Dialog isShowing: ${dialog?.isShowing}")

        mainHandler.post {
            dismissDialog()
        }
    }

    /**
     * Safely dismiss dialog without immediately clearing reference
     */
    private fun dismissDialog() {
        Log.d(TAG, "🗑️ dismissDialog() called")
        Log.d(TAG, "Dialog reference: $dialog")

        try {
            dialog?.let { dlg ->
                Log.d(TAG, "Dialog exists - checking if showing...")
                Log.d(TAG, "Dialog.isShowing(): ${dlg.isShowing}")

                if (dlg.isShowing) {
                    Log.d(TAG, "Dialog is showing - dismissing now...")
                    dlg.dismiss()
                    Log.d(TAG, "✅ Dialog dismissed successfully")
                } else {
                    Log.w(TAG, "⚠️ Dialog exists but was not showing")
                }
            } ?: run {
                Log.w(TAG, "⚠️ No dialog to dismiss - dialog is null")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to dismiss dialog", e)
        }
        // ⚠️ KHÔNG set dialog = null ở đây nữa
    }

    /**
     * Explicitly clear dialog reference (use in cleanup only)
     */
    private fun clearDialogReference() {
        Log.d(TAG, "🗑️ clearDialogReference() called")
        dialog = null
        Log.d(TAG, "Dialog reference cleared")
    }

    /**
     * Cleanup resources when activity is destroyed
     */
    fun cleanup() {
        Log.d(TAG, "🧹 cleanup() called")
        Log.d(TAG, "🧹 Instance hashCode: ${this.hashCode()}")
        Log.d(TAG, "🧹 Current dialog before cleanup: $dialog")

        dismissDialog()
        clearDialogReference() // chỉ clear khi cleanup thực sự
        activityRef?.clear()
        activityRef = null

        Log.d(TAG, "🧹 Cleanup completed")
    }

    /**
     * Check if splash screen is currently showing
     */
    fun isShowing(): Boolean {
        val showing = dialog?.isShowing == true
        Log.d(TAG, "🔍 isShowing() check: $showing (dialog: $dialog)")
        return showing
    }

    /**
     * Debug method to print current state
     */
    fun debugState() {
        Log.d(TAG, "=== DEBUG STATE ===")
        Log.d(TAG, "Dialog reference: $dialog")
        Log.d(TAG, "Dialog isShowing: ${dialog?.isShowing}")
        Log.d(TAG, "Activity reference: ${activityRef?.get()}")
        Log.d(TAG, "Activity valid: ${getActivitySafely() != null}")
        Log.d(TAG, "Logo resource ID: $logoResId")
        Log.d(TAG, "Is initialized: $isInitialized")
        Log.d(TAG, "==================")
    }
}