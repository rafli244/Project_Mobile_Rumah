package com.example.rafli_3tia

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/** Data demo publik + laporan lokal. Pemilik tetap disimpan meskipun tampil anonim. */
class PengaduanRepository(context: Context) {
    private val prefs = context.getSharedPreferences("SICOMPLAINT_REPORTS", Context.MODE_PRIVATE)

    fun semua(): List<Pengaduan> = contoh() + lokal()
    fun riwayat(username: String): List<Pengaduan> = semua().filter {
        it.pelapor.equals(username, ignoreCase = true)
    }.reversed()
    fun cari(id: String): Pengaduan? = semua().find { it.id == id }

    private fun lokal(): List<Pengaduan> {
        val raw = prefs.getString("reports", "[]") ?: "[]"
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                Pengaduan(o.getString("id"), o.getString("judul"), o.getString("kategori"),
                    o.getString("lokasi"), o.getString("tanggal"), o.getString("status"),
                    o.getString("deskripsi"), o.getString("pelapor"),
                    o.optString("prioritas", "Normal"), o.optBoolean("anonim", false), o.optString("fotoPath", ""))
            }
        } catch (e: org.json.JSONException) {
            // Jangan menimpa data yang rusak secara diam-diam ketika pengguna mengirim laporan.
            throw IllegalStateException("Data laporan lokal tidak dapat dibaca", e)
        }
    }

    fun simpan(p: Pengaduan): Boolean {
        val all = lokal() + p
        val array = JSONArray()
        all.forEach {
            array.put(JSONObject().put("id", it.id).put("judul", it.judul)
                .put("kategori", it.kategori).put("lokasi", it.lokasi).put("tanggal", it.tanggal)
                .put("status", it.status).put("deskripsi", it.deskripsi).put("pelapor", it.pelapor)
                .put("prioritas", it.prioritas).put("anonim", it.anonim).put("fotoPath", it.fotoPath))
        }
        return prefs.edit().putString("reports", array.toString()).commit()
    }

    companion object {
        val kategori = listOf("Jalan Rusak", "Sampah Menumpuk", "Lampu Jalan Mati",
            "Drainase Tersumbat", "Fasilitas Umum Rusak", "Gangguan Ketertiban")

        fun contoh(): List<Pengaduan> {
            val judul = listOf("Jalan Berlubang Parah di Jl. Merdeka", "Sampah di Pasar Pagi",
                "Lampu Jalan Melati Padam", "Drainase di Gang Mawar Tersumbat",
                "Bangku Taman Kota Rusak", "Kebisingan di Lingkungan Warga")
            val lokasi = listOf("Jl. Merdeka No. 45, Kecamatan Kota", "Pasar Pagi, Blok B",
                "Jl. Melati, RT 03", "Gang Mawar, RT 02", "Taman Kota, sisi timur", "Jl. Kenanga, RT 05")
            val deskripsi = listOf(
                "Jalan berlubang sedalam sekitar 15 cm dan diameter 1 meter membahayakan pengendara. Mohon diperbaiki.",
                "Sampah menumpuk di dekat pintu pasar dan menimbulkan bau. Mohon dilakukan pengangkutan.",
                "Tiga lampu jalan tidak menyala pada malam hari sehingga jalan gelap dan berbahaya.",
                "Saluran air tertutup sampah dan lumpur. Air meluap ke jalan ketika hujan.",
                "Dua bangku taman patah dan baut terlepas. Fasilitas perlu diperbaiki agar aman digunakan.",
                "Suara musik keras sampai larut malam mengganggu waktu istirahat warga. Mohon ditindaklanjuti.")
            return kategori.indices.map { i -> Pengaduan("DEMO-${i+1}", judul[i], kategori[i],
                lokasi[i], String.format(Locale.ROOT, "%02d Oktober 2026", i+4),
                listOf("Diproses", "Menunggu", "Selesai")[i%3], deskripsi[i],
                if (i%2 == 0) "rafli" else "admin") }
        }
    }
}
