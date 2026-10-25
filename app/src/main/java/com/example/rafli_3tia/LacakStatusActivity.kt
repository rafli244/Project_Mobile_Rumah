package com.example.rafli_3tia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rafli_3tia.databinding.ActivityLacakStatusBinding

class LacakStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge()
        val b = ActivityLacakStatusBinding.inflate(layoutInflater); setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); v.setPadding(bars.left, bars.top, bars.right, bars.bottom); insets
        }
        b.toolbarLacak.setNavigationOnClickListener { finish() }
        val p = PengaduanRepository(this).cari(intent.getStringExtra("PENGADUAN_ID") ?: "")
        if (p == null) { Toast.makeText(this, "Laporan tidak ditemukan", Toast.LENGTH_SHORT).show(); finish(); return }
        b.tvReportId.text = "#SCP-${p.id.take(12).uppercase(java.util.Locale.ROOT)}"
        b.tvStatus.text = statusLabel(p.status)
        b.tvStatus.setTextColor(getColor(R.color.white))
        b.tvStatus.setBackgroundResource(statusBackground(p.status))
        val completed = when (p.status) { "Selesai" -> 4; "Diproses", "Dalam Proses" -> 3; else -> 1 }
        val circles = listOf(b.stepCircle0, b.stepCircle1, b.stepCircle2, b.stepCircle3)
        val titles = listOf(b.stepTitle0, b.stepTitle1, b.stepTitle2, b.stepTitle3)
        val dates = listOf(b.stepDate0, b.stepDate1, b.stepDate2, b.stepDate3)
        val descriptions = listOf(b.stepDesc0, b.stepDesc1, b.stepDesc2, b.stepDesc3)
        val lines = listOf(b.stepLine0, b.stepLine1, b.stepLine2)
        val desc = listOf("Laporan berhasil diterima di perangkat ini.", "Laporan telah diverifikasi.", "Laporan sedang ditangani.", "Penanganan laporan telah selesai.")
        circles.forEachIndexed { i, circle ->
            val done = i < completed
            circle.setBackgroundResource(if (done) R.drawable.bg_circle_red else R.drawable.bg_circle_gray)
            circle.text = if (done) "✓" else (i+1).toString()
            titles[i].setTextColor(getColor(if (i == completed-1) R.color.primary else R.color.text_dark))
            dates[i].text = if (i == 0) p.tanggal else if (done) "${if (p.id.startsWith("DEMO")) "Data contoh" else "Status laporan"}" else "Menunggu tahap ini"
            descriptions[i].text = if (done) desc[i] else "Belum ada pembaruan."
            if (i < 3) lines[i].setBackgroundColor(getColor(if (i < completed-1) R.color.primary else R.color.gray_border))
        }
        b.btnDetail.setOnClickListener { finish() }
    }
    companion object {
        fun open(context: Context, id: String) { context.startActivity(Intent(context, LacakStatusActivity::class.java).putExtra("PENGADUAN_ID", id)) }
    }
}
