package com.octopusassem.aioctofree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopusassem.aioctofree.data.Providers
import com.octopusassem.aioctofree.data.Store
import com.octopusassem.aioctofree.guide.QuestionRouter
import com.octopusassem.aioctofree.guide.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(store: Store) {
    var q by remember { mutableStateOf("") }
    var route by remember { mutableStateOf<Route?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("المرشد", fontWeight = FontWeight.SemiBold)
                        Text(
                            "أي AI أنسب لسؤالك؟",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "اكتب سؤالك — المرشد يصنّفه ويقترح أفضل مزوّد مجاني متاح عندك.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = q,
                onValueChange = { q = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                placeholder = { Text("مثال: صلّح لي كود Kotlin…") },
            )
            Button(
                onClick = {
                    route = QuestionRouter.route(q) { store.getKey(it).isNotBlank() }
                },
                enabled = q.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("اقترح") }

            route?.let { r ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("التصنيف: ${r.categoryAr}", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("المزوّد: ${Providers.byId(r.providerId)?.name ?: r.providerId}")
                        Text("الموديل: ${r.model}", fontSize = 13.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(r.reasonAr, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        val has = store.getKey(r.providerId).isNotBlank()
                        if (!has) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "مفيش مفتاح لهذا المزوّد — روح تبويب المفاتيح وأضفه.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("دليل سريع", fontWeight = FontWeight.SemiBold)
            GuideRow("كود / برمجة", "Groq (سرعة)")
            GuideRow("عربي / ترجمة", "Gemini")
            GuideRow("تلخيص طويل", "Gemini")
            GuideRow("إبداع / أفكار", "OpenRouter :free")
            GuideRow("عام سريع", "Groq")
        }
    }
}

@Composable
private fun GuideRow(cat: String, pick: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(cat, fontWeight = FontWeight.Medium)
            Text(pick, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
