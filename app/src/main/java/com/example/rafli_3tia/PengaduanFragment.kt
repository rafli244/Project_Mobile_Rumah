package com.example.rafli_3tia

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.rafli_3tia.databinding.FragmentPengaduanBinding

// MATERI-12: layout otomatis dari constructor Fragment(R.layout.fragment_pengaduan).
// onCreateView BELUM dioverride; onViewCreated dipakai setelah layout tersedia.
open class PengaduanFragment : Fragment(R.layout.fragment_pengaduan) {
    protected open val history = false
    private var mine = true
    // MATERI-12 / MATERI-04: bind(view) menghubungkan view yang sudah dibuat ke ViewBinding.
    // Jangan inflate layout kedua kali di sini; Activity memakai inflate + setContentView.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentPengaduanBinding.bind(view)
        mine = savedInstanceState?.getBoolean("mine", true) ?: true
        b.rgScope.check(if (mine) R.id.rbMilikSaya else R.id.rbMasyarakat)

        // MATERI-15 CHIPGROUP: checkedIds.firstOrNull -> Chip terpilih -> Toast -> refresh filter.
        b.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedId = checkedIds.firstOrNull()
            if (selectedId != null) {
                val chip = group.findViewById<com.google.android.material.chip.Chip>(selectedId)
                android.widget.Toast.makeText(requireContext(), "Filter: ${chip.text}", android.widget.Toast.LENGTH_SHORT).show()
            }
            refresh()
        }
        refresh()
    }
    fun filter(own: Boolean) {
        mine = own
        view?.let {
            val b = FragmentPengaduanBinding.bind(it)
            b.rgScope.check(if (own) R.id.rbMilikSaya else R.id.rbMasyarakat)
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
        // MATERI-15 CHIPGROUP: mapping id chip menjadi status; null berarti tanpa batasan status.
        val selectedStatus = when (b.chipGroupFilter.checkedChipId) {
            R.id.chipDiproses -> "Diproses"
            R.id.chipMenunggu -> "Menunggu"
            R.id.chipSelesai -> "Selesai"
            else -> null
        }
        val reports = if (selectedStatus == null) {
            all
        } else {
            all.filter { it.status.equals(selectedStatus, ignoreCase = true) }
        }
        b.tvHeading.text = if (mine || history) "Riwayat laporan saya" else "Pengaduan masyarakat"
        b.tvCount.text = "${reports.size} laporan"
        b.listPengaduan.emptyView = b.tvEmpty
        val adapter = PengaduanAdapter(reports) {
            DetailActivity.open(requireContext(), it.id)
        }
        // MATERI-11: pasang PengaduanAdapter; layout baris item_pengaduan.xml, model Pengaduan.kt.
        b.listPengaduan.adapter = adapter
        b.listPengaduan.setOnItemClickListener { _, _, position, _ -> DetailActivity.open(requireContext(), reports[position].id) }
    }
}
