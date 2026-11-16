package com.example.rafli_3tia

/*
 * =====================================================================================
 * IMPLEMENTASI TABLAYOUT, VIEWPAGER2, TABLAYOUTMEDIATOR & BADGES
 * =====================================================================================
 * Materi  : Pertemuan 10 Praktikum Mobile Programming
 * Konsep  :
 *   1. TabLayout: Menampilkan deretan tab judul, ikon, dan badge di bagian atas.
 *   2. ViewPager2: Area swipe halaman yang dikelola oleh PengaduanTabsAdapter.
 *   3. TabLayoutMediator: Menghubungkan TabLayout dan ViewPager2 secara dua arah.
 *   4. Kustomisasi Tab: Menambahkan icon drawable dan badge angka real-time.
 *   5. Filter Terintegrasi: Search bar & ChipGroup memfilter isi RecyclerView di tiap tab.
 * =====================================================================================
 */

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.rafli_3tia.databinding.FragmentPengaduanBinding
import com.google.android.material.tabs.TabLayoutMediator

open class PengaduanFragment : Fragment(R.layout.fragment_pengaduan) {

    private var _binding: FragmentPengaduanBinding? = null
    private val binding get() = _binding!!

    private var tabMediator: TabLayoutMediator? = null

    // Properti filter yang dapat dibaca oleh setiap LaporanTabFragment anak
    val currentQuery: String
        get() = _binding?.etFilter?.text?.toString()?.trim() ?: ""

    val currentStatus: String?
        get() = when (_binding?.chipGroupFilter?.checkedChipId) {
            R.id.chipDiproses -> "Diproses"
            R.id.chipMenunggu -> "Menunggu"
            R.id.chipSelesai -> "Selesai"
            else -> null
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPengaduanBinding.bind(view)

        // 1. Inisialisasi Adapter ViewPager2
        val tabsAdapter = PengaduanTabsAdapter(this)
        binding.viewPagerPengaduan.adapter = tabsAdapter

        // 2. Hubungkan TabLayout & ViewPager2 dengan TabLayoutMediator
        tabMediator = TabLayoutMediator(binding.tabLayoutPengaduan, binding.viewPagerPengaduan) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Laporan Saya"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_figma_user)
                }
                1 -> {
                    tab.text = "Publik"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_figma_reports)
                }
                2 -> {
                    tab.text = "Selesai"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_circle)
                }
            }
        }
        tabMediator?.attach()

        // 3. Listener input pencarian & filter chip status
        binding.etFilter.doAfterTextChanged { refreshAllTabs() }
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, _ -> refreshAllTabs() }

        // Update badge angka di tiap tab
        updateBadges()
    }

    /**
     * Memperbarui badge pada setiap Tab sesuai materi Pertemuan 10 Tahap 2.1
     */
    fun updateBadges() {
        val b = _binding ?: return
        val repo = PengaduanRepository(requireContext())
        val username = (activity as? MainActivity)?.username ?: "rafli"

        // Badge Tab 0: Jumlah laporan aktif milik user yang sedang login
        val myActiveReports = repo.riwayat(username).count { !it.status.equals("Selesai", ignoreCase = true) }
        b.tabLayoutPengaduan.getTabAt(0)?.let { tab ->
            val badge = tab.getOrCreateBadge()
            if (myActiveReports > 0) {
                badge.isVisible = true
                badge.number = myActiveReports
                badge.backgroundColor = ContextCompat.getColor(requireContext(), R.color.primary)
            } else {
                badge.isVisible = false
            }
        }

        // Badge Tab 1: Total seluruh laporan pengaduan masyarakat
        val totalReports = repo.semua().size
        b.tabLayoutPengaduan.getTabAt(1)?.let { tab ->
            val badge = tab.getOrCreateBadge()
            badge.isVisible = true
            badge.number = totalReports
            badge.backgroundColor = ContextCompat.getColor(requireContext(), R.color.status_dalam_proses)
        }

        // Badge Tab 2: Total laporan yang sudah berhasil selesai ditangani
        val doneReports = repo.semua().count { it.status.equals("Selesai", ignoreCase = true) }
        b.tabLayoutPengaduan.getTabAt(2)?.let { tab ->
            val badge = tab.getOrCreateBadge()
            badge.isVisible = true
            badge.number = doneReports
            badge.backgroundColor = ContextCompat.getColor(requireContext(), R.color.status_selesai)
        }
    }

    /**
     * Memerintahkan semua tab fragment anak untuk memperbarui daftar RecyclerView-nya
     */
    fun refreshAllTabs() {
        updateBadges()
        childFragmentManager.fragments.forEach {
            (it as? LaporanTabFragment)?.refreshData()
        }
    }

    /**
     * Navigasi langsung ke tab tertentu (dipanggil dari MainActivity / Beranda / Profil)
     */
    fun filter(own: Boolean) {
        val targetIndex = if (own) 0 else 1
        _binding?.viewPagerPengaduan?.currentItem = targetIndex
        refreshAllTabs()
    }

    override fun onResume() {
        super.onResume()
        refreshAllTabs()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) refreshAllTabs()
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
        _binding = null
    }
}
