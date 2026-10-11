package com.octopusassem.aioctofree.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.octopusassem.aioctofree.data.Store

enum class Tab { Chat, Archive, Providers, Guide }

@Composable
fun AppRoot() {
    val ctx = LocalContext.current
    val store = remember { Store(ctx) }
    var tab by remember { mutableStateOf(Tab.Chat) }
    var openChatId by remember { mutableStateOf<String?>(null) }
    var bump by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Nav(Tab.Chat, tab, "شات", Icons.Outlined.Chat) { tab = Tab.Chat }
                Nav(Tab.Archive, tab, "أرشيف", Icons.Outlined.History) { tab = Tab.Archive }
                Nav(Tab.Providers, tab, "مفاتيح", Icons.Outlined.Key) { tab = Tab.Providers }
                Nav(Tab.Guide, tab, "مرشد", Icons.Outlined.Explore) { tab = Tab.Guide }
            }
        },
    ) { pad ->
        androidx.compose.foundation.layout.Box(Modifier.padding(pad)) {
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

@Composable
private fun Nav(
    id: Tab,
    current: Tab,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = current == id,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, fontSize = 11.sp) },
    )
}
