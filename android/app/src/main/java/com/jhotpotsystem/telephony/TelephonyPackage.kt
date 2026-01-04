// TelephonyPackage.kt
package com.jhotpotsystem.telephony
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class TelephonyPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext) =
        listOf(TelephonyModule(reactContext))
    override fun createViewManagers(reactContext: ReactApplicationContext) = emptyList<ViewManager<*, *>>()
}
