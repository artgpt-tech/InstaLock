package com.instalock.app

import android.content.Context

object Prefs {
    // حزمة المعرض تختلف حسب الشركة المصنّعة؛ عدّل القائمة إن لزم.
    val INSTAGRAM = setOf("com.instagram.android")
    val GALLERY = setOf(
        "com.gallery20", "com.google.android.apps.photosgo", "com.google.android.apps.photos",
        "com.sec.android.gallery3d", "com.miui.gallery", "com.android.gallery3d",
        "com.coloros.gallery3d", "com.vivo.gallery", "com.huawei.photos"
    )

    private fun sp(c: Context) = c.getSharedPreferences("instalock", Context.MODE_PRIVATE)
    fun enabled(c: Context) = sp(c).getBoolean("enabled", false)
    fun setEnabled(c: Context, v: Boolean) = sp(c).edit().putBoolean("enabled", v).apply()
    fun instagram(c: Context) = sp(c).getBoolean("instagram", true)
    fun setInstagram(c: Context, v: Boolean) = sp(c).edit().putBoolean("instagram", v).apply()
    fun gallery(c: Context) = sp(c).getBoolean("gallery", true)
    fun setGallery(c: Context, v: Boolean) = sp(c).edit().putBoolean("gallery", v).apply()
    fun lastPkg(c: Context) = sp(c).getString("last_pkg", "—") ?: "—"
    fun setLastPkg(c: Context, v: String) = sp(c).edit().putString("last_pkg", v).apply()

    fun protectedPackages(c: Context): Set<String> {
        val s = HashSet<String>()
        if (instagram(c)) s.addAll(INSTAGRAM)
        if (gallery(c)) s.addAll(GALLERY)
        return s
    }
}
