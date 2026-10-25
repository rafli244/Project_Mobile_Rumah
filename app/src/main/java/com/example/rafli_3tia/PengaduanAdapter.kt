package com.example.rafli_3tia

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

class PengaduanAdapter(
    private val reports: List<Pengaduan>,
    private val onDetail: (Pengaduan) -> Unit
) : BaseAdapter() {
    override fun getCount() = reports.size
    override fun getItem(position: Int) = reports[position]
    override fun getItemId(position: Int) = position.toLong()

    private class ViewHolder(val binding: ItemPengaduanBinding)

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
