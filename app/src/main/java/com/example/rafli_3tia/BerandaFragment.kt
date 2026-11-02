package com.example.rafli_3tia

/*
 * PANDUAN PINDAH KE PROJECT BARU | MATERI 04,10,12,15
 * TUJUAN: contoh ListView sederhana + header + enam tombol kategori.
 * 1. Buat Fragment dan fragment_beranda.xml dengan ListView; bind(view) di onViewCreated.
 * 2. Untuk daftar sederhana ambil ArrayAdapter sampai listKategori.adapter = adapter.
 * 3. Ganti PengaduanRepository.kategori dengan daftar data soal; import android.widget.ArrayAdapter.
 * 4. Header adalah fitur tambahan. Tanpa addHeaderView, posisi data langsung position.
 * 5. Jika ikut memakai header, salin inflate HeaderBerandaBinding dan addHeaderView sebelum adapter.
 * 6. Grid butuh header_beranda.xml; listener category0..5 membuka showForm milik MainActivity.
 * 7. Di project baru ganti aksi showForm sesuai soal atau siapkan fungsi host yang sama.
 * UJI: semua item tampil; posisi klik benar; setiap tombol kategori menjalankan aksi yang sesuai.
 */

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rafli_3tia.databinding.FragmentBerandaBinding
import com.example.rafli_3tia.databinding.HeaderBerandaBinding

// MATERI-12: Fragment(R.layout.fragment_beranda) otomatis menyediakan view tanpa override onCreateView.
class BerandaFragment : Fragment(R.layout.fragment_beranda) {
    private var header: HeaderBerandaBinding? = null
    override fun onDestroyView() {
        header = null
        super.onDestroyView()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentBerandaBinding.bind(view)
        val h = HeaderBerandaBinding.inflate(layoutInflater, b.listKategori, false)
        header = h
        val metrics = resources.displayMetrics
        h.hero.layoutParams.height = ((metrics.heightPixels / metrics.density * .47f).coerceIn(280f, 440f) * metrics.density).toInt()
        b.listKategori.post {
            if (header === h) {
                h.hero.layoutParams.height = (b.listKategori.height * .86f).toInt()
                h.hero.requestLayout()
            }
        }
        h.tvAjakan.text = "Ada yang perlu\ndilaporkan?"
        b.listKategori.addHeaderView(h.root, null, false)
        // MATERI-10 MULAI: List kategori -> ArrayAdapter layout bawaan -> ListView.
        // Salin pembuatan adapter dan b.listKategori.adapter = adapter; import android.widget.ArrayAdapter.
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            PengaduanRepository.kategori
        )
        // PETUNJUK: TEMPEL SESUDAH PEMBUATAN ADAPTER: tanpa baris ini data tidak terhubung ke ListView.
        b.listKategori.adapter = adapter
        // MATERI-10: posisi data dikurangi headerViewsCount karena ListView punya header tambahan.
        b.listKategori.setOnItemClickListener { _, _, position, _ ->
            // PETUNJUK: HANYA KARENA ADA HEADER: pada daftar tanpa header gunakan position langsung.
            val index = position - b.listKategori.headerViewsCount
            if (index in PengaduanRepository.kategori.indices) Toast.makeText(requireContext(), "Kategori dipilih: ${PengaduanRepository.kategori[index]}", Toast.LENGTH_SHORT).show()
        }
        // MATERI-15 GRID: listener enam tombol GridLayout di header_beranda; Toast lalu buka form kategori.
        listOf(h.category0, h.category1, h.category2, h.category3, h.category4, h.category5).forEachIndexed { i, button ->
            button.setOnClickListener {
                Toast.makeText(requireContext(), "Kategori: ${PengaduanRepository.kategori[i]}", Toast.LENGTH_SHORT).show()
                (requireActivity() as MainActivity).showForm(PengaduanRepository.kategori[i])
            }
        }
        h.btnTutorial.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), TutorialActivity::class.java))
        }
        h.btnDaftarPublik.setOnClickListener { (requireActivity() as MainActivity).showReports(false) }
        refresh()
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) refresh()
    }
    private fun refresh() {
        val b = header ?: return
        val host = requireActivity() as MainActivity
        b.tvWelcomeTitle.text = "Halo, ${host.username}"
        val all = PengaduanRepository(requireContext()).semua()
        b.tvRingkasan.text = "${all.size} laporan  •  ${all.count { it.status == "Selesai" }} selesai ditangani"
    }
}
