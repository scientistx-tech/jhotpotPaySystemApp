// SimInfoProvider.kt
package com.jhotpotsystem.telephony

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SubscriptionManager
import androidx.core.app.ActivityCompat
import org.json.JSONArray
import org.json.JSONObject

object SimInfoProvider {
    fun getSimInfo(context: Context): JSONArray {
        val arr = JSONArray()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            return arr

        val manager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        manager.activeSubscriptionInfoList?.forEach {
            val obj = JSONObject()
            obj.put("slot", it.simSlotIndex)
            obj.put("subId", it.subscriptionId)
            obj.put("carrier", it.carrierName?.toString() ?: "")
            arr.put(obj)
        }
        return arr
    }
}
