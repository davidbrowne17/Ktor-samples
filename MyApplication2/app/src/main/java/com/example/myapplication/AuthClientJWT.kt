package com.example.myapplication

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI

class AuthClientJWT(private val baseUrl: String) {
    private var jwtToken: String? = null

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    if (jwtToken == null) throw IllegalArgumentException("Token is not available")
                    BearerTokens(jwtToken!!, "")
                }
            }
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response: HttpResponse = client.post("$baseUrl/login") {
                contentType(ContentType.Application.Json)
                body = LoginForm(username, password)
            }
            jwtToken = response.body<LoginResponse>().token  // Parse and store the JWT token
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchProtectedData(): String {
        return client.get("$baseUrl/protected").body()
    }
}
