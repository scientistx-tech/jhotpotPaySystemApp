// SocketForegroundService.kt
package com.jhotpotsystem.service

import android.app.*
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jhotpotsystem.telephony.SimInfoProvider
import com.jhotpotsystem.telephony.SmsSender
import com.jhotpotsystem.telephony.UssdExecutor
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketForegroundService : Service() {
    private var socket: Socket? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serverUrl = intent?.getStringExtra("url") ?: return START_STICKY
        initSocket(serverUrl)
        return START_STICKY
    }

    private fun startForegroundNotification() {
        val channelId = "SOCKET_CHANNEL"
        val channel =
                NotificationChannel(channelId, "Socket Service", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification =
                NotificationCompat.Builder(this, channelId)
                        .setContentTitle("Socket Service Active")
                        .setSmallIcon(android.R.drawable.stat_notify_sync)
                        .build()
        startForeground(1, notification)
    }

    private fun initSocket(url: String) {
        // Prevent multiple connections
        if (socket?.connected() == true) return

        socket = IO.socket(url)

        socket?.on(Socket.EVENT_CONNECT) {
            val simInfo = SimInfoProvider.getSimInfo(this)
            socket?.emit("register_device", simInfo.toString())
        }

        socket?.on("sms") { args ->
            val data = JSONObject(args[0].toString())
            val number = data.getString("number")
            val text = data.getString("text")
            val sim = data.getInt("sim")

            val subId = SimInfoProvider.getSimInfo(this).optJSONObject(sim)?.optInt("subId") ?: 0

            val sent = SmsSender.sendSms(this, subId, number, text)
            socket?.emit(if (sent) "sms_success" else "sms_failed", number)
        }

        socket?.on("ussd") { args ->
            val data = JSONObject(args[0].toString())
            val code = data.getString("code")
            val sim = data.getInt("sim")
            val id = data.getString("id")

            val subId = SimInfoProvider.getSimInfo(this).optJSONObject(sim)?.optInt("subId") ?: 0

            UssdExecutor()
                    .sendUssd(
                            this,
                            subId,
                            code,
                            object : UssdExecutor.Callback {
                                override fun onResponse(response: String) {
                                    socket?.emit("ussd_success", id)
                                }

                                override fun onFailure(error: String) {
                                    socket?.emit("ussd_failed", id)
                                }
                            }
                    )
        }

        socket?.connect()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
