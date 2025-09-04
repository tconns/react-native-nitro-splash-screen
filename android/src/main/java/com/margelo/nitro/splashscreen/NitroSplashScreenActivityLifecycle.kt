package com.margelo.nitro.splashscreen

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log

class NitroSplashScreenActivityLifecycle : ActivityLifecycleCallbacks {
    companion object {
        private const val TAG = "NitroSplashLifecycle"

        @Volatile
        private var INSTANCE: NitroSplashScreenActivityLifecycle? = null

        val instance: NitroSplashScreenActivityLifecycle
            get() {
                return INSTANCE ?: synchronized(this) {
                    INSTANCE ?: NitroSplashScreenActivityLifecycle().also {
                        INSTANCE = it
                        Log.d(TAG, "✅ NitroSplashScreenActivityLifecycle singleton created")
                    }
                }
            }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(TAG, "🏗️ onActivityCreated: ${activity.javaClass.simpleName}")
        NitroSplashScreenManager.instance.setActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "▶️ onActivityStarted: ${activity.javaClass.simpleName}")
        // Update activity reference when activity starts
        NitroSplashScreenManager.instance.setActivity(activity)
        NitroSplashScreenManager.instance.show("light") // Default to light theme; could be dynamic
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "🔄 onActivityResumed: ${activity.javaClass.simpleName}")
        // Optional: Could trigger splash show here based on business logic
        // For now, let manual control handle when to show
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(TAG, "⏸️ onActivityPaused: ${activity.javaClass.simpleName}")
        // DON'T auto-hide on pause - splash should persist across activity transitions
        // if (NitroSplashScreen.instance.isShowing()) {
        //     NitroSplashScreen.instance.hide()
        // }
        Log.d(TAG, "⏸️ Skipping auto-hide on pause")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "⏹️ onActivityStopped: ${activity.javaClass.simpleName}")
        // DON'T auto-hide on stop - let manual control handle this
        // NitroSplashScreen.instance.hide()
        Log.d(TAG, "⏹️ Skipping auto-hide on stop")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(TAG, "💾 onActivitySaveInstanceState: ${activity.javaClass.simpleName}")
        // No specific action needed for splash screen
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "💥 onActivityDestroyed: ${activity.javaClass.simpleName}")

        // Only cleanup if this is the SAME activity that we're currently using
        val currentActivity = NitroSplashScreenManager.instance.getCurrentActivity()
        if (currentActivity == activity) {
            Log.d(TAG, "💥 Cleaning up - destroyed activity matches current activity")
            NitroSplashScreenManager.instance.cleanup()
        } else {
            Log.d(TAG, "💥 NOT cleaning up - destroyed activity is different from current")
        }
    }
}