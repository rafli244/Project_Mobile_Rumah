package com.example.rafli_3tia

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rafli_3tia.databinding.FragmentBerandaBinding
import com.example.rafli_3tia.databinding.HeaderBerandaBinding

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
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            PengaduanRepository.kategori
        )
        b.listKategori.adapter = adapter
        b.listKategori.setOnItemClickListener { _, _, position, _ ->
            val index = position - b.listKategori.headerViewsCount
            if (index in PengaduanRepository.kategori.indices) Toast.makeText(requireContext(), "Kategori dipilih: ${PengaduanRepository.kategori[index]}", Toast.LENGTH_SHORT).show()
        }
        listOf(h.category0, h.category1, h.category2, h.category3).forEachIndexed { i, button ->
            button.setOnClickListener { (requireActivity() as MainActivity).showForm(PengaduanRepository.kategori[i]) }
        }
        h.btnDaftarPublik.setOnClickListener { (requireActivity() as MainActivity).showReports(false) }
        h.etCari.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                (requireActivity() as MainActivity).showReports(false, h.etCari.text.toString().trim())
                true
            } else false
        }
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
