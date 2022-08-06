package com.realityexpander.ktorpushnotifications.data.remote

interface ApiService {

    suspend fun sendNotification(title: String, description: String)

    companion object {
        // Make sure to set to your local machine IP address
        const val SEND_NOTIFICATION = "http://192.168.0.186:8083/sendNotification"
    }
}