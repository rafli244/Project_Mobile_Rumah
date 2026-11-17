package com.example.rafli_3tia

/*
 * =====================================================================================
 * FRAGMENT TAB CONTENT & RECYCLERVIEW IMPLEMENTATION
 * =====================================================================================
 * Materi  : Pertemuan 10 Praktikum Mobile Programming (Tahap 4)
 * Konsep  :
 *   1. Fragment isi tab di dalam ViewPager2 (Tahap 4.1).
 *   2. RecyclerView dengan LayoutManager & Adapter (Tahap 4.6).
 *   3. PengaduanRecyclerAdapter: Mengisi data pengaduan ke ViewHolder.
 *   4. Interaksi Item Click: Menampilkan feedback Toast sesuai modul dan membuka detail.
 *   5. onDestroyView: Membersihkan _binding = null untuk mencegah memory leak (Halaman 12).
 * =====================================================================================
 */

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rafli_3tia.databinding.FragmentLaporanTabBinding

class LaporanTabFragment : Fragment() {

    private var _binding: FragmentLaporanTabBinding? = null
    private val binding get() = _binding!!

    private var tabPosition: Int = 0
    private lateinit var reportAdapter: PengaduanRecyclerAdapter

    companion object {
        private const val ARG_POSITION = "ARG_POSITION"

        fun newInstance(position: Int): LaporanTabFragment {
            return LaporanTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabPosition = arguments?.getInt(ARG_POSITION) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaporanTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Adapter RecyclerView dengan callback klik item sesuai modul (Tahap 4.4 & 4.6)
        reportAdapter = PengaduanRecyclerAdapter { report ->
            Toast.makeText(
                requireContext(),
                "Anda memilih ${report.judul}",
                Toast.LENGTH_SHORT
            ).show()
            DetailActivity.open(requireContext(), report.id)
        }

        // LayoutManager & Adapter dipasang ke RecyclerView sesuai modul Tahap 4.6
        binding.rvPengaduanTab.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reportAdapter
        }

        refreshData()
    }

    fun refreshData() {
        if (_binding == null) return

        val repo = PengaduanRepository(requireContext())
        val username = (activity as? MainActivity)?.username ?: "rafli"

        // 1. Ambil data sesuai jenis tab
        val rawList: List<Pengaduan> = when (tabPosition) {
            0 -> repo.riwayat(username)
            1 -> repo.semua().asReversed()
            else -> repo.semua().filter { it.status.equals("Selesai", ignoreCase = true) }.asReversed()
        }

        // 2. Terapkan filter query pencarian & filter status dari parent PengaduanFragment
        val parent = parentFragment as? PengaduanFragment
        val query = parent?.currentQuery ?: ""
        val status = parent?.currentStatus

        val filtered = rawList.filter { report ->
            val matchStatus = status == null || report.status.equals(status, ignoreCase = true)
            val matchQuery = query.isEmpty() ||
                report.judul.contains(query, ignoreCase = true) ||
                report.kategori.contains(query, ignoreCase = true) ||
                report.lokasi.contains(query, ignoreCase = true)
            matchStatus && matchQuery
        }

        // 3. Update Heading & Total Laporan
        binding.tvTabHeading.text = when (tabPosition) {
            0 -> "Laporan Pengaduan Saya"
            1 -> "Pengaduan Seluruh Warga"
            else -> "Laporan yang Selesai Ditangani"
        }
        binding.tvTabCount.text = "${filtered.size} laporan ditemukan"

        // 4. Update status Empty State & Adapter RecyclerView
        binding.tvEmpty.isVisible = filtered.isEmpty()
        binding.rvPengaduanTab.isVisible = filtered.isNotEmpty()
        reportAdapter.submitList(filtered)
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onDestroyView() {
        binding.rvPengaduanTab.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
