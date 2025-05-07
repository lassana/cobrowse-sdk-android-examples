package io.cobrowse.sample

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class LoggingInterceptor : Interceptor {
    companion object {
        private val allowedHosts = setOf(".cobrowse.io", ".cbrws.io")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val host = request.url().host()
        if (allowedHosts.any { host.endsWith(it) }) {
            return chain.proceed(request)
        } else {
            throw IOException("This request is not allowed: " + request.url())
        }
    }
}
