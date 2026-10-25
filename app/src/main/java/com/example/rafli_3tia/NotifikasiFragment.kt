package com.example.rafli_3tia

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import com.example.rafli_3tia.databinding.FragmentNotifikasiBinding
import com.example.rafli_3tia.databinding.ItemNotifikasiBinding

class NotifikasiFragment : Fragment(R.layout.fragment_notifikasi) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        FragmentNotifikasiBinding.bind(view).btnBacaSemua.setOnClickListener {
            NotifikasiRepository(requireContext()).markAll((requireActivity() as MainActivity).username); refresh()
        }
        refresh()
    }
    override fun onResume() { super.onResume(); refresh() }
    override fun onHiddenChanged(hidden: Boolean) { super.onHiddenChanged(hidden); if (!hidden) refresh() }
    private fun refresh() {
        val b = view?.let { FragmentNotifikasiBinding.bind(it) } ?: return
        val host = requireActivity() as MainActivity
        val repo = NotifikasiRepository(requireContext())
        val all = repo.semua(host.username)
        b.listNotifikasi.emptyView = b.tvEmpty
        b.listNotifikasi.adapter = NotifikasiAdapter(all)
        b.listNotifikasi.setOnItemClickListener { _, _, pos, _ ->
            repo.mark(host.username, all[pos].id); refresh(); DetailActivity.open(requireContext(), all[pos].reportId)
        }
        host.refreshBadge()
    }
}
private class NotifikasiAdapter(private val items: List<Notifikasi>) : BaseAdapter() {
    private class Holder(val b: ItemNotifikasiBinding)
    override fun getCount() = items.size
    override fun getItem(position: Int) = items[position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder = if (convertView == null) Holder(ItemNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)).also { it.b.root.tag = it } else convertView.tag as Holder
        val p = items[position]
        with(holder.b) {
            tvJudul.text = p.judul; tvIsi.text = p.isi; tvWaktu.text = p.tanggal
            dotUnread.visibility = if (p.dibaca) View.INVISIBLE else View.VISIBLE
            root.setBackgroundColor(parent.context.getColor(if (p.dibaca) R.color.background_app else R.color.primary_soft))
        }
        return holder.b.root
    }
}
