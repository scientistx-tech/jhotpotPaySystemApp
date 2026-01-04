// SmsSender.kt
package com.jhotpotsystem.telephony

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat

object SmsSender {
    fun sendSms(context: Context, subId: Int, number: String, text: String): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
            return false
        val smsManager = SmsManager.getSmsManagerForSubscriptionId(subId)
        smsManager.sendTextMessage(number, null, text, null, null)
        return true
    }
}
