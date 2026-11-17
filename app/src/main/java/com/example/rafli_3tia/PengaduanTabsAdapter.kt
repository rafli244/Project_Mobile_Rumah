package com.example.rafli_3tia

/*
 * =====================================================================================
 * FRAGMENT STATE ADAPTER UNTUK VIEWPAGER2
 * =====================================================================================
 * Materi  : Pertemuan 10 Praktikum Mobile Programming
 * Konsep  :
 *   1. FragmentStateAdapter: Menyediakan instance Fragment untuk tiap halaman ViewPager2.
 *   2. getItemCount(): Mengatur jumlah tab (3 tab: Laporan Saya, Publik, Selesai).
 *   3. createFragment(position): Membuat dan mengembalikan LaporanTabFragment sesuai posisi.
 * =====================================================================================
 */

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PengaduanTabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // Menyediakan 3 tab pengaduan (melebihi syarat minimal 2 tab dari modul)
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return LaporanTabFragment.newInstance(position)
    }
}
