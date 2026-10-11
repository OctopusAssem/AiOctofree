package com.octopusassem.aioctofree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopusassem.aioctofree.browser.Tabs
import com.octopusassem.aioctofree.data.Providers
import com.octopusassem.aioctofree.data.Store

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen(
    store: Store,
    onBump: () -> Unit,
    themeMode: String,
    onThemeMode: (String) -> Unit,
) {
    val ctx = LocalContext.current
    val drafts = remember {
        mutableStateMapOf<String, String>().apply {
            Providers.all.forEach { put(it.id, store.getKey(it.id)) }
        }
    }
    val show = remember { mutableStateMapOf<String, Boolean>() }
    var saved by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                ),
                title = {
                    Column {
                        Text("المفاتيح والمظهر", fontWeight = FontWeight.SemiBold)
                        Text(
                            "مفاتيحك على جهازك فقط · مشفّرة",
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
                "كل مزوّد بيدي مفتاح مجاني. اضغط «افتح الموقع»، سجّل، انسخ المفتاح، والصقه هنا.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
            if (saved != null) {
                Text(saved!!, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("مظهر التطبيق", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(
                        "اختار ألوانك أو اتبع إعداد الهاتف.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = themeMode == "system",
                            onClick = { onThemeMode("system") },
                            label = { Text("الجهاز", fontSize = 12.sp) },
                        )
                        FilterChip(
                            selected = themeMode == "dark",
                            onClick = { onThemeMode("dark") },
                            label = { Text("ليلي", fontSize = 12.sp) },
                        )
                        FilterChip(
                            selected = themeMode == "light",
                            onClick = { onThemeMode("light") },
                            label = { Text("فاتح", fontSize = 12.sp) },
                        )
                    }
                }
            }
            Providers.all.forEach { p ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(p.name, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                        Text(p.freeNote, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        val visible = show[p.id] == true
                        OutlinedTextField(
                            value = drafts[p.id].orEmpty(),
                            onValueChange = { drafts[p.id] = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("API Key") },
                            singleLine = true,
                            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { show[p.id] = !visible }) {
                                    Icon(
                                        if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = null,
                                    )
                                }
                            },
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    store.setKey(p.id, drafts[p.id].orEmpty())
                                    saved = "تم حفظ مفتاح ${p.name}"
                                    onBump()
                                },
                            ) { Text("حفظ") }
                            OutlinedButton(onClick = { Tabs.open(ctx, p.keyUrl) }) {
                                Icon(Icons.Outlined.OpenInBrowser, null)
                                Text("  فتح الموقع")
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "AiOctofree مفتوح المصدر · OctopusAssem\nالإصدار 0.1.2",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
