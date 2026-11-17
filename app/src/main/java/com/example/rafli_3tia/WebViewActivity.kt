package com.example.rafli_3tia

/*
 * PANDUAN PINDAH KE PROJECT BARU | MATERI WEBVIEW (PERTEMUAN 5)
 * TUJUAN: Menampilkan konten website terintegrasi langsung di aplikasi (in-app).
 * 1. Tambahkan izin internet di AndroidManifest.xml:
 *    <uses-permission android:name="android.permission.INTERNET" />
 * 2. Tambahkan Activity ini di AndroidManifest.xml.
 * 3. Gunakan WebViewClient agar tautan tidak dialihkan ke browser eksternal.
 * 4. Aktifkan settings.javaScriptEnabled = true agar website interaktif dapat berjalan.
 * 5. Gunakan WebChromeClient untuk update indikator progress loading.
 * 6. Tangani tombol back: jika webView.canGoBack(), panggil webView.goBack().
 */

/*
 * =====================================================================================
 * KOMPONEN WEBVIEW, JAVASCRIPT INTEGRATION & IZIN INTERNET
 * =====================================================================================
 * Materi  : Pertemuan 5 Praktikum Mobile Programming
 * Konsep  :
 *   1. android.webkit.WebView & WebViewClient:
 *      Agar website terbuka langsung di dalam aplikasi (in-app), bukan dilempar ke browser luar.
 *   2. WebChromeClient:
 *      Memantau progres loading (0-100%) dan mengupdate ProgressBar secara real-time.
 *   3. settings.javaScriptEnabled = true:
 *      Mengaktifkan fungsionalitas JavaScript pada halaman web.
 *   4. onBackPressedDispatcher:
 *      Jika webView.canGoBack(), kembali ke riwayat web sebelumnya sebelum menutup Activity.
 *   5. AndroidManifest.xml:
 *      Wajib menambahkan <uses-permission android:name="android.permission.INTERNET" />.
 * =====================================================================================
 */

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rafli_3tia.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    companion object {
        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val DEFAULT_URL = "https://lapor.go.id/"
        const val DEFAULT_TITLE = "SP4N-LAPOR!"

        fun open(context: Context, url: String = DEFAULT_URL, title: String = DEFAULT_TITLE) {
            val intent = Intent(context, WebViewActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_TITLE, title)
            }
            context.startActivity(intent)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val targetUrl = intent.getStringExtra(EXTRA_URL)?.takeIf { it.isNotBlank() } ?: DEFAULT_URL
        val pageTitle = intent.getStringExtra(EXTRA_TITLE)?.takeIf { it.isNotBlank() } ?: DEFAULT_TITLE

        // Setup Toolbar
        binding.toolbarWeb.title = pageTitle
        binding.toolbarWeb.subtitle = targetUrl
        binding.toolbarWeb.setNavigationOnClickListener {
            handleBackAction()
        }

        // Setup WebView Settings
        with(binding.webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        // WebViewClient: Agar navigasi halaman web tetap di dalam aplikasi
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBarWeb.visibility = View.VISIBLE
                binding.toolbarWeb.subtitle = url ?: targetUrl
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBarWeb.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }
        }

        // WebChromeClient: Memantau proses loading & judul web
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progressBarWeb.progress = newProgress
                if (newProgress >= 100) {
                    binding.progressBarWeb.visibility = View.GONE
                } else {
                    binding.progressBarWeb.visibility = View.VISIBLE
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (!title.isNullOrBlank()) {
                    binding.toolbarWeb.title = title
                }
            }
        }

        // Muat URL website
        binding.webView.loadUrl(targetUrl)

        // Tangani navigasi back hardware/gesture
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackAction()
            }
        })
    }

    private fun handleBackAction() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        binding.webView.stopLoading()
        binding.webView.destroy()
        super.onDestroy()
    }
}
