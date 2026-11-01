package com.example.rafli_3tia

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.rafli_3tia.databinding.FragmentTutorial1Binding

// MATERI-12 / MATERI-15: halaman tutorial 1; Fragment(R.layout...) menggantikan inflate manual di onCreateView.
// onViewCreated memasang listener; requireActivity().finish() menutup Activity tutorial.
class Tutorial1Fragment : Fragment(R.layout.fragment_tutorial1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTutorial1Binding.bind(view)
        binding.btnLewati.setOnClickListener {
            requireActivity().finish()
        }
    }
}
