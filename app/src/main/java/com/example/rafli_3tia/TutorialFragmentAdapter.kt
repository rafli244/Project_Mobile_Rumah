package com.example.rafli_3tia

/*
 * PANDUAN PINDAH KE PROJECT BARU | MATERI 01,15
 * TUJUAN: menyediakan halaman untuk ViewPager2; adapter bukan halaman keempat.
 * 1. Salin class ini bersama import Fragment, FragmentActivity dan FragmentStateAdapter.
 * 2. Di Activity siapkan List<Fragment>, buat adapter, lalu pasang ke ViewPager2.
 * 3. getItemCount adalah jumlah halaman; createFragment mengembalikan halaman pada posisi.
 * 4. Activity pemanggil harus FragmentActivity atau turunannya, misalnya AppCompatActivity.
 * UJI: jumlah titik dan halaman sama dengan jumlah anggota list.
 */

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// MATERI-15 FRAGMENTSTATEADAPTER: getItemCount = jumlah halaman; createFragment = halaman pada posisi.
// Adapter bukan Fragment keempat; Activity harus berupa FragmentActivity atau turunannya.
class TutorialFragmentAdapter(
    activity: FragmentActivity,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}
