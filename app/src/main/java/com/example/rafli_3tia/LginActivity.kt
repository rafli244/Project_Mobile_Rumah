package com.example.rafli_3tia

/*
 * =====================================================================================
 * FORM LOGIN, CARDVIEW, TOAST, & SIMPAN SHARED PREFERENCES
 * =====================================================================================
 * Materi  : Pertemuan 2, 3, & 6
 * Konsep  :
 *   1. ViewBinding: ActivityLginBinding.inflate(layoutInflater) -> setContentView(binding.root).
 *   2. Validasi input: Cek editText.text.toString().trim() tidak boleh kosong.
 *   3. Toast feedback: Toast.makeText(this, "Pesan...", Toast.LENGTH_SHORT).show().
 *   4. Simpan Sesi: getSharedPreferences("USER_SESSION", MODE_PRIVATE).edit().putBoolean("isLogin", true).
 *   5. Pindah Halaman: startActivity(Intent(this, MainActivity::class.java)) + finish().
 * =====================================================================================
 */

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
import com.example.rafli_3tia.databinding.ActivityLginBinding

class LginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (e: Exception) {
            Log.e("LginActivity", "EdgeToEdge error", e)
        }

        // Inisialisasi ViewBinding
        binding = ActivityLginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: Exception) {
            Log.e("LginActivity", "Insets error", e)
        }

        val remembered = getSharedPreferences("LOGIN_PREFERENCES", MODE_PRIVATE)
        binding.checkBox.isChecked = remembered.getBoolean("remember", false)
        if (binding.checkBox.isChecked) {
            binding.editTextText.setText(remembered.getString("username", ""))
        }

        binding.tvLupaPassword.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Bantuan Akun Demo")
                .setMessage("Silakan gunakan akun demo:\n• Username: rafli | Password: 12345\n• Username: admin | Password: admin123")
                .setPositiveButton("Tutup", null)
                .show()
        }

        binding.tvDaftar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Panduan Akun")
                .setMessage("Sesuai aturan praktikum:\nAnda dapat langsung login dengan password yang sama persis dengan username (misal: user / user),\natau gunakan akun demo: rafli / 12345.")
                .setPositiveButton("Mengerti", null)
                .show()
        }

        binding.btnLogin.setOnClickListener {
            val usernameInput = binding.editTextText.text.toString().trim()
            val passwordInput = binding.editTextTextPassword.text.toString().trim()

            // Reset pesan error sebelumnya
            binding.tilUsername.error = null
            binding.tilPassword.error = null

            // 1. Validasi Input Tidak Boleh Kosong
            if (usernameInput.isEmpty()) {
                binding.tilUsername.error = "Username tidak boleh kosong!"
                binding.editTextText.requestFocus()
                Toast.makeText(this, "Silakan masukkan username!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordInput.isEmpty()) {
                binding.tilPassword.error = "Kata sandi tidak boleh kosong!"
                binding.editTextTextPassword.requestFocus()
                Toast.makeText(this, "Silakan masukkan kata sandi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Logika Verifikasi Kredensial (Akun Demo + Aturan Modul: username == password)
            val isRafliValid = usernameInput.equals("rafli", ignoreCase = true) && passwordInput == "12345"
            val isAdminValid = usernameInput.equals("admin", ignoreCase = true) && passwordInput == "admin123"
            val isMatching = usernameInput == passwordInput

            if (isRafliValid || isAdminValid || isMatching) {
                remembered.edit().putBoolean("remember", binding.checkBox.isChecked)
                    .putString("username", if (binding.checkBox.isChecked) usernameInput else "").apply()

                // 3. Simpan Sesi ke SharedPreferences
                val sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putBoolean("isLogin", true)
                    putString("USERNAME", usernameInput)
                    apply()
                }

                Toast.makeText(this, "Login Berhasil! Halo, $usernameInput", Toast.LENGTH_SHORT).show()

                // 4. Intent Berpindah dari Login ke MainActivity
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("EXTRA_USERNAME", usernameInput)
                }
                startActivity(intent)
                finish() // Tutup LginActivity agar tidak bisa di-back
            } else {
                // Kredensial Salah
                binding.tilPassword.error = "Password tidak cocok"
                Toast.makeText(
                    this,
                    "Login Gagal! Username atau password salah.\n(Coba: rafli / 12345)",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
