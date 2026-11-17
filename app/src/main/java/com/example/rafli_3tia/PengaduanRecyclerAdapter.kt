package com.example.rafli_3tia

/*
 * =====================================================================================
 * RECYCLERVIEW ADAPTER & VIEWHOLDER
 * =====================================================================================
 * Materi  : Pertemuan 10 Praktikum Mobile Programming
 * Konsep  :
 *   1. RecyclerView.Adapter: Jembatan antara dataset pengaduan dan RecyclerView.
 *   2. ViewHolder Pattern: Menyimpan binding baris agar tidak perlu findViewById ulang.
 *   3. onCreateViewHolder: Meng-inflate layout item_pengaduan.xml ke dalam ViewHolder.
 *   4. onBindViewHolder: Memasukkan data model Pengaduan ke komponen tampilan baris.
 *   5. getItemCount: Menghitung total item dalam daftar pengaduan.
 *   6. onItemClick lambda: Callback ketika baris item disentuh pengguna.
 * =====================================================================================
 */

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rafli_3tia.databinding.ItemPengaduanBinding

class PengaduanRecyclerAdapter(
    private var reportList: List<Pengaduan> = emptyList(),
    private val onItemClick: (Pengaduan) -> Unit
) : RecyclerView.Adapter<PengaduanRecyclerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPengaduanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPengaduanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reportList[position]
        holder.binding.bindReport(report, onItemClick)
        holder.itemView.setOnClickListener { onItemClick(report) }
    }

    override fun getItemCount(): Int = reportList.size

    fun submitList(newList: List<Pengaduan>) {
        reportList = newList
        notifyDataSetChanged()
    }
}
