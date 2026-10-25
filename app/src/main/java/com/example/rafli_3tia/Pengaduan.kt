package com.example.rafli_3tia

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
