package com.instalock.app

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object Auth {
    private const val AUTH = BiometricManager.Authenticators.BIOMETRIC_WEAK

    fun available(c: Context) =
        BiometricManager.from(c).canAuthenticate(AUTH) == BiometricManager.BIOMETRIC_SUCCESS

    fun prompt(a: FragmentActivity, title: String, onOk: () -> Unit, onFail: () -> Unit) {
        val cb = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = onOk()
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) = onFail()
        }
        val bp = BiometricPrompt(a, ContextCompat.getMainExecutor(a), cb)
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle("ضع إصبعك على المستشعر")
            .setAllowedAuthenticators(AUTH)
            .setNegativeButtonText("إلغاء")
            .setConfirmationRequired(false)
            .build()
        bp.authenticate(info)
    }
}
