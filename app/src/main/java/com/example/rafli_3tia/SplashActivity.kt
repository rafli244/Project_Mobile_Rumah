package com.example.rafli_3tia

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rafli_3tia.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    // Durasi tampil Splash Screen (2 detik)
    private val splashDuration = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (e: Exception) {
            Log.e("SplashActivity", "EdgeToEdge error", e)
        }

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Insets error", e)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            // 1. Ambil data sesi dari SharedPreferences
            val sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
            val isLogin = sharedPreferences.getBoolean("isLogin", false)
            val savedUsername = sharedPreferences.getString("USERNAME", "Rafli")

            // 2. Tentukan tujuan intent berdasarkan status isLogin
            if (isLogin) {
                // Sudah login -> Langsung ke MainActivity
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("EXTRA_USERNAME", savedUsername)
                }
                startActivity(intent)
            } else {
                // Belum login -> Arahkan ke Halaman Login
                val intent = Intent(this, LginActivity::class.java)
                startActivity(intent)
            }

            // Tutup SplashActivity agar tidak bisa kembali dengan tombol back
            finish()
        }, splashDuration)
    }
}
