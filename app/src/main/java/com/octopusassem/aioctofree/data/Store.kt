package com.octopusassem.aioctofree.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.util.UUID

class Store(ctx: Context) {
    private val app = ctx.applicationContext
    private val db = Helper(app).writableDatabase
    private val prefs = app.getSharedPreferences("aiocto", Context.MODE_PRIVATE)
    private val keys by lazy {
        val master = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "aiocto_keys",
            master,
            app,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    var lastProviderId: String
        get() = prefs.getString("last_provider", "groq") ?: "groq"
        set(v) = prefs.edit { putString("last_provider", v) }

    var lastModel: String
        get() = prefs.getString("last_model", "") ?: ""
        set(v) = prefs.edit { putString("last_model", v) }

    fun getKey(providerId: String): String =
        keys.getString("key_$providerId", "") ?: ""

    fun setKey(providerId: String, value: String) {
        keys.edit { putString("key_$providerId", value.trim()) }
    }

    fun hasAnyKey(): Boolean = Providers.all.any { getKey(it.id).isNotBlank() }

    fun chats(): List<Chat> {
        val out = mutableListOf<Chat>()
        db.rawQuery(
            "SELECT id,title,provider_id,model,category,updated_at FROM chats ORDER BY updated_at DESC",
            null,
        ).use { c ->
            while (c.moveToNext()) {
                out += Chat(
                    id = c.getString(0),
                    title = c.getString(1),
                    providerId = c.getString(2),
                    model = c.getString(3),
                    category = c.getString(4),
                    updatedAt = c.getLong(5),
                )
            }
        }
        return out
    }

    fun chat(id: String): Chat? {
        db.rawQuery(
            "SELECT id,title,provider_id,model,category,updated_at FROM chats WHERE id=?",
            arrayOf(id),
        ).use { c ->
            if (!c.moveToFirst()) return null
            return Chat(
                id = c.getString(0),
                title = c.getString(1),
                providerId = c.getString(2),
                model = c.getString(3),
                category = c.getString(4),
                updatedAt = c.getLong(5),
            )
        }
    }

    fun messages(chatId: String): List<Msg> {
        val out = mutableListOf<Msg>()
        db.rawQuery(
            "SELECT id,chat_id,role,content,ts FROM messages WHERE chat_id=? ORDER BY ts ASC, rowid ASC",
            arrayOf(chatId),
        ).use { c ->
            while (c.moveToNext()) {
                out += Msg(
                    id = c.getString(0),
                    chatId = c.getString(1),
                    role = c.getString(2),
                    content = c.getString(3),
                    ts = c.getLong(4),
                )
            }
        }
        return out
    }

    fun newChat(providerId: String, model: String, category: String = "general"): Chat {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        db.execSQL(
            "INSERT INTO chats(id,title,provider_id,model,category,updated_at) VALUES(?,?,?,?,?,?)",
            arrayOf<Any?>(id, "محادثة جديدة", providerId, model, category, now),
        )
        return Chat(id, "محادثة جديدة", providerId, model, category, now)
    }

    fun renameChat(id: String, title: String) {
        db.execSQL(
            "UPDATE chats SET title=?, updated_at=? WHERE id=?",
            arrayOf<Any?>(title.take(80), System.currentTimeMillis(), id),
        )
    }

    fun touchChat(id: String) {
        db.execSQL(
            "UPDATE chats SET updated_at=? WHERE id=?",
            arrayOf<Any?>(System.currentTimeMillis(), id),
        )
    }

    fun updateChatRoute(id: String, providerId: String, model: String, category: String) {
        db.execSQL(
            "UPDATE chats SET provider_id=?, model=?, category=?, updated_at=? WHERE id=?",
            arrayOf<Any?>(providerId, model, category, System.currentTimeMillis(), id),
        )
    }

    fun addMessage(chatId: String, role: String, content: String): Msg {
        val id = UUID.randomUUID().toString()
        val ts = System.currentTimeMillis()
        db.execSQL(
            "INSERT INTO messages(id,chat_id,role,content,ts) VALUES(?,?,?,?,?)",
            arrayOf<Any?>(id, chatId, role, content, ts),
        )
        touchChat(chatId)
        if (role == "user") {
            val t = content.trim().replace('\n', ' ')
            if (t.isNotEmpty()) {
                val cur = chat(chatId)
                if (cur != null && (cur.title == "محادثة جديدة" || cur.title == "New chat")) {
                    renameChat(chatId, t.take(48))
                }
            }
        }
        return Msg(id, chatId, role, content, ts)
    }

    fun updateMessage(id: String, content: String) {
        db.execSQL("UPDATE messages SET content=? WHERE id=?", arrayOf<Any?>(content, id))
    }

    fun deleteChat(id: String) {
        db.execSQL("DELETE FROM messages WHERE chat_id=?", arrayOf<Any?>(id))
        db.execSQL("DELETE FROM chats WHERE id=?", arrayOf<Any?>(id))
    }

    fun search(q: String): List<Chat> {
        val like = "%${q.trim()}%"
        if (q.isBlank()) return chats()
        val out = mutableListOf<Chat>()
        db.rawQuery(
            """
            SELECT DISTINCT c.id,c.title,c.provider_id,c.model,c.category,c.updated_at
            FROM chats c
            LEFT JOIN messages m ON m.chat_id=c.id
            WHERE c.title LIKE ? OR m.content LIKE ?
            ORDER BY c.updated_at DESC
            """.trimIndent(),
            arrayOf(like, like),
        ).use { c ->
            while (c.moveToNext()) {
                out += Chat(
                    id = c.getString(0),
                    title = c.getString(1),
                    providerId = c.getString(2),
                    model = c.getString(3),
                    category = c.getString(4),
                    updatedAt = c.getLong(5),
                )
            }
        }
        return out
    }

    private class Helper(ctx: Context) : SQLiteOpenHelper(ctx, "aioctofree.db", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE chats(
                  id TEXT PRIMARY KEY,
                  title TEXT NOT NULL,
                  provider_id TEXT NOT NULL,
                  model TEXT NOT NULL,
                  category TEXT NOT NULL,
                  updated_at INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE messages(
                  id TEXT PRIMARY KEY,
                  chat_id TEXT NOT NULL,
                  role TEXT NOT NULL,
                  content TEXT NOT NULL,
                  ts INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL("CREATE INDEX idx_msg_chat ON messages(chat_id, ts)")
        }

        override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {}
    }
}
