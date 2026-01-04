package com.jhotpotsystem.telephony

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.UssdResponseCallback
import androidx.annotation.RequiresApi

class UssdExecutor {

    interface Callback {
        fun onResponse(response: String)
        fun onFailure(error: String)
    }

    fun sendUssd(context: Context, subId: Int, code: String, callback: Callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendUssdOreo(context, subId, code, callback)
        } else {
            // Pre-Oreo fallback: use ACTION_CALL intent
            try {
                val ussdCode = Uri.encode(code)
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$ussdCode")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)

                // We cannot get USSD response on pre-O devices
                Handler(Looper.getMainLooper()).post {
                    callback.onResponse("USSD sent via call intent (no response available)")
                }
            } catch (e: Exception) {
                callback.onFailure(e.message ?: "Failed to dial USSD")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendUssdOreo(context: Context, subId: Int, code: String, callback: Callback) {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val tmSub = tm.createForSubscriptionId(subId)

            tmSub.sendUssdRequest(
                code,
                object : UssdResponseCallback() {
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager,
                        request: String,
                        response: CharSequence
                    ) {
                        Handler(Looper.getMainLooper()).post {
                            callback.onResponse(response.toString())
                        }
                    }

                    override fun onReceiveUssdResponseFailed(
                        telephonyManager: TelephonyManager,
                        request: String,
                        failureCode: Int
                    ) {
                        Handler(Looper.getMainLooper()).post {
                            callback.onFailure("USSD error code: $failureCode")
                        }
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } catch (e: Exception) {
            callback.onFailure(e.message ?: "Unknown USSD Error")
        }
    }
}
