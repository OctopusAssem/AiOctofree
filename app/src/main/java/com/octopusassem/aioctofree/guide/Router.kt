package com.octopusassem.aioctofree.guide

import com.octopusassem.aioctofree.data.ProviderDef
import com.octopusassem.aioctofree.data.Providers

data class Route(
    val category: String,
    val categoryAr: String,
    val providerId: String,
    val model: String,
    val reasonAr: String,
)

object QuestionRouter {
    private val code = listOf(
        "code", "kotlin", "java", "python", "bug", "error", "sql", "function", "class",
        "كود", "برمجة", "خطأ", "دالة", "كوتلين", "بايثون", "جافا", "سكربت", "fix",
    )
    private val translate = listOf(
        "translate", "translation", "arabic", "english", "rewrite",
        "ترجمة", "ترجم", "صيغة", "صياغة", "عربي", "إنجليزي", "انجليزي", "فصحى", "عامية",
    )
    private val summarize = listOf(
        "summarize", "summary", "tl;dr", "long text", "article",
        "لخّص", "لخص", "تلخيص", "ملخص", "مقال", "طويل",
    )
    private val creative = listOf(
        "story", "poem", "idea", "brainstorm", "name", "creative", "joke",
        "قصة", "شعر", "أفكار", "اسم", "إبداع", "نكتة", "سيناريو",
    )
    private val math = listOf(
        "math", "calculate", "equation", "prove", "logic", "solve",
        "رياضيات", "احسب", "معادلة", "حل", "منطق", "مسألة",
    )

    fun classify(text: String): String {
        val t = text.lowercase()
        fun hit(words: List<String>) = words.any { t.contains(it) }
        return when {
            hit(code) -> "code"
            hit(math) -> "math"
            hit(translate) -> "translate"
            hit(summarize) -> "summarize"
            hit(creative) -> "creative"
            else -> "general"
        }
    }

    fun route(text: String, available: (String) -> Boolean = { true }): Route {
        val cat = classify(text)
        val pick = when (cat) {
            "code" -> prefer(listOf("groq", "gemini", "openrouter"), available)
            "math" -> prefer(listOf("gemini", "groq", "openrouter"), available)
            "translate" -> prefer(listOf("gemini", "openrouter", "groq"), available)
            "summarize" -> prefer(listOf("gemini", "openrouter", "groq"), available)
            "creative" -> prefer(listOf("openrouter", "gemini", "groq"), available)
            else -> prefer(listOf("groq", "gemini", "openrouter"), available)
        }
        val p = Providers.byId(pick) ?: Providers.all.first()
        return Route(
            category = cat,
            categoryAr = ar(cat),
            providerId = p.id,
            model = p.defaultModel,
            reasonAr = reason(cat, p),
        )
    }

    private fun prefer(order: List<String>, available: (String) -> Boolean): String {
        order.forEach { if (available(it)) return it }
        return order.first()
    }

    private fun ar(cat: String) = when (cat) {
        "code" -> "كود / برمجة"
        "math" -> "رياضيات / منطق"
        "translate" -> "ترجمة / صياغة"
        "summarize" -> "تلخيص"
        "creative" -> "إبداع"
        else -> "عام"
    }

    private fun reason(cat: String, p: ProviderDef) = when (cat) {
        "code" -> "Groq سريع للكود — ${p.name}"
        "math" -> "Gemini مناسب للخطوات المنطقية — ${p.name}"
        "translate" -> "Gemini قوي في العربي — ${p.name}"
        "summarize" -> "سياق أطول يناسب التلخيص — ${p.name}"
        "creative" -> "تنوّع موديلات OpenRouter — ${p.name}"
        else -> "أسرع رد مجاني — ${p.name}"
    }
}
