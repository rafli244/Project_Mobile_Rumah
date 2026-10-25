package com.example.rafli_3tia

import android.content.Context
import java.util.Locale

/**
 * Repository data laporan menggunakan in-memory MutableList
 * sesuai materi Collection pada Modul Praktikum Pertemuan 2.
 */
class PengaduanRepository(context: Context? = null) {

    fun semua(): List<Pengaduan> = listLaporan

    fun riwayat(username: String): List<Pengaduan> = semua().filter {
        it.pelapor.equals(username, ignoreCase = true) || it.id == "DEMO-1"
    }.reversed()

    fun cari(id: String): Pengaduan? = semua().find { it.id == id }

    fun simpan(p: Pengaduan): Boolean {
        listLaporan.add(p)
        return true
    }

    companion object {
        val kategori = listOf(
            "Jalan Rusak", "Sampah Menumpuk", "Lampu Jalan Mati",
            "Drainase Tersumbat", "Fasilitas Umum Rusak", "Gangguan Ketertiban"
        )

        private val listLaporan: MutableList<Pengaduan> = contoh().toMutableList()

        fun contoh(): List<Pengaduan> {
            val judul = listOf(
                "Jalan Berlubang Parah di Jl. Merdeka", "Sampah di Pasar Pagi",
                "Lampu Jalan Melati Padam", "Drainase di Gang Mawar Tersumbat",
                "Bangku Taman Kota Rusak", "Kebisingan di Lingkungan Warga"
            )
            val lokasi = listOf(
                "Jl. Merdeka No. 45, Kecamatan Kota", "Pasar Pagi, Blok B",
                "Jl. Melati, RT 03", "Gang Mawar, RT 02", "Taman Kota, sisi timur", "Jl. Kenanga, RT 05"
            )
            val deskripsi = listOf(
                "Jalan berlubang sedalam sekitar 15 cm dan diameter 1 meter membahayakan pengendara. Mohon diperbaiki.",
                "Sampah menumpuk di dekat pintu pasar dan menimbulkan bau. Mohon dilakukan pengangkutan.",
                "Tiga lampu jalan tidak menyala pada malam hari sehingga jalan gelap dan berbahaya.",
                "Saluran air tertutup sampah dan lumpur. Air meluap ke jalan ketika hujan.",
                "Dua bangku taman patah dan baut terlepas. Fasilitas perlu diperbaiki agar aman digunakan.",
                "Suara musik keras sampai larut malam mengganggu waktu istirahat warga. Mohon ditindaklanjuti."
            )
            return kategori.indices.map { i ->
                Pengaduan(
                    "DEMO-${i + 1}",
                    judul[i],
                    kategori[i],
                    lokasi[i],
                    String.format(Locale.ROOT, "%02d Oktober 2026", i + 4),
                    listOf("Diproses", "Menunggu", "Selesai")[i % 3],
                    deskripsi[i],
                    if (i % 2 == 0) "rafli" else "admin"
                )
            }
        }
    }
}
