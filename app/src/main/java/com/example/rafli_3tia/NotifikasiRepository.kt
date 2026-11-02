package com.example.rafli_3tia

/*
 * PANDUAN PINDAH KE PROJECT BARU | MATERI 01,07,11
 * TUJUAN: menghasilkan notifikasi lokal dari laporan, menyimpan status dibaca.
 * 1. Notifikasi adalah data class; sesuaikan properti jika memakai model ini di project baru.
 * 2. NOTIFICATION_STATE menyimpan Boolean dibaca, bukan data notifikasi lengkap.
 * 3. key menggabungkan pengguna dan ID; pembacaan dan penulisan wajib memakai key sama.
 * 4. semua menggunakan flatMap/map dan data PengaduanRepository; ini pengembangan Kotlin.
 * 5. Tidak terhubung push notification atau server; untuk contoh Preferences dasar lihat LginActivity.
 * UJI: mark satu item; baca ulang dan pastikan dibaca=true untuk pengguna yang sama.
 */

import android.content.Context
import java.util.Locale

data class Notifikasi(val id: String, val reportId: String, val judul: String, val isi: String, val tanggal: String, val dibaca: Boolean)

/** Notifikasi lokal diturunkan dari laporan, bukan push dari layanan atau dinas. */
class NotifikasiRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("NOTIFICATION_STATE", Context.MODE_PRIVATE)
    private fun key(user: String, id: String) = user.lowercase(Locale.ROOT) + ":" + id
    fun semua(user: String): List<Notifikasi> = PengaduanRepository(context).riwayat(user).flatMap { p ->
        val events = mutableListOf("dikirim" to "Laporan berhasil dikirim")
        if (p.status in listOf("Diproses", "Selesai")) events += "verifikasi" to "Laporan terverifikasi"
        if (p.status == "Diproses") events += "proses" to "Laporan sedang diproses"
        if (p.status == "Selesai") events += "selesai" to "Laporan selesai"
        events.reversed().map { (stage, title) ->
            val id = "${p.id}:$stage"
            Notifikasi(id, p.id, title, "${p.judul}${if (p.id.startsWith("DEMO")) " • Contoh" else ""}", p.tanggal, prefs.getBoolean(key(user, id), false))
        }
    }
    fun mark(user: String, id: String) { prefs.edit().putBoolean(key(user, id), true).apply() }
    fun markAll(user: String) { val edit = prefs.edit(); semua(user).forEach { edit.putBoolean(key(user, it.id), true) }; edit.apply() }
    fun unread(user: String) = semua(user).count { !it.dibaca }
}
