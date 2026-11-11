package com.instalock.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class LockActivity : AppCompatActivity() {
    private var finished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pkg = intent.getStringExtra("pkg")
        setContentView(TextView(this).apply {
            text = "InstaLock"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setBackgroundColor(0xFF0A0E17.toInt())
        })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = goHome()
        })
        if (!Auth.available(this)) {
            Toast.makeText(this, "لا توجد بصمة مسجّلة على الهاتف", Toast.LENGTH_LONG).show()
            goHome(); return
        }
        Auth.prompt(this, "تأكيد الهوية لفتح التطبيق",
            onOk = {
                AppLockService.unlockedPkg = pkg
                finished = true
                finish()
            },
            onFail = { goHome() })
    }

    private fun goHome() {
        if (finished) return
        finished = true
        AppLockService.unlockedPkg = null
        startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }

    override fun onDestroy() {
        AppLockService.lockShowing = false
        super.onDestroy()
    }
}
