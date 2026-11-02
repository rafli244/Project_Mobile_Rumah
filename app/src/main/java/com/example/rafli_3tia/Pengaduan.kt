package com.example.rafli_3tia

/*
 * PANDUAN PINDAH KE PROJECT BARU | MATERI 01,11
 * TUJUAN: model satu laporan, bukan daftar dan bukan komponen tampilan.
 * 1. Buat file Kotlin untuk data class; ubah nama/properti sesuai data soal.
 * 2. Samakan tipe dan nama properti dengan constructor serta adapter yang membacanya.
 * 3. Parameter bernilai default boleh tidak diisi ketika membuat objek.
 * 4. Array/List<Pengaduan> menampung banyak objek; data class ini hanya mewakili satu item.
 * UJI: buat satu objek, lalu tampilkan propertinya melalui adapter atau TextView.
 */

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
