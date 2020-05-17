package com.example.simplepasswordkeeper

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

class BiometricUnlock {
    companion object {
        fun setBiometricUnlock(activity : FragmentActivity, successCallback : ()->Unit) {
            if (BiometricManager.from(activity).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                activity.applicationContext.let { applicationContext ->
                    BiometricPrompt(activity, activity.mainExecutor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence
                            ) {
                                super.onAuthenticationError(errorCode, errString)
                                Toast.makeText(applicationContext,
                                    "Authentication error: $errString", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onAuthenticationSucceeded(
                                result: BiometricPrompt.AuthenticationResult
                            ) {
                                super.onAuthenticationSucceeded(result)
                                successCallback()
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                                Toast.makeText(applicationContext,
                                    "Authentication failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    ).authenticate(
                        BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric login to view field")
                            .setSubtitle("Log in using your biometric credential")
                            .setNegativeButtonText("Cancel")
                            .build()
                    )
                }
            } else
                successCallback()
        }
    }
}