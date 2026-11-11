package com.instalock.app

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {
    private val BG = 0xFF0A0E17.toInt()
    private val CARD = 0xFF121A2B.toInt()
    private val ACCENT = 0xFF2979FF.toInt()
    private val MUTED = 0xFF8A97B0.toInt()

    private lateinit var swMain: SwitchCompat
    private lateinit var swInsta: SwitchCompat
    private lateinit var swGallery: SwitchCompat
    private lateinit var tvMain: TextView
    private lateinit var tvInsta: TextView
    private lateinit var tvGallery: TextView
    private lateinit var tvPerm: TextView
    private lateinit var tvLast: TextView
    private var binding = false

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun text(t: String, size: Float, color: Int = Color.WHITE, bold: Boolean = false) =
        TextView(this).apply {
            text = t; textSize = size; setTextColor(color)
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

    private fun card() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(16), dp(16), dp(16))
        background = GradientDrawable().apply { setColor(CARD); cornerRadius = dp(16).toFloat() }
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) }
    }

    private fun row(label: TextView, sw: SwitchCompat) = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        addView(label, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(sw)
    }

    private fun button(t: String, onClick: () -> Unit) = Button(this).apply {
        text = t; setTextColor(Color.WHITE)
        background = GradientDrawable().apply { setColor(ACCENT); cornerRadius = dp(12).toFloat() }
        setOnClickListener { onClick() }
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(10) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(24), dp(16), dp(24))
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        root.addView(text("InstaLock", 30f, ACCENT, true).apply { gravity = Gravity.CENTER; textAlignment = View.TEXT_ALIGNMENT_CENTER })
        root.addView(text("حماية تطبيقاتك ببصمة الإصبع", 14f, MUTED).apply { textAlignment = View.TEXT_ALIGNMENT_CENTER })

        swMain = SwitchCompat(this); tvMain = text("", 13f, MUTED)
        val c1 = card()
        c1.addView(row(text("تشغيل الحماية", 18f, Color.WHITE, true), swMain))
        c1.addView(tvMain)
        root.addView(c1)

        swInsta = SwitchCompat(this); swGallery = SwitchCompat(this)
        tvInsta = text("", 12f, MUTED); tvGallery = text("", 12f, MUTED)
        val c2 = card()
        c2.addView(text("التطبيقات المحمية", 16f, Color.WHITE, true))
        c2.addView(row(text("Instagram", 16f), swInsta)); c2.addView(tvInsta)
        c2.addView(row(text("المعرض (Gallery)", 16f).apply { setPadding(0, dp(12), 0, 0) }, swGallery)); c2.addView(tvGallery)
        root.addView(c2)

        tvPerm = text("", 13f, MUTED)
        val c3 = card()
        c3.addView(text("الصلاحيات المطلوبة", 16f, Color.WHITE, true))
        c3.addView(text("يحتاج InstaLock إلى «خدمة إمكانية الوصول» ليعرف متى يُفتح Instagram أو المعرض فيطلب البصمة. لا يقرأ محتوى الشاشة، ولا يجمع بيانات، ولا يتصل بالإنترنت.\n\nالتفعيل: الإعدادات ← إمكانية الوصول ← التطبيقات المثبّتة ← InstaLock ← تشغيل.", 13f, MUTED))
        c3.addView(tvPerm)
        c3.addView(button("فتح إعدادات إمكانية الوصول") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) })
        root.addView(c3)

        val c4 = card()
        c4.addView(text("إعدادات البصمة", 16f, Color.WHITE, true))
        c4.addView(text("تُدار البصمات بالكامل من نظام أندرويد؛ التطبيق لا يخزنها.", 13f, MUTED))
        c4.addView(button("فتح إعدادات الأمان") { startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS)) })
        root.addView(c4)

        tvLast = text("", 12f, MUTED)
        val c5 = card()
        c5.addView(text("تشخيص: آخر تطبيق تم رصده", 14f, Color.WHITE, true))
        c5.addView(tvLast)
        c5.addView(text("افتح المعرض ثم عد إلى هنا؛ إن اختلف اسم حزمته عن القائمة المدعومة فأخبرني به.", 12f, MUTED))
        root.addView(c5)

        setContentView(ScrollView(this).apply { setBackgroundColor(BG); addView(root) })

        swMain.setOnCheckedChangeListener { _, on ->
            if (binding) return@setOnCheckedChangeListener
            if (on) {
                if (!Auth.available(this)) { toast("سجّل بصمة في إعدادات الهاتف أولًا"); refresh(); return@setOnCheckedChangeListener }
                if (!accessibilityOn()) { toast("فعّل خدمة إمكانية الوصول أولًا"); refresh(); return@setOnCheckedChangeListener }
                Prefs.setEnabled(this, true); refresh()
            } else confirm { Prefs.setEnabled(this, false) }
        }
        swInsta.setOnCheckedChangeListener { _, on ->
            if (binding) return@setOnCheckedChangeListener
            if (on) { Prefs.setInstagram(this, true); refresh() } else confirm { Prefs.setInstagram(this, false) }
        }
        swGallery.setOnCheckedChangeListener { _, on ->
            if (binding) return@setOnCheckedChangeListener
            if (on) { Prefs.setGallery(this, true); refresh() } else confirm { Prefs.setGallery(this, false) }
        }
    }

    // إيقاف أي حماية يتطلب بصمة، لحماية الإعدادات من التغيير غير المصرّح به
    private fun confirm(apply: () -> Unit) {
        if (!Auth.available(this)) { refresh(); return }
        Auth.prompt(this, "تأكيد الهوية لتغيير الإعدادات",
            onOk = { apply(); refresh() }, onFail = { refresh() })
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()

    private fun accessibilityOn(): Boolean {
        val s = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
        return s.contains("$packageName/$packageName.AppLockService") ||
            s.contains("$packageName/.AppLockService")
    }

    override fun onResume() { super.onResume(); refresh() }

    private fun refresh() {
        binding = true
        val acc = accessibilityOn()
        val on = Prefs.enabled(this) && acc
        swMain.isChecked = on
        swInsta.isChecked = Prefs.instagram(this)
        swGallery.isChecked = Prefs.gallery(this)
        tvMain.text = if (on) "الحماية تعمل" else "الحماية متوقفة"
        tvInsta.text = if (on && Prefs.instagram(this)) "محمي ✓" else "غير محمي"
        tvGallery.text = if (on && Prefs.gallery(this)) "محمي ✓" else "غير محمي"
        tvPerm.text = (if (acc) "خدمة إمكانية الوصول: مفعّلة ✓" else "خدمة إمكانية الوصول: غير مفعّلة ✗") + "\n" +
            (if (Auth.available(this)) "البصمة: جاهزة ✓" else "البصمة: غير مسجّلة ✗")
        tvLast.text = Prefs.lastPkg(this)
        binding = false
    }
}
