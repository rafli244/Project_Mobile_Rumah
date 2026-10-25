package com.example.rafli_3tia

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.widget.addTextChangedListener
import com.example.rafli_3tia.databinding.FragmentBuatLaporanBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BuatLaporanFragment : Fragment(R.layout.fragment_buat_laporan) {

    fun selectCategory(category: String) {
        val index = PengaduanRepository.kategori.indexOf(category)
        if (index >= 0) view?.let { FragmentBuatLaporanBinding.bind(it).spinnerKategori.setSelection(index + 1) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentBuatLaporanBinding.bind(view)

        b.uploadArea.setOnClickListener {
            Toast.makeText(requireContext(), "Foto ilustrasi kategori otomatis digunakan.", Toast.LENGTH_SHORT).show()
        }
        b.btnHapusFoto.visibility = View.GONE
        b.ivFotoPreview.visibility = View.GONE

        b.btnOpsi.setOnClickListener {
            b.layoutOpsi.visibility = if (b.layoutOpsi.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            b.btnOpsi.text = if (b.layoutOpsi.visibility == View.VISIBLE) "Opsi laporan  −" else "Opsi laporan  +"
        }

        listOf(b.etJudul, b.etLokasi, b.etDeskripsi).forEach { field ->
            field.addTextChangedListener { if (!it.isNullOrBlank()) field.error = null }
        }

        b.spinnerKategori.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item,
            listOf("Pilih kategori") + PengaduanRepository.kategori
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        b.btnKirim.setOnClickListener {
            val fields = listOf(b.etJudul, b.etLokasi, b.etDeskripsi)
            fields.forEach { it.error = null }
            val empty = fields.firstOrNull { it.text.toString().trim().isEmpty() }
            if (empty != null) {
                empty.error = "Wajib diisi"
                empty.requestFocus()
                return@setOnClickListener
            }
            if (b.spinnerKategori.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "Pilih kategori pengaduan", Toast.LENGTH_SHORT).show()
                b.spinnerKategori.performClick()
                return@setOnClickListener
            }
            if (!b.cbKonfirmasi.isChecked) {
                Toast.makeText(requireContext(), "Konfirmasi kebenaran laporan terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val p = Pengaduan(
                UUID.randomUUID().toString(),
                b.etJudul.text.toString().trim(),
                b.spinnerKategori.selectedItem.toString(),
                b.etLokasi.text.toString().trim(),
                SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID")).format(Date()),
                "Menunggu",
                b.etDeskripsi.text.toString().trim(),
                (requireActivity() as MainActivity).username,
                if (b.rgPrioritas.checkedRadioButtonId == R.id.rbMendesak) "Mendesak" else "Normal",
                b.switchAnonim.isChecked,
                ""
            )

            b.btnKirim.isEnabled = false
            val saved = PengaduanRepository(requireContext()).simpan(p)
            b.btnKirim.isEnabled = true

            if (!saved) {
                Toast.makeText(requireContext(), "Laporan gagal disimpan. Silakan coba kembali.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            fields.forEach { it.text?.clear() }
            b.spinnerKategori.setSelection(0)
            b.cbKonfirmasi.isChecked = false
            b.switchAnonim.isChecked = false
            b.rgPrioritas.check(R.id.rbNormal)

            AlertDialog.Builder(requireContext())
                .setTitle("Laporan berhasil dikirim")
                .setMessage("${p.judul}\nStatus: Menunggu\nLaporan tersimpan di memori aplikasi.")
                .setPositiveButton("Lihat riwayat") { _, _ ->
                    (requireActivity() as MainActivity).showReports(true)
                }
                .setNegativeButton("Tutup", null)
                .show()
        }
    }
}
