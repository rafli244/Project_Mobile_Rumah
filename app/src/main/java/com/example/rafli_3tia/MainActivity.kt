package com.example.rafli_3tia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rafli_3tia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (e: Exception) {
            Log.e("MainActivity", "EdgeToEdge error", e)
        }

        // 1. Menggunakan ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Insets error", e)
        }

        // ============================================================
        // 2. PENERAPAN TOOLBAR PADA MAIN ACTIVITY (SESUAI TUGAS)
        // ============================================================
        setSupportActionBar(binding.toolbarMain)

        // ============================================================
        // 3. MEMBACA SESI USER DARI SHAREDPREFERENCES ATAU INTENT
        // ============================================================
        val sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("USERNAME", null)
        val intentUsername = intent.getStringExtra("EXTRA_USERNAME")

        val currentUsername = intentUsername ?: savedUsername ?: "Warga"
        binding.tvWelcomeTitle.text = "Halo, $currentUsername!"

        // 4. Navigasi Intent ke DetailActivity
        binding.btnDetail.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        // 5. Menampilkan AlertDialog Konfirmasi Logout
        binding.btnLogout.setOnClickListener {
            showLogoutAlertDialog()
        }
    }

    private fun showLogoutAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Logout")
        builder.setMessage("Apakah Anda yakin ingin keluar dari akun SiComplaint?")

        builder.setPositiveButton("Ya, Keluar") { _, _ ->
            // 1. Reset status login di SharedPreferences
            val sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean("isLogin", false)
                remove("USERNAME")
                apply()
            }

            Toast.makeText(this, "Anda telah berhasil logout.", Toast.LENGTH_SHORT).show()

            // 2. Arahkan kembali ke Halaman Login (LginActivity)
            val intent = Intent(this, LginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}