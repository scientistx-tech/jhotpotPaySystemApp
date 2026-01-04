// TelephonyModule.kt
package com.jhotpotsystem.telephony

import android.content.Intent
import android.os.Build
import com.facebook.react.bridge.*
import com.jhotpotsystem.service.SocketForegroundService

class TelephonyModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName() = "TelephonyModule"

    @ReactMethod
    fun startSocketService(url: String) {
        val intent = Intent(reactContext, SocketForegroundService::class.java)
        intent.putExtra("url", url)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            reactContext.startForegroundService(intent)
        else reactContext.startService(intent)
    }

    @ReactMethod
    fun stopSocketService() {
        val intent = Intent(reactContext, SocketForegroundService::class.java)
        reactContext.stopService(intent)
    }
}
