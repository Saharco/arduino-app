package com.technion.columbus.map

import android.util.Log
import com.technion.columbus.main.MainActivity
import com.technion.columbus.utility.RPI_RECV_PORT
import com.technion.columbus.utility.RPI_SEND_PORT
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class SocketsHandler(private val address: String, private val sendPort: Int, private val recvPort: Int) {
    private val sendSocket = Socket()
    private val recvSocket = Socket()

    private fun openSocket(address: String, port: Int) {
        Log.d(MainActivity.TAG, "Started opening socket on port $port")
        try {
            when (port) {
                RPI_SEND_PORT -> sendSocket.connect(InetSocketAddress(address, port), 10000)
                RPI_RECV_PORT -> recvSocket.connect(InetSocketAddress(address, port), 10000)
            }
            Log.d(MainActivity.TAG, "Opened port $port successfully")
        } catch (e: IOException) {
            Log.d(MainActivity.TAG, "Caught IOException while opening socket on port $port")
        } catch (e: Exception) {
            Log.d(MainActivity.TAG, "Caught an unknown exception while opening socket on port $port")
        }
    }

    fun getSendSocket(): Socket {
        if (sendSocket.isConnected)
            return sendSocket
        openSocket(address, sendPort)
        if (sendSocket.isConnected)
            return sendSocket
        return Socket()
    }

    fun getRecvSocket(): Socket {
        if (recvSocket.isConnected)
            return recvSocket
        openSocket(address, recvPort)
        if (recvSocket.isConnected)
            return recvSocket
        return Socket()
    }
}


class SocketsHolder {
    companion object {
        var sendSocket = Socket()
        var recvSocket = Socket()
    }
}
