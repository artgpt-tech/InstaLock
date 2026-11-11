package com.instalock.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppLockService : AccessibilityService() {

    companion object {
        @Volatile var unlockedPkg: String? = null
        @Volatile var lockShowing = false
    }

    override fun onServiceConnected() {
        unlockedPkg = null
        lockShowing = false
    }

    private fun ignored(p: String) =
        p == packageName || p == "android" || p == "com.android.systemui" ||
            p.contains("inputmethod") || p.contains("keyboard")

    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        if (e == null || e.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = e.packageName?.toString() ?: return
        if (ignored(pkg)) return
        Prefs.setLastPkg(this, pkg)
        if (!Prefs.enabled(this) || pkg !in Prefs.protectedPackages(this)) {
            unlockedPkg = null   // مغادرة التطبيق المحمي = إعادة القفل
            return
        }
        if (unlockedPkg == pkg || lockShowing) return
        lockShowing = true
        startActivity(
            Intent(this, LockActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                .putExtra("pkg", pkg)
        )
    }

    override fun onInterrupt() {}

    override fun onUnbind(intent: Intent?): Boolean {
        unlockedPkg = null
        lockShowing = false
        return super.onUnbind(intent)
    }
}
