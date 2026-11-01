package com.example.rafli_3tia

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
