package com.example.rafli_3tia

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
        binding.dotIndicator.attachTo(binding.tutorialViewPager)
    }
}
