package com.octopusassem.aioctofree.browser

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object Tabs {
    fun open(ctx: Context, url: String) {
        try {
            val tabs = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            tabs.launchUrl(ctx, Uri.parse(url))
        } catch (_: Exception) {
            ctx.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }
}
