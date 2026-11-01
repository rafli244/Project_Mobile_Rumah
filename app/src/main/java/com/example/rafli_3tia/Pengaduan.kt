package com.example.rafli_3tia

/*
 * =====================================================================================
 * DATA CLASS (MODEL DATA UNTUK LISTVIEW)
 * =====================================================================================
 * Materi  : Pertemuan 2 & 7
 * Konsep  : Wadah untuk menampung data tiap baris (Item Model).
 * =====================================================================================
 */

// MATERI-11 / MATERI-01: model satu laporan; data class membuat equals/copy/toString otomatis.
// val properti; nilai default dipakai jika argumen opsional tidak diisi.
data class Pengaduan(
    val id: String,
    val judul: String,
    val kategori: String,
    val lokasi: String,
    val tanggal: String,
    val status: String,
    val deskripsi: String,
    val pelapor: String,
    val prioritas: String = "Normal",
    val anonim: Boolean = false,
    val fotoPath: String = ""
)
