package com.example.myapplication

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.request.*

class AuthClientBasic(private val baseUrl: String) {
    private val client = HttpClient(Android) {
        install(Auth) {
            basic {
                credentials { BasicAuthCredentials("","") }
            }
        }
    }

    suspend fun fetchProtectedData(): String {
        return client.get("$baseUrl/protected").body()
    }
}