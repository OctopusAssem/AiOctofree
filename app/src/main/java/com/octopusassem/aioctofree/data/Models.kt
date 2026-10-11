package com.octopusassem.aioctofree.data

data class ProviderDef(
    val id: String,
    val name: String,
    val baseUrl: String,
    val signupUrl: String,
    val keyUrl: String,
    val defaultModel: String,
    val models: List<String>,
    val freeNote: String,
    val style: ApiStyle = ApiStyle.OpenAI,
)

enum class ApiStyle { OpenAI, Gemini }

data class Chat(
    val id: String,
    val title: String,
    val providerId: String,
    val model: String,
    val category: String,
    val updatedAt: Long,
)

data class Msg(
    val id: String,
    val chatId: String,
    val role: String,
    val content: String,
    val ts: Long,
)

object Providers {
    val all: List<ProviderDef> = listOf(
        ProviderDef(
            id = "groq",
            name = "Groq",
            baseUrl = "https://api.groq.com/openai/v1",
            signupUrl = "https://console.groq.com/keys",
            keyUrl = "https://console.groq.com/keys",
            defaultModel = "openai/gpt-oss-20b",
            models = listOf(
                "openai/gpt-oss-20b",
                "openai/gpt-oss-120b",
                "qwen/qwen3.8-27b",
            ),
            freeNote = "~30 req/min · 1000/day · no card",
        ),
        ProviderDef(
            id = "openrouter",
            name = "OpenRouter",
            baseUrl = "https://openrouter.ai/api/v1",
            signupUrl = "https://openrouter.ai/keys",
            keyUrl = "https://openrouter.ai/keys",
            defaultModel = "openrouter/free",
            models = listOf(
                "openrouter/free",
                "google/gemma-3-27b-it:free",
                "nvidia/nemotron-3-super-120b-a12b:free",
            ),
            freeNote = "~20/min · 50/day free (:free models)",
        ),
        ProviderDef(
            id = "gemini",
            name = "Google Gemini",
            baseUrl = "https://generativelanguage.googleapis.com/v1beta",
            signupUrl = "https://aistudio.google.com/apikey",
            keyUrl = "https://aistudio.google.com/apikey",
            defaultModel = "gemini-2.5-flash",
            models = listOf(
                "gemini-2.5-flash",
                "gemini-2.0-flash",
                "gemini-2.5-flash-lite",
            ),
            freeNote = "AI Studio free tier · no card to start",
            style = ApiStyle.Gemini,
        ),
    )

    fun byId(id: String): ProviderDef? = all.find { it.id == id }
}
