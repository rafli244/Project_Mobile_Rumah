package com.example.rafli_3tia

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.rafli_3tia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var showingForm = false
    val username: String get() = getSharedPreferences("USER_SESSION", MODE_PRIVATE).getString("USERNAME", "Warga") ?: "Warga"
    val account: AkunRepository get() = AkunRepository(this)
    private val backForm = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() { showTab(binding.bottomNavigation.selectedItemId) }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!getSharedPreferences("USER_SESSION", MODE_PRIVATE).getBoolean("isLogin", false)) {
            startActivity(Intent(this, LginActivity::class.java)); finish(); return
        }
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.root.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            binding.topInset.layoutParams.height = bars.top
            v.setPadding(bars.left, 0, bars.right, maxOf(bars.bottom, ime.bottom)); insets
        }
        // The root owns system insets; do not let BottomNavigationView add them a second time.
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { nav, insets -> nav.setPadding(0, 0, 0, 0); insets }
        onBackPressedDispatcher.addCallback(this, backForm)
        binding.bottomNavigation.setOnItemSelectedListener { showTab(it.itemId); true }
        binding.fabBuat.setOnClickListener { showForm() }
        binding.toolbarMain.setNavigationOnClickListener { backForm.handleOnBackPressed() }
        if (savedInstanceState == null) navigate(R.id.nav_beranda)
        else {
            showingForm = savedInstanceState.getBoolean("form", false)
            updateChrome(savedInstanceState.getInt("selected", R.id.nav_beranda), showingForm)
        }
        refreshBadge()
    }
    private fun display(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.fragments.forEach { hide(it) }
            val existing = supportFragmentManager.findFragmentByTag(tag)
            if (existing == null) add(R.id.fragmentContainer, fragment, tag) else show(existing)
        }.commit()
    }
    private fun showTab(id: Int) {
        showingForm = false
        display(when (id) {
            R.id.nav_pengaduan -> PengaduanFragment()
            R.id.nav_notifikasi -> NotifikasiFragment()
            R.id.nav_profil -> ProfilFragment()
            else -> BerandaFragment()
        }, id.toString())
        updateChrome(id, false)
        refreshBadge()
    }
    private fun updateChrome(id: Int, form: Boolean) {
        backForm.isEnabled = form
        binding.bottomNavigation.visibility = if (form) View.GONE else View.VISIBLE
        binding.fabBuat.visibility = if (!form && id in listOf(R.id.nav_beranda, R.id.nav_pengaduan)) View.VISIBLE else View.GONE
        binding.toolbarMain.visibility = if (form || id in listOf(R.id.nav_pengaduan, R.id.nav_notifikasi)) View.VISIBLE else View.GONE
        binding.toolbarMain.title = if (form) "Buat Laporan" else binding.bottomNavigation.menu.findItem(id)?.title
        binding.toolbarMain.navigationIcon = if (form) ContextCompat.getDrawable(this, R.drawable.ic_figma_back) else null
        binding.toolbarMain.setNavigationIconTint(ContextCompat.getColor(this, R.color.white))
        val redHeader = form || id in listOf(R.id.nav_beranda, R.id.nav_profil, R.id.nav_notifikasi, R.id.nav_pengaduan)
        binding.topInset.setBackgroundColor(ContextCompat.getColor(this, if (redHeader) R.color.primary else R.color.background_app))
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !redHeader
    }
    fun navigate(id: Int) {
        if (id == R.id.nav_buat) { showForm(); return }
        val actual = if (id == R.id.nav_riwayat) R.id.nav_pengaduan else id
        if (binding.bottomNavigation.selectedItemId == actual) showTab(actual)
        else binding.bottomNavigation.selectedItemId = actual
    }
    fun showForm(category: String? = null) {
        showingForm = true
        display(BuatLaporanFragment(), "form")
        if (category != null) {
            supportFragmentManager.executePendingTransactions()
            (supportFragmentManager.findFragmentByTag("form") as? BuatLaporanFragment)?.selectCategory(category)
        }
        updateChrome(binding.bottomNavigation.selectedItemId, true)
    }
    fun showReports(mine: Boolean = true, query: String = "") {
        navigate(R.id.nav_pengaduan)
        supportFragmentManager.executePendingTransactions()
        (supportFragmentManager.findFragmentByTag(R.id.nav_pengaduan.toString()) as? PengaduanFragment)?.filter(mine, query)
    }
    fun refreshBadge() {
        if (!::binding.isInitialized) return
        val count = NotifikasiRepository(this).unread(username)
        if (count > 0) binding.bottomNavigation.getOrCreateBadge(R.id.nav_notifikasi).apply {
            number = count; backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.primary)
        } else binding.bottomNavigation.removeBadge(R.id.nav_notifikasi)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.getBoolean("form", false)) showForm()
    }
    override fun onResume() { super.onResume(); refreshBadge() }
    override fun onSaveInstanceState(outState: Bundle) {
        if (::binding.isInitialized) {
            outState.putBoolean("form", showingForm)
            outState.putInt("selected", binding.bottomNavigation.selectedItemId)
        }
        super.onSaveInstanceState(outState)
    }
    fun logout() {
        AlertDialog.Builder(this).setTitle("Keluar dari SiComplaint?")
            .setMessage("Laporan Anda tetap tersimpan di perangkat ini.")
            .setNegativeButton("Batal", null).setPositiveButton("Ya, Keluar") { _, _ ->
                getSharedPreferences("USER_SESSION", MODE_PRIVATE).edit().putBoolean("isLogin", false).remove("USERNAME").apply()
                android.widget.Toast.makeText(this, "Anda telah berhasil logout.", android.widget.Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LginActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
                finish()
            }.show()
    }
}
