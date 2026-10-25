package com.example.rafli_3tia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rafli_3tia.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge()
        val b = ActivityDetailBinding.inflate(layoutInflater); setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); v.setPadding(bars.left, bars.top, bars.right, bars.bottom); insets
        }
        b.toolbarDetail.setNavigationOnClickListener { finish() }
        val p = PengaduanRepository(this).cari(intent.getStringExtra("PENGADUAN_ID") ?: "DEMO-1")
        if (p == null) { Toast.makeText(this, "Laporan tidak ditemukan", Toast.LENGTH_SHORT).show(); finish(); return }
        b.tvKategori.text = "Kategori: ${p.kategori}"
        b.tvReportTitle.text = p.judul
        b.tvLocation.text = p.lokasi
        b.tvTanggal.text = "Tanggal Lapor: ${p.tanggal}"
        b.tvReportDesc.text = p.deskripsi
        b.chipStatus.text = statusLabel(p.status); b.chipStatus.setBackgroundResource(statusBackground(p.status))
        b.tvPelapor.text = "Pelapor: ${if (p.anonim) "Anonim" else AkunRepository(this).nama(p.pelapor)}"
        b.tvVerifikasi.text = when (p.status) {
            "Diproses", "Selesai" -> "${if (p.id.startsWith("DEMO")) "Contoh • " else ""}Terverifikasi • Prioritas ${p.prioritas}"
            else -> "Menunggu verifikasi • Prioritas ${p.prioritas}"
        }
        b.tvVerifikasi.setTextColor(getColor(if (p.status in listOf("Diproses", "Selesai")) R.color.status_selesai else R.color.gray_text))
        if (!PhotoStore.show(b.ivDetailImage, p.fotoPath)) {
            b.ivDetailImage.setImageResource(if (p.id == "DEMO-1") R.drawable.laporan_jalan else R.drawable.ic_sicomplaint_logo)
            b.ivDetailImage.scaleType = if (p.id == "DEMO-1") ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
        }
        b.btnLacak.setOnClickListener { LacakStatusActivity.open(this, p.id) }
        b.btnBackToMain.setOnClickListener { finish() }
    }
    companion object {
        fun open(context: Context, id: String) { context.startActivity(Intent(context, DetailActivity::class.java).putExtra("PENGADUAN_ID", id)) }
    }
}
