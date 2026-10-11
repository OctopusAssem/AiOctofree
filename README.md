# AiOctofree

<p align="center">
  <img src="app/src/main/res/drawable-nodpi/brand_logo.webp" width="148" alt="شعار AiOctofree">
</p>

<h2 align="center">محطة الذكاء الاصطناعي المجانية</h2>

<p align="center">شات واحد لعدة مزوّدين، أرشيف محلي لمحادثاتك، ومرشد يساعدك تختار المزوّد المناسب لسؤالك.</p>

<p align="center">
  <a href="https://github.com/OctopusAssem/AiOctofree/releases/latest/download/app-release.apk">تحميل أحدث APK موقّع</a>
  · <a href="https://github.com/OctopusAssem/AiOctofree/releases">كل الإصدارات</a>
  · <a href="CHANGELOG.md">سجل التغييرات</a>
</p>

AiOctofree تطبيق Android مفتوح المصدر، معمول عشان يجمع واجهات الذكاء الاصطناعي ذات الخطط المجانية في تجربة عربية بسيطة. تختار المزوّد، تضيف مفتاحك الشخصي، وتبدأ المحادثة من غير حساب داخل التطبيق أو خادم وسيط تابع للمشروع.

## المميزات

- **شات متعدد المزوّدين:** Groq وGoogle Gemini وOpenRouter، مع اختيار الموديل المتاح لحسابك.
- **مرشد للأسئلة:** اكتب سؤالك لتحصل على تصنيف واقتراح لمزوّد وموديل مناسبين؛ الاقتراح لا يرسل السؤال تلقائيًا.
- **أرشيف على جهازك:** المحادثات محفوظة في SQLite، ويمكن البحث فيها أو فتحها أو حذفها.
- **مفاتيحك تفضل معاك:** مفاتيح API تُحفظ مشفّرة محليًا على الهاتف، ولا توجد مفاتيح مشتركة مدمجة في التطبيق.
- **واجهة عربية:** شاشة ترحيب وإعداد واضحان، مع فتح مواقع التسجيل داخل المتصفح الآمن المدمج في Android.
- **مظهر على ذوقك:** اتّباع إعداد الجهاز أو اختيار الوضع الليلي أو الفاتح، مع ألوان وهوية OctopusAssem.
- **اللوجو الأصلي:** اللوجو الكامل مستخدم في أيقونات Android التكيفية والتقليدية.

## الخصوصية واستخدام المفاتيح

التطبيق يعمل بنظام **استخدم مفتاحك الشخصي (BYOK)**. لا يحتوي APK على مفتاح API جاهز أو خادم وسيط للمشروع. عند إرسال رسالة، يرسل التطبيق طلبًا مباشرةً إلى المزوّد الذي اخترته مستخدمًا مفتاحك؛ لذلك تنطبق سياسة ذلك المزوّد على الرسالة. سجل المحادثات والمفاتيح المحفوظة محليان على الهاتف، والمفاتيح مشفّرة باستخدام AndroidX Security Crypto.

لا تضع في محادثة أي بيانات لا تريد مشاركتها مع مزوّد الذكاء الاصطناعي. حدود الاستخدام والخطط المجانية وتوفّر الموديلات يحددها كل مزوّد وقد تتغير في أي وقت؛ كلمة «مجاني» لا تعني توفرًا أو حصة مضمونة.

## المزوّدون

| المزوّد | صفحة المفتاح | ملاحظة |
| --- | --- | --- |
| [Groq](https://console.groq.com/keys) | إنشاء أو إدارة مفتاح | الحدود والموديلات تتغير حسب حساب المزوّد. |
| [Google Gemini](https://aistudio.google.com/apikey) | Google AI Studio | يتطلب مفتاح Gemini خاصًا بك. |
| [OpenRouter](https://openrouter.ai/keys) | إنشاء أو إدارة مفتاح | اختر موديلًا متاحًا، مثل الموديلات ذات اللاحقة `:free` عندما تكون متوفرة. |

## التثبيت والبدء

1. نزّل **أحدث APK موقّع** من [صفحة الإصدارات](https://github.com/OctopusAssem/AiOctofree/releases/latest).
2. افتح الملف واسمح بالتثبيت من المصدر الذي نزّلت منه إذا طلب Android ذلك.
3. افتح تبويب **المفاتيح والمظهر**، واختر المزوّد واضغط **فتح الموقع** لإنشاء مفتاحك.
4. الصق المفتاح في التطبيق واضغط **حفظ**.
5. ابدأ من **الشات**، أو استخدم **المرشد** لاقتراح مزوّد وموديل.

يتطلب التطبيق **Android 8.0 (API 26) أو أحدث**. لتثبيت تحديث فوق إصدار موجود، استخدم APK الرسمي الموقّع من هذا المشروع؛ التوقيع نفسه محفوظ لإصدارات التطبيق.

## بناء المشروع

المشروع يستخدم Kotlin وJetpack Compose وMaterial 3 وOkHttp وSQLite وAndroidX Security Crypto وAndroid Custom Tabs. يتطلب بناء Android JDK 21 وGradle 9.7.1. أمر البناء المستخدم في CI:

```bash
gradle :app:assembleRelease --no-daemon
```

ملف التوقيع وكلمات مروره غير موجودة في المستودع. إصدار APK موقّع يُبنى تلقائيًا عبر GitHub Actions باستخدام أسرار المستودع، ثم يتم التحقق من التوقيع قبل نشره. لا ترفع ملف التوقيع أو كلمات مروره إلى GitHub.

## سجل الإصدارات

تفاصيل الإصدارات الحالية والسابقة موجودة في [CHANGELOG.md](CHANGELOG.md). نحتفظ بآخر إصدار موقّع فقط في صفحة Releases لتجنب تثبيت نسخة قديمة بالخطأ؛ سجل التغييرات يوثّق ما أضيف في الإصدارات السابقة.

## الترخيص

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) · [OctopusAssem](https://github.com/OctopusAssem) — عاصم حسين.

---

## English

**AiOctofree** is an open-source Android AI app with multi-provider chat, an on-device conversation archive, and a guide that recommends a provider and model for a question.

- Providers: Groq, Google Gemini, and OpenRouter.
- Bring your own API keys; keys are encrypted on-device and no project API key or proxy server is bundled.
- Chat history is stored locally in SQLite and can be searched or deleted.
- Arabic-first interface, saved system/light/dark appearance, welcome flow, and the original OctopusAssem launcher logo.
- Android 8.0 (API 26) or newer.

Download the [latest signed APK](https://github.com/OctopusAssem/AiOctofree/releases/latest/download/app-release.apk). When sending a prompt, the app sends it directly to the selected AI provider, so requests are subject to that provider's published privacy policy and usage limits. Free tiers and model availability can change.

Built with Kotlin, Jetpack Compose, Material 3, OkHttp, SQLite, AndroidX Security Crypto, and Custom Tabs. Signed releases are built and signature-verified by GitHub Actions; signing credentials are not stored in this repository.

See [CHANGELOG.md](CHANGELOG.md) for release history. Licensed under Apache-2.0.