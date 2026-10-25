package com.example.rafli_3tia

import android.content.Context
import org.json.JSONObject
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID

/** Akun lokal untuk demonstrasi UTS. Tidak mengubah kredensial demo yang telah ada. */
class AkunRepository(context: Context) {
    private val prefs = context.getSharedPreferences("LOCAL_ACCOUNTS", Context.MODE_PRIVATE)
    private fun key(username: String) = username.lowercase(Locale.ROOT)
    private fun account(username: String): JSONObject? = prefs.getString(key(username), null)?.let { JSONObject(it) }
    private fun hash(password: String, salt: String) = MessageDigest.getInstance("SHA-256")
        .digest((salt + password).toByteArray()).joinToString("") { "%02x".format(it) }
    fun valid(username: String, password: String): Boolean {
        if (username.equals("rafli", true) && password == "12345") return true
        if (username.equals("admin", true) && password == "admin123") return true
        val a = account(username) ?: return false
        return a.has("hash") && hash(password, a.getString("salt")) == a.getString("hash")
    }
    fun daftar(nama: String, username: String, password: String): String? {
        if (nama.isBlank() || !username.matches(Regex("[A-Za-z0-9_.@-]{3,64}"))) return "Isi nama dan username yang valid (minimal 3 karakter)."
        if (password.length < 6) return "Kata sandi minimal 6 karakter."
        if (key(username) in listOf("rafli", "admin") || account(username) != null) return "Username sudah digunakan."
        val salt = UUID.randomUUID().toString()
        val a = JSONObject().put("nama", nama.trim()).put("email", "").put("salt", salt).put("hash", hash(password, salt))
        return if (prefs.edit().putString(key(username), a.toString()).commit()) null else "Akun gagal disimpan."
    }
    fun nama(username: String) = account(username)?.optString("nama")?.takeIf { it.isNotBlank() }
        ?: when { username.equals("rafli", true) -> "Rafli Hanafi"; username.equals("admin", true) -> "Admin SiComplaint"; else -> username }
    fun email(username: String) = account(username)?.optString("email")?.takeIf { it.isNotBlank() } ?: "@$username"
    fun update(username: String, nama: String, email: String): Boolean {
        val a = account(username) ?: JSONObject()
        a.put("nama", nama.trim()).put("email", email.trim())
        return prefs.edit().putString(key(username), a.toString()).commit()
    }
}
