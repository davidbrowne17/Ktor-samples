package com.example.myapplication

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.serialization.Serializable


class AuthClientToken(private val baseUrl: String) {
    private var token: String? = null

    // Client with Authentication feature
    private val client = HttpClient(Android) {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(token ?: "", "")
                }
            }
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = client.post("$baseUrl/login") {
                contentType(ContentType.Application.Json)
                body = LoginForm(username, password)
            }
            token = response.body()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchProtectedData(): String {
        return client.get("$baseUrl/protected").toString()
    }
}

data class LoginForm(val username: String, val password: String)

@Serializable
data class LoginResponse(val token: String)