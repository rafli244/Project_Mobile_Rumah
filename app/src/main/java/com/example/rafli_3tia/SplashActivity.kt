package com.example.rafli_3tia

/*
 * PANDUAN PINDAH KE PROJECT BARU | MATERI 03,04,06,07,08
 * TUJUAN: layar pembuka dua detik, cek sesi, lalu pilih tujuan.
 * 1. Buat SplashActivity dan activity_splash.xml; sesuaikan import ActivitySplashBinding.
 * 2. Salin binding + onCreate; ambil splashDuration dan blok Handler sampai }, splashDuration).
 * 3. Import Handler dan Looper dari android.os, Intent dari android.content.
 * 4. Jika tanpa login, callback cukup menuju Activity tujuan; jika dengan login samakan USER_SESSION.
 * 5. Daftarkan Splash sebagai satu-satunya MAIN/LAUNCHER (exported=true) di Manifest.
 * 6. Pertahankan finish setelah startActivity agar Back tidak kembali ke splash.
 * UJI: buka dari kondisi tertutup, tunggu dua detik; sesudah logout harus menuju Login.
 */

/*
 * =====================================================================================
 * SPLASH SCREEN & AUTO-LOGIN (SHARED PREFERENCES)
 * =====================================================================================
 * Materi  : Pertemuan 4 & 6
 * Konsep  :
 *   1. Handler/Coroutine delay 2000ms (2 detik).
 *   2. Membaca "isLogin" dari SharedPreferences "USER_SESSION".
 *   3. Jika isLogin == true -> Langsung ke MainActivity.
 *   4. Jika isLogin == false -> Masuk ke LginActivity.
 *   5. finish() agar pengguna tidak bisa kembali ke Splash saat menekan tombol Back.
 * =====================================================================================
 */

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
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).apply { isAppearanceLightStatusBars = false; isAppearanceLightNavigationBars = false }

        try {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Insets error", e)
        }

        // MATERI-08 MULAI: Handler main thread menjadwalkan blok setelah splashDuration (2000 ms).
        // Ini penjadwalan, bukan Thread.sleep; salin sampai }, splashDuration).
        // PETUNJUK: SALIN SATU BLOK UTUH sampai }, splashDuration); properti durasi dan import Handler/Looper ikut dibutuhkan.
        Handler(Looper.getMainLooper()).postDelayed({
            // 1. Ambil data sesi dari SharedPreferences
            // MATERI-07: baca isLogin; default false ketika sesi belum tersimpan.
            val sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
            val isLogin = sharedPreferences.getBoolean("isLogin", false)
            val savedUsername = sharedPreferences.getString("USERNAME", "Rafli")

            // 2. Tentukan tujuan intent berdasarkan status isLogin
            // MATERI-01 / MATERI-06: if-else menentukan Activity tujuan; finish menutup Splash.
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
