package com.example.rafli_3tia

/*
 * Beranda menampilkan header dengan grid kategori statis dan satu kartu laporan terbaru.
 * Tambahkan header sebelum memasang PengaduanAdapter pada ListView.
 */

import android.os.Bundle
import android.view.View
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
        val h = HeaderBerandaBinding.inflate(layoutInflater, b.listPengaduanTerbaru, false)
        header = h
        val metrics = resources.displayMetrics
        h.hero.layoutParams.height = ((metrics.heightPixels / metrics.density * .47f).coerceIn(280f, 440f) * metrics.density).toInt()
        b.listPengaduanTerbaru.post {
            if (header === h) {
                h.hero.layoutParams.height = (b.listPengaduanTerbaru.height * .86f).toInt()
                h.hero.requestLayout()
            }
        }
        h.tvAjakan.text = "Ada yang perlu\ndilaporkan?"
        b.listPengaduanTerbaru.addHeaderView(h.root, null, false)
        h.btnAbout.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), AboutActivity::class.java))
        }
        h.btnPortalWeb.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), WebViewActivity::class.java))
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
        // Repository menambahkan laporan baru di akhir daftar.
        val latest = all.takeLast(1)
        val list = view?.let { FragmentBerandaBinding.bind(it).listPengaduanTerbaru } ?: return
        list.adapter = PengaduanAdapter(latest) { report ->
            DetailActivity.open(requireContext(), report.id)
        }
        b.tvRingkasan.text = "${all.size} laporan  •  ${all.count { it.status == "Selesai" }} selesai ditangani"
    }
}
