package com.example.rafli_3tia

/*
 * PANDUAN PINDAH KE PROJECT BARU | MATERI 04,12,15
 * TUJUAN: halaman tutorial 3; salin bersama fragment_tutorial3.xml.
 * 1. Buat class Fragment(R.layout.fragment_tutorial3) dan layout pasangannya.
 * 2. onViewCreated dipanggil setelah layout siap; bind(view) menggunakan view yang sudah ada.
 * 3. Ganti package, import binding dan nama layout jika nama file di project baru berbeda.
 * 4. Listener memanggil requireActivity().finish untuk menutup wadah tutorial.
 * 5. Masukkan instance Fragment ini ke list pada TutorialActivity.
 * UJI: halaman tampil, tombol menutup tutorial. Tidak ada override onCreateView manual.
 */

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.rafli_3tia.databinding.FragmentTutorial3Binding

// MATERI-12 / MATERI-15: halaman tutorial 3; Fragment(R.layout...) menggantikan inflate manual di onCreateView.
// onViewCreated memasang listener; requireActivity().finish() menutup Activity tutorial.
class Tutorial3Fragment : Fragment(R.layout.fragment_tutorial3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTutorial3Binding.bind(view)
        binding.btnMulai.setOnClickListener {
            requireActivity().finish()
        }
    }
}
