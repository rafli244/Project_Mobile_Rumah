package com.example.rafli_3tia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
        if (binding.checkBox.isChecked) binding.editTextText.setText(remembered.getString("username", ""))
        binding.tvLupaPassword.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Bantuan akun demo")
                .setMessage("Akun demo: rafli / 12345 atau admin / admin123.").setPositiveButton("Tutup", null).show()
        }
        binding.tvDaftar.setOnClickListener {
            val form = com.example.rafli_3tia.databinding.DialogRegisterBinding.inflate(layoutInflater)
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Daftar Akun")
                .setMessage("Buat akun lokal untuk melaporkan masalah di sekitar Anda.")
                .setView(form.root).setNegativeButton("Batal", null).setPositiveButton("Daftar", null).create()
            dialog.setOnShowListener {
                dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val user = form.etUsernameDaftar.text.toString().trim()
                    val error = AkunRepository(this).daftar(form.etNamaDaftar.text.toString(), user, form.etPasswordDaftar.text.toString())
                    if (error != null) form.etUsernameDaftar.error = error
                    else {
                        binding.editTextText.setText(user)
                        binding.editTextTextPassword.text?.clear()
                        Toast.makeText(this, "Akun berhasil dibuat. Silakan masuk.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            dialog.show()
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

            // 2. Logika Verifikasi Kredensial
            val isRafliValid = usernameInput.equals("rafli", ignoreCase = true) && passwordInput == "12345"
            val isAdminValid = usernameInput.equals("admin", ignoreCase = true) && passwordInput == "admin123"

            if (isRafliValid || isAdminValid || AkunRepository(this).valid(usernameInput, passwordInput)) {
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
