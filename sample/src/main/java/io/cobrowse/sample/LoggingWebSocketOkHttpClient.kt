package io.cobrowse.sample

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class LoggingWebSocketOkHttpClient : OkHttpClient() {
    override fun newWebSocket(request: Request, listener: WebSocketListener): WebSocket {
        return super.newWebSocket(request, LoggingWebSocketListener(listener))
    }
}

class LoggingWebSocketListener(
    private val delegate: WebSocketListener
) : WebSocketListener() {

    val TAG = "Cobrowse WebSocket";

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(TAG, "opened: ${response.request().url()}")
        delegate.onOpen(webSocket, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d(TAG, "message: $text")
        delegate.onMessage(webSocket, text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d(TAG, "message: $bytes")
        delegate.onMessage(webSocket, bytes);
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "closing: $code $reason")
        delegate.onClosing(webSocket, code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "closed: $code $reason")
        delegate.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d(TAG, "failure: ${t.message}")
        delegate.onFailure(webSocket, t, response)
    }
}
