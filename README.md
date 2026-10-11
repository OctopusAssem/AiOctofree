# AiOctofree

**محطة الذكاء الاصطناعي المجانية** — تطبيق أندرويد مفتوح المصدر يجمع مزوّدي AI المجانيين في مكان واحد.

[English](#english) · [العربية](#العربية)

---

## العربية

### ليه AiOctofree؟
- **مجاني أولاً**: Groq · Google Gemini · OpenRouter (`:free`)
- **أرشيف محلي**: كل المحادثات على موبايلك (SQLite) — خصوصية
- **مرشد ذكي**: يصنّف سؤالك ويقترح أنسب موديل مجاني
- **مفاتيحك عندك**: مشفّرة على الجهاز (EncryptedSharedPreferences) — مفيش سيرفر وسطاء
- **فتح الموقع**: لو محتاج مفتاح، زر يفتح صفحة الاشتراك (Custom Tabs)

### التثبيت
1. نزّل أحدث APK من [Releases](https://github.com/OctopusAssem/AiOctofree/releases)
2. ثبّته (مصدر غير معروف)
3. من تبويب **مفاتيح** → افتح موقع المزود → انسخ المفتاح → احفظ
4. ارجع للشات واسأل

### مزوّدون (حدود تقريبية — تتغيّر)
| مزوّد | ملاحظة مجانية |
|--------|----------------|
| [Groq](https://console.groq.com/keys) | ~30/د · 1000/يوم · بدون بطاقة |
| [Gemini](https://aistudio.google.com/apikey) | AI Studio free |
| [OpenRouter](https://openrouter.ai/keys) | موديلات `:free` · ~50/يوم |

### البناء من المصدر
```bash
gradle :app:assembleRelease
# APK → app/build/outputs/apk/release/
```

### الستاك
Kotlin · Jetpack Compose · Material 3 · OkHttp (SSE) · SQLite · Security Crypto · Custom Tabs

### الترخيص
Apache-2.0 · by [OctopusAssem](https://github.com/OctopusAssem) (عاصم حسين)

---

## English

**AiOctofree** is a free-first Android AI station: multi-provider chat, on-device archive, and a question→model guide.

### Features
- Free-tier providers: Groq, Gemini, OpenRouter `:free`
- Local SQLite history + encrypted API keys (BYOK only)
- Keyword/category router suggests the best free model
- Custom Tabs to open provider signup pages

### Install
Download the latest APK from [Releases](https://github.com/OctopusAssem/AiOctofree/releases).

### Build
```bash
gradle :app:assembleRelease
```

### License
Apache-2.0 · [OctopusAssem](https://github.com/OctopusAssem)
