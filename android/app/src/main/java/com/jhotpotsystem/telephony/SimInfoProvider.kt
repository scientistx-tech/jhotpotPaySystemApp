// SimInfoProvider.kt
package com.jhotpotsystem.telephony

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import org.json.JSONArray
import org.json.JSONObject

object SimInfoProvider {

    fun getSimInfo(context: Context): JSONArray {
        val arr = JSONArray()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) !=
                        PackageManager.PERMISSION_GRANTED
        )
                return arr

        val subManager =
                context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as
                        SubscriptionManager

        val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        subManager.activeSubscriptionInfoList?.forEach { info ->
            val obj = JSONObject()
            obj.put("slot", info.simSlotIndex)
            obj.put("subId", info.subscriptionId)

            try {
                val tmForSub = telephonyManager.createForSubscriptionId(info.subscriptionId)

                obj.put("carrier", tmForSub.simOperatorName ?: info.carrierName?.toString() ?: "")
            } catch (e: Exception) {
                obj.put("carrier", info.carrierName?.toString() ?: "")
            }

            arr.put(obj)
        }

        return arr
    }
}
