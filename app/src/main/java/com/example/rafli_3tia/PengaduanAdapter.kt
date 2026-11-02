package com.example.rafli_3tia

/*
 * PANDUAN PINDAH KE PROJECT BARU | MATERI 04,11
 * TUJUAN: custom BaseAdapter untuk satu baris ListView.
 * 1. Salin Pengaduan.kt dan item_pengaduan.xml; ganti kolom/id sesuai soal.
 * 2. Salin class adapter utuh: constructor, getCount/getItem/getItemId, ViewHolder, getView.
 * 3. Ganti ItemPengaduanBinding dengan binding layout baris baru.
 * 4. getView membaca satu model lalu mengisi SEMUA tampilan yang berubah agar view daur ulang benar.
 * 5. Jika memakai adapter asli, salin fungsi statusLabel/categoryIcon di bawah dan resource terkait.
 * 6. Pasang adapter ke ListView seperti PengaduanFragment; callback onDetail menentukan aksi klik.
 * UJI: scroll berulang, judul/gambar/status tidak tertukar; klik detail sesuai baris.
 * Bukan custom ArrayAdapter: kelas ini mewarisi BaseAdapter.
 */

/*
 * =====================================================================================
 * CUSTOM LISTVIEW ADAPTER & ITEM CLICK LISTENER
 * =====================================================================================
 * Materi  : Pertemuan 7
 * Konsep  :
 *   1. Subclass BaseAdapter(): Wajib implement getCount(), getItem(), getItemId(), getView().
 *   2. ViewHolder Pattern: Menyimpan binding baris di row.tag agar hemat memori saat scroll.
 *   3. Binding Baris: ItemPengaduanBinding.inflate(LayoutInflater.from(parent.context), parent, false).
 *   4. Aksi Klik Item: binding.btnDetail.setOnClickListener { onDetail(item) }.
 * =====================================================================================
 */

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.example.rafli_3tia.databinding.ItemPengaduanBinding

// MATERI-11 MULAI: custom BaseAdapter menerima List<Pengaduan> dan callback klik detail.
// Salin class utuh + Pengaduan.kt + item_pengaduan.xml + fungsi statusLabel/categoryIcon yang dipakai.
class PengaduanAdapter(
    private val reports: List<Pengaduan>,
    private val onDetail: (Pengaduan) -> Unit
) : BaseAdapter() {
    override fun getCount() = reports.size
    override fun getItem(position: Int) = reports[position]
    override fun getItemId(position: Int) = position.toLong()

    private class ViewHolder(val binding: ItemPengaduanBinding)

    // MATERI-11: getView mengisi satu baris; convertView nullable untuk daur ulang view.
    // ViewHolder menyimpan binding; jika null inflate baris baru, else gunakan baris lama.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val row: View
        if (convertView == null) {
            val binding = ItemPengaduanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            holder = ViewHolder(binding)
            row = binding.root
            row.tag = holder
        } else {
            row = convertView
            holder = row.tag as ViewHolder
        }
        // PETUNJUK: MODEL SATU BARIS: sesuaikan p.judul/dll dengan properti data class baru.
        val p = getItem(position)
        with(holder.binding) {
            tvJudul.text = p.judul
            tvKategori.text = p.kategori
            tvLokasi.text = p.lokasi
            tvTanggal.text = p.tanggal
            tvStatus.text = statusLabel(p.status)
            if (p.id == "DEMO-1" || p.kategori == "Jalan Rusak") {
                ivLaporan.setImageResource(R.drawable.lubang)
                ivLaporan.imageTintList = null
                ivLaporan.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                ivLaporan.setPadding(0, 0, 0, 0)
            } else {
                ivLaporan.setImageResource(categoryIcon(p.kategori))
                ivLaporan.imageTintList = ContextCompat.getColorStateList(parent.context, R.color.primary)
                ivLaporan.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                val p16 = (16 * parent.context.resources.displayMetrics.density).toInt()
                ivLaporan.setPadding(p16, p16, p16, p16)
            }
            tvStatus.setTextColor(ContextCompat.getColor(parent.context, R.color.white))
            ivLaporan.contentDescription = "Ikon laporan ${p.kategori}"
            btnDetail.contentDescription = "Detail ${p.judul}"
            // PETUNJUK: CALLBACK: aksi detail disediakan pembuat adapter; jangan hardcode Activity baru tanpa kebutuhan.
            btnDetail.setOnClickListener { onDetail(p) }
        }
        return row
    }
}

fun statusColor(status: String) = when (status) {
    "Selesai" -> R.color.status_selesai
    "Diproses" -> R.color.status_dalam_proses
    "Ditolak" -> R.color.status_ditolak
    else -> R.color.status_menunggu
}

fun statusLabel(status: String) = if (status == "Diproses") "Dalam Proses" else status
fun statusBackground(status: String) = when (status) {
    "Selesai" -> R.drawable.chip_selesai
    "Diproses", "Dalam Proses" -> R.drawable.chip_diproses
    "Ditolak" -> R.drawable.chip_ditolak
    else -> R.drawable.chip_menunggu
}
fun categoryIcon(category: String) = when (category) {
    "Sampah Menumpuk" -> R.drawable.ic_figma_trash
    "Lampu Jalan Mati" -> R.drawable.ic_figma_lamp
    "Drainase Tersumbat" -> R.drawable.ic_figma_drain
    "Fasilitas Umum Rusak" -> R.drawable.ic_figma_facility
    "Gangguan Ketertiban" -> R.drawable.ic_figma_order
    else -> R.drawable.ic_figma_road
}
