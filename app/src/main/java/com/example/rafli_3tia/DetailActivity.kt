package com.example.rafli_3tia

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rafli_3tia.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (e: Exception) {
            Log.e("DetailActivity", "EdgeToEdge error", e)
        }

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: Exception) {
            Log.e("DetailActivity", "Insets error", e)
        }

        // ============================================================
        // 1. PENERAPAN TOOLBAR DENGAN TOMBOL BACK (SESUAI TUGAS)
        // ============================================================
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Event klik navigasi panah kembali pada Toolbar
        binding.toolbarDetail.setNavigationOnClickListener {
            finish()
        }

        // 2. Tombol Button di bawah juga berfungsi kembali ke MainActivity
        binding.btnBackToMain.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
