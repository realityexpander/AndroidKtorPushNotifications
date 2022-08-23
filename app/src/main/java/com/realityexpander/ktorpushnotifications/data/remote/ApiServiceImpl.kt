package com.realityexpander.ktorpushnotifications.data.remote

import io.ktor.client.*
import io.ktor.client.request.*

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {

    override suspend fun sendNotification(title: String, description: String) {
        try {
            // GET request to our server, which will make the actual call to the OneSignal API
            client.get<String> {
                url(ApiService.SEND_NOTIFICATION)
                parameter("title", title)
                parameter("description", description)
                parameter("button1id", "1")
                parameter("button1text", "Yes")
                parameter("button1Icon", "ic_notification")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}