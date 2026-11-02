package com.example.rafli_3tia

/*
 * PANDUAN PINDAH KE PROJECT BARU | MATERI 03,04,15
 * TUJUAN: wadah tiga halaman tutorial yang bisa digeser.
 * 1. Salin activity_tutorial.xml, adapter dan ketiga TutorialFragment beserta layout-nya.
 * 2. Tambahkan dependency ViewPager2 dan dotsindicator dari Gradle app; Sync.
 * 3. Sesuaikan package/import binding; daftarkan TutorialActivity di Manifest.
 * 4. Pasang adapter ke tutorialViewPager SEBELUM dotIndicator.attachTo.
 * 5. Buka dari listener tombol dengan Intent menuju TutorialActivity.
 * UJI: swipe 1->2->3, titik mengikuti, Lewati/Mulai menutup tutorial.
 */

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rafli_3tia.databinding.ActivityTutorialBinding

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MATERI-15 VIEWPAGER2: tiga Fragment = tiga halaman, di dalam satu TutorialActivity.
        val fragments = listOf(
            Tutorial1Fragment(),
            Tutorial2Fragment(),
            Tutorial3Fragment()
        )
        // MATERI-15: pasang FragmentStateAdapter dahulu, baru attachTo pager untuk DotsIndicator.
        // Pasangan: activity_tutorial.xml; Gradle ViewPager2 + dotsindicator; Manifest TutorialActivity.
        val adapter = TutorialFragmentAdapter(this, fragments)
        binding.tutorialViewPager.adapter = adapter
        // PETUNJUK: URUTAN WAJIB: panggil setelah tutorialViewPager.adapter di baris sebelumnya.
        binding.dotIndicator.attachTo(binding.tutorialViewPager)
    }
}
