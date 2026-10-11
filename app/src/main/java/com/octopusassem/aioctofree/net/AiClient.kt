package com.octopusassem.aioctofree.net

import com.octopusassem.aioctofree.data.ApiStyle
import com.octopusassem.aioctofree.data.Msg
import com.octopusassem.aioctofree.data.ProviderDef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiClient {
    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = "application/json; charset=utf-8".toMediaType()

    suspend fun chat(
        provider: ProviderDef,
        apiKey: String,
        model: String,
        history: List<Msg>,
        onDelta: (String) -> Unit,
    ): String = withContext(Dispatchers.IO) {
        require(apiKey.isNotBlank()) { "مفيش مفتاح API لهذا المزوّد" }
        when (provider.style) {
            ApiStyle.OpenAI -> openAiStream(provider, apiKey, model, history, onDelta)
            ApiStyle.Gemini -> gemini(provider, apiKey, model, history, onDelta)
        }
    }

    private fun openAiStream(
        provider: ProviderDef,
        apiKey: String,
        model: String,
        history: List<Msg>,
        onDelta: (String) -> Unit,
    ): String {
        val messages = JSONArray()
        messages.put(
            JSONObject()
                .put("role", "system")
                .put(
                    "content",
                    "You are a helpful assistant inside AiOctofree, a free multi-AI Android app. " +
                        "Reply in the user's language. Be clear and concise.",
                ),
        )
        history.forEach { m ->
            if (m.role == "user" || m.role == "assistant") {
                messages.put(JSONObject().put("role", m.role).put("content", m.content))
            }
        }
        val body = JSONObject()
            .put("model", model)
            .put("messages", messages)
            .put("stream", true)
            .put("temperature", 0.7)

        val req = Request.Builder()
            .url("${provider.baseUrl.trimEnd('/')}/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .header("HTTP-Referer", "https://github.com/OctopusAssem/AiOctofree")
            .header("X-Title", "AiOctofree")
            .post(body.toString().toRequestBody(json))
            .build()

        http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) {
                val err = resp.body?.string().orEmpty().take(500)
                throw IllegalStateException("HTTP ${resp.code}: $err")
            }
            val src = resp.body?.source() ?: throw IllegalStateException("empty body")
            val full = StringBuilder()
            while (!src.exhausted()) {
                val line = src.readUtf8Line() ?: break
                if (!line.startsWith("data:")) continue
                val data = line.removePrefix("data:").trim()
                if (data == "[DONE]") break
                try {
                    val obj = JSONObject(data)
                    val delta = obj.optJSONArray("choices")
                        ?.optJSONObject(0)
                        ?.optJSONObject("delta")
                        ?.optString("content")
                        .orEmpty()
                    if (delta.isNotEmpty()) {
                        full.append(delta)
                        onDelta(delta)
                    }
                } catch (_: Exception) {
                }
            }
            if (full.isEmpty()) throw IllegalStateException("رد فاضي من المزوّد")
            return full.toString()
        }
    }

    private fun gemini(
        provider: ProviderDef,
        apiKey: String,
        model: String,
        history: List<Msg>,
        onDelta: (String) -> Unit,
    ): String {
        val contents = JSONArray()
        history.forEach { m ->
            val role = if (m.role == "assistant") "model" else "user"
            if (m.role == "user" || m.role == "assistant") {
                contents.put(
                    JSONObject()
                        .put("role", role)
                        .put(
                            "parts",
                            JSONArray().put(JSONObject().put("text", m.content)),
                        ),
                )
            }
        }
        val body = JSONObject()
            .put("contents", contents)
            .put(
                "generationConfig",
                JSONObject().put("temperature", 0.7),
            )

        val url =
            "${provider.baseUrl.trimEnd('/')}/models/$model:generateContent?key=${apiKey.trim()}"
        val req = Request.Builder()
            .url(url)
            .header("Content-Type", "application/json")
            .post(body.toString().toRequestBody(json))
            .build()

        http.newCall(req).execute().use { resp ->
            val text = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) {
                throw IllegalStateException("HTTP ${resp.code}: ${text.take(500)}")
            }
            val obj = JSONObject(text)
            val out = obj.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")
                .orEmpty()
            if (out.isEmpty()) throw IllegalStateException("رد فاضي من Gemini")
            onDelta(out)
            return out
        }
    }
}
