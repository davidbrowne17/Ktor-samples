package com.example.myapplication

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.util.InternalAPI
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(val accessToken: String, val refreshToken: String)

class AuthClient(private val baseUrl: String) {
    private var currentTokens: AuthTokens? = null

    private val client = HttpClient(Android) {
        install(Auth) {
            bearer {
                loadTokens {
                    currentTokens?.let {
                        BearerTokens(it.accessToken, it.refreshToken)
                    } ?: run {
                        val newTokens = requestNewTokens() // Initial token request
                        currentTokens = newTokens
                        BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                    }
                }

                refreshTokens {
                    currentTokens?.refreshToken?.let {
                        val refreshedTokens = refreshAccessToken(it)
                        currentTokens = refreshedTokens
                        BearerTokens(refreshedTokens.accessToken, refreshedTokens.refreshToken)
                    }
                }
            }
        }
    }

    @OptIn(InternalAPI::class)
    private suspend fun requestNewTokens(): AuthTokens {
        // Simulate a request to a login endpoint
        return client.post("$baseUrl/api/login") {
            contentType(ContentType.Application.Json)
            body = LoginForm("username", "password")
        }.body()
    }

    @OptIn(InternalAPI::class)
    private suspend fun refreshAccessToken(refreshToken: String?): AuthTokens {
        // Simulate a request to a token refresh endpoint
        return client.post("$baseUrl/api/refresh") {
            contentType(ContentType.Application.Json)
            body = mapOf("refreshToken" to refreshToken)
        }.body()
    }

    suspend fun fetchProtectedData(): String {
        return client.get("$baseUrl/protected-resource").bodyAsText()
    }
}

