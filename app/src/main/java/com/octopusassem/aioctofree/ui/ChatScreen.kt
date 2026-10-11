package com.octopusassem.aioctofree.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octopusassem.aioctofree.data.Msg
import com.octopusassem.aioctofree.data.Providers
import com.octopusassem.aioctofree.data.Store
import com.octopusassem.aioctofree.guide.QuestionRouter
import com.octopusassem.aioctofree.net.AiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    store: Store,
    chatId: String?,
    onChatId: (String?) -> Unit,
    onNeedKey: () -> Unit,
    bump: Int,
    onBump: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val client = remember { AiClient() }
    var id by remember(chatId, bump) { mutableStateOf(chatId) }
    var messages by remember { mutableStateOf<List<Msg>>(emptyList()) }
    var input by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var stream by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var hint by remember { mutableStateOf<String?>(null) }
    var menu by remember { mutableStateOf(false) }

    val chat = id?.let { store.chat(it) }
    val providerId = chat?.providerId ?: store.lastProviderId
    val provider = Providers.byId(providerId) ?: Providers.all.first()
    val model = chat?.model?.ifBlank { null }
        ?: store.lastModel.ifBlank { null }
        ?: provider.defaultModel

    LaunchedEffect(id, bump) {
        messages = if (id != null) store.messages(id!!) else emptyList()
        stream = ""
        error = null
    }

    val listState = rememberLazyListState()
    LaunchedEffect(messages.size, stream) {
        val n = messages.size + if (stream.isNotEmpty()) 1 else 0
        if (n > 0) listState.animateScrollToItem(n - 1)
    }

    fun ensureChat(cat: String = "general"): String {
        val existing = id
        if (existing != null) return existing
        val c = store.newChat(provider.id, model, cat)
        id = c.id
        onChatId(c.id)
        store.lastProviderId = c.providerId
        store.lastModel = c.model
        return c.id
    }

    fun send() {
        val text = input.trim()
        if (text.isEmpty() || busy) return
        error = null
        hint = null

        val route = QuestionRouter.route(text) { pid -> store.getKey(pid).isNotBlank() }
        val useProvider = Providers.byId(route.providerId) ?: provider
        val useModel = if (store.getKey(useProvider.id).isNotBlank()) route.model else model
        val key = store.getKey(useProvider.id)
        if (key.isBlank()) {
            error = "حط مفتاح ${useProvider.name} من تبويب المفاتيح"
            onNeedKey()
            return
        }

        val cid = ensureChat(route.category)
        store.updateChatRoute(cid, useProvider.id, useModel, route.category)
        store.lastProviderId = useProvider.id
        store.lastModel = useModel
        store.addMessage(cid, "user", text)
        input = ""
        messages = store.messages(cid)
        hint = "المرشد: ${route.categoryAr} → ${useProvider.name} / $useModel"
        busy = true
        stream = ""

        scope.launch {
            try {
                val history = store.messages(cid)
                val reply = withContext(Dispatchers.IO) {
                    client.chat(useProvider, key, useModel, history) { delta ->
                        stream += delta
                    }
                }
                store.addMessage(cid, "assistant", reply)
                messages = store.messages(cid)
                stream = ""
                onBump()
            } catch (e: Exception) {
                error = e.message ?: "فشل الطلب"
                stream = ""
            } finally {
                busy = false
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Image(
                            painter = painterResource(com.octopusassem.aioctofree.R.drawable.brand_icon),
                            contentDescription = "AiOctofree",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)),
                        )
                        Column {
                            Text(chat?.title ?: "AiOctofree", fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Text(
                                "${provider.name} · ${model.substringAfterLast('/')}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        id = null
                        onChatId(null)
                        messages = emptyList()
                        stream = ""
                        error = null
                        hint = null
                    }) {
                        Icon(Icons.Outlined.Add, contentDescription = "جديد")
                    }
                    Box {
                        TextButton(onClick = { menu = true }) {
                            Text(provider.name, fontSize = 12.sp)
                        }
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            Providers.all.forEach { p ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            buildString {
                                                append(p.name)
                                                if (store.getKey(p.id).isBlank()) append("  (مفيش مفتاح)")
                                            },
                                        )
                                    },
                                    onClick = {
                                        menu = false
                                        store.lastProviderId = p.id
                                        store.lastModel = p.defaultModel
                                        val cid = id
                                        if (cid != null) {
                                            store.updateChatRoute(cid, p.id, p.defaultModel, chat?.category ?: "general")
                                            onBump()
                                        }
                                    },
                                )
                            }
                        }
                    }
                },
            )
        },
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .imePadding(),
        ) {
            if (!store.hasAnyKey()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(14.dp),
                ) {
                    Column {
                        Text("مرحباً 👋", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "AiOctofree مجاني 100٪ — حط مفتاح API مجاني (Groq / Gemini / OpenRouter) من تبويب المفاتيح، وبعدين اسأل.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(onClick = onNeedKey) { Text("إضافة مفتاح") }
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer(Modifier.height(4.dp)) }
                items(messages, key = { it.id }) { m ->
                    Bubble(m.role, m.content)
                }
                if (stream.isNotEmpty()) {
                    item { Bubble("assistant", stream, streaming = true) }
                }
                if (busy && stream.isEmpty()) {
                    item {
                        Row(
                            Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Text("بيفكر…", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }

            if (hint != null) {
                AssistChip(
                    onClick = {},
                    label = { Text(hint!!, fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Outlined.AutoAwesome, null, Modifier.height(16.dp)) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
            if (error != null) {
                Text(
                    error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { if (it.length <= 8000) input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("اسأل أي AI مجاني…") },
                    minLines = 1,
                    maxLines = 5,
                    enabled = !busy,
                )
                FilledIconButton(onClick = { send() }, enabled = !busy && input.isNotBlank()) {
                    Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "إرسال")
                }
            }
        }
    }
}

@Composable
private fun Bubble(role: String, text: String, streaming: Boolean = false) {
    val mine = role == "user"
    val bg = if (mine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            Modifier
                .widthIn(max = 340.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text + if (streaming) " ▍" else "",
                color = fg,
                fontSize = 15.sp,
                lineHeight = 22.sp,
            )
        }
    }
}
