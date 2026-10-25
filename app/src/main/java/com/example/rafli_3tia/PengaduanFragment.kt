package com.example.rafli_3tia

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.core.widget.addTextChangedListener
import com.example.rafli_3tia.databinding.FragmentPengaduanBinding

open class PengaduanFragment : Fragment(R.layout.fragment_pengaduan) {
    protected open val history = false
    private var mine = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentPengaduanBinding.bind(view)
        mine = savedInstanceState?.getBoolean("mine", true) ?: true
        b.rgScope.check(if (mine) R.id.rbMilikSaya else R.id.rbMasyarakat)
        b.rgScope.setOnCheckedChangeListener { _, id ->
            mine = id == R.id.rbMilikSaya
            refresh()
        }
        b.etFilter.addTextChangedListener { refresh() }
        refresh()
    }
    fun filter(own: Boolean, query: String) {
        mine = own
        view?.let {
            val b = FragmentPengaduanBinding.bind(it)
            b.rgScope.check(if (own) R.id.rbMilikSaya else R.id.rbMasyarakat)
            b.etFilter.setText(query)
        }
        refresh()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("mine", mine)
        super.onSaveInstanceState(outState)
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) refresh()
    }
    private fun refresh() {
        val b = view?.let { FragmentPengaduanBinding.bind(it) } ?: return
        val repository = PengaduanRepository(requireContext())
        val all = if (mine || history) {
            repository.riwayat((requireActivity() as MainActivity).username)
        } else {
            repository.semua().reversed()
        }
        val query = b.etFilter.text.toString().trim()
        val reports = all.filter {
            "${it.judul} ${it.kategori} ${it.lokasi}".contains(query, ignoreCase = true)
        }
        b.tvHeading.text = if (mine || history) "Riwayat laporan saya" else "Pengaduan masyarakat"
        b.tvCount.text = "${reports.size} laporan"
        b.listPengaduan.emptyView = b.tvEmpty
        val adapter = PengaduanAdapter(reports) {
            DetailActivity.open(requireContext(), it.id)
        }
        b.listPengaduan.adapter = adapter
        b.listPengaduan.setOnItemClickListener { _, _, position, _ -> DetailActivity.open(requireContext(), reports[position].id) }
    }
}
