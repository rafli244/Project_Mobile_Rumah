package com.example.rafli_3tia

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
