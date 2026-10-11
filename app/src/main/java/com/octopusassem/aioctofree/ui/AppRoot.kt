package com.octopusassem.aioctofree.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.octopusassem.aioctofree.data.Store

enum class Tab { Chat, Archive, Providers, Guide }

@Composable
fun AppRoot() {
    val ctx = LocalContext.current
    val store = remember { Store(ctx) }
    var tab by remember { mutableStateOf(Tab.Chat) }
    var openChatId by remember { mutableStateOf<String?>(null) }
    var bump by remember { mutableIntStateOf(0) }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(com.octopusassem.aioctofree.R.drawable.app_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.78f),
            ),
        )
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)) {
                    val items = listOf(
                        Triple(Tab.Chat, Icons.Outlined.Chat, "شات"),
                        Triple(Tab.Archive, Icons.Outlined.History, "أرشيف"),
                        Triple(Tab.Providers, Icons.Outlined.Key, "مفاتيح"),
                        Triple(Tab.Guide, Icons.Outlined.Explore, "مرشد"),
                    )
                    for ((d, icon, label) in items) {
                        NavigationBarItem(
                            selected = tab == d,
                            onClick = { tab = d },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 11.sp) },
                        )
                    }
                }
            },
        ) { pad ->
            Box(Modifier.padding(pad)) {
                when (tab) {
                    Tab.Chat -> ChatScreen(
                        store = store,
                        chatId = openChatId,
                        onChatId = { openChatId = it },
                        onNeedKey = { tab = Tab.Providers },
                        bump = bump,
                        onBump = { bump++ },
                    )
                    Tab.Archive -> ArchiveScreen(
                        store = store,
                        bump = bump,
                        onOpen = {
                            openChatId = it
                            tab = Tab.Chat
                        },
                        onBump = { bump++ },
                    )
                    Tab.Providers -> ProvidersScreen(store = store, onBump = { bump++ })
                    Tab.Guide -> GuideScreen(store = store)
                }
            }
        }
    }
}
