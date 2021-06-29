package sku.app.lib_tracker.utils

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio

fun MockWebServer.enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
    val inputStream = javaClass.classLoader!!.getResourceAsStream("api-response/$fileName")
    val source = Okio.buffer(Okio.source(inputStream))
    val mockResponse = MockResponse()
    for ((key, value) in headers) {
        mockResponse.addHeader(key, value)
    }
    this.enqueue(mockResponse.setBody(source.readString(Charsets.UTF_8)))
}