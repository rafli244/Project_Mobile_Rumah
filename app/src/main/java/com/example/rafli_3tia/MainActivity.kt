package com.example.rafli_3tia

/*
 * =====================================================================================
 * BOTTOM NAVIGATION, FRAGMENT TRANSACTIONS, & ALERTDIALOG LOGOUT
 * =====================================================================================
 * Materi  : Pertemuan 5, 6, & 7
 * Konsep  :
 *   1. BottomNavigationView: setOnItemSelectedListener { when (it.itemId) { ... } }.
 *   2. Fragment Host: Menampilkan Fragment di FrameLayout/FragmentContainerView.
 *   3. AlertDialog Logout: AlertDialog.Builder(this).setTitle(...).setPositiveButton("Ya") { ... }
 *      -> Bersihkan sesi: getSharedPreferences("USER_SESSION", ...).edit().clear().apply()
 *      -> Kembali ke Login: startActivity(Intent(this, LginActivity::class.java)) + finish().
 * =====================================================================================
 */

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

/*
 * PETA MATERI UJIAN - cari MATERI-01 sampai MATERI-15 dengan Ctrl+Shift+F.
 * Nomor label mengikuti daftar topik belajar, BUKAN nomor pertemuan modul.
 * 01 Dasar Kotlin: LginActivity (val, if-else), MainActivity (var, fun, ?. dan ?:),
 *    PengaduanRepository (List, listOf). val = referensi tidak bisa diassign ulang;
 *    isi objek mutable yang ditunjuk val masih bisa berubah.
 * 02 XML: activity_lgin (LinearLayout, TextView), fragment_pengaduan (EditText),
 *    fragment_buat_laporan (Button), activity_tutorial (ConstraintLayout).
 * 03 Activity/Lifecycle: LginActivity.onCreate, setContentView; MainActivity.onResume.
 * 04 ViewBinding: LginActivity.inflate; PengaduanFragment.bind; app/build.gradle.kts.
 * 05 Klik/Toast/validasi: LginActivity.btnLogin; BuatLaporanFragment.btnKirim.
 * 06 Intent: LginActivity mengirim EXTRA_USERNAME; MainActivity menerima.
 *    DetailActivity.open mengirim PENGADUAN_ID; onCreate menerima key yang sama.
 * 07 SharedPreferences: LginActivity menyimpan; SplashActivity membaca; logout di sini.
 * 08 Splash: SplashActivity, Handler(Looper.getMainLooper()).postDelayed.
 * 09 AlertDialog konfirmasi: fungsi logout di file ini (Batal / Ya, Keluar).
 * 10 ListView sederhana: BerandaFragment ArrayAdapter + fragment_beranda.xml.
 * 11 Custom Adapter: PengaduanAdapter BaseAdapter/getView + Pengaduan data class.
 *    ArrayAdapter sederhana ada di BerandaFragment; bukan custom ArrayAdapter.
 * 12 Fragment: PengaduanFragment.onViewCreated dan binding; Tutorial1Fragment.
 *    onCreateView tidak dioverride: Fragment(R.layout...) meng-inflate layout otomatis.
 * 13 Bottom Navigation: listener, showTab dan display di file ini;
 *    activity_main.xml + menu/bottom_navigation.xml.
 * 14 Toolbar: listener menu/back dan updateChrome di sini; DetailActivity tombol back;
 *    activity_main.xml + menu/main_toolbar_menu.xml.
 * 15 Material: activity_lgin TextInputLayout/MaterialButton; fragment_pengaduan ChipGroup;
 *    header_beranda GridLayout/bg_header_wafe; drawable/bg_header_wafe.xml;
 *    TutorialActivity + TutorialFragmentAdapter + activity_tutorial untuk pager/dots.
 *    MenuProvider BELUM dipakai. Menu aktif memakai toolbar.inflateMenu dan listener.

 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    // MATERI-01: var bisa diassign ulang; Boolean menyimpan kondisi form.
    private var showingForm = false
    // MATERI-01 / MATERI-06: ?. akses aman; ?: nilai cadangan jika null; getStringExtra menerima Intent.
    val username: String
        get() = intent.getStringExtra("EXTRA_USERNAME")?.takeIf { it.isNotBlank() }
            ?: getSharedPreferences("USER_SESSION", MODE_PRIVATE)
                .getString("USERNAME", "Warga")?.takeIf { it.isNotBlank() }
            ?: "Warga"
    private val backForm = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            showTab(binding.bottomNavigation.selectedItemId)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!getSharedPreferences("USER_SESSION", MODE_PRIVATE).getBoolean("isLogin", false)) {
            startActivity(Intent(this, LginActivity::class.java))
            finish()
            return
        }
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.root.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            binding.topInset.layoutParams.height = bars.top
            v.setPadding(bars.left, 0, bars.right, maxOf(bars.bottom, ime.bottom))
            insets
        }
        // The root owns system insets; do not let BottomNavigationView add them a second time.
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { nav, insets ->
            nav.setPadding(0, 0, 0, 0)
            insets
        }
        onBackPressedDispatcher.addCallback(this, backForm)
        // MATERI-13: id menu -> showTab -> FragmentManager; pasangan XML: activity_main + bottom_navigation.
        binding.bottomNavigation.setOnItemSelectedListener {
            showTab(it.itemId)
            true
        }
        binding.fabBuat.setOnClickListener { showForm() }
        // MATERI-14 / MATERI-15: back dan menu toolbar aktif. Ini memakai inflateMenu, bukan MenuProvider.
        binding.toolbarMain.setNavigationOnClickListener { backForm.handleOnBackPressed() }
        binding.toolbarMain.inflateMenu(R.menu.main_toolbar_menu)
        binding.toolbarMain.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_tutorial) {
                startActivity(Intent(this, TutorialActivity::class.java))
                true
            } else false
        }
        if (savedInstanceState == null) navigate(R.id.nav_beranda)
        else {
            showingForm = savedInstanceState.getBoolean("form", false)
            updateChrome(savedInstanceState.getInt("selected", R.id.nav_beranda), showingForm)
        }
        refreshBadge()
    }
    // MATERI-12 / MATERI-13: transaksi Fragment; add pertama kali, hide/show untuk berpindah tab.
    private fun display(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.fragments.forEach { hide(it) }
            val existing = supportFragmentManager.findFragmentByTag(tag)
            if (existing == null) {
                add(R.id.fragmentContainer, fragment, tag)
            } else {
                show(existing)
            }
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
    // MATERI-14: atur judul toolbar dan navigationIcon sesuai tab atau form.
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
        if (id == R.id.nav_buat) {
            showForm()
            return
        }
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
    fun showReports(mine: Boolean = true) {
        navigate(R.id.nav_pengaduan)
        supportFragmentManager.executePendingTransactions()
        (supportFragmentManager.findFragmentByTag(R.id.nav_pengaduan.toString()) as? PengaduanFragment)?.filter(mine)
    }
    fun refreshBadge() {
        if (!::binding.isInitialized) return
        val count = NotifikasiRepository(this).unread(username)
        if (count > 0) binding.bottomNavigation.getOrCreateBadge(R.id.nav_notifikasi).apply {
            number = count
            backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.primary)
        } else binding.bottomNavigation.removeBadge(R.id.nav_notifikasi)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.getBoolean("form", false)) showForm()
    }
    // MATERI-03: onResume dipanggil saat Activity kembali aktif; perbarui badge notifikasi.
    override fun onResume() {
        super.onResume()
        refreshBadge()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        if (::binding.isInitialized) {
            outState.putBoolean("form", showingForm)
            outState.putInt("selected", binding.bottomNavigation.selectedItemId)
        }
        super.onSaveInstanceState(outState)
    }
    // MATERI-07 / MATERI-09: dialog konfirmasi; sesi baru dihapus setelah tombol positif diklik.
    // Salin fungsi logout utuh; USER_SESSION, isLogin, USERNAME harus sama dengan kode login/splash.
    fun logout() {
        AlertDialog.Builder(this).setTitle("Keluar dari SiComplaint?")
            .setMessage("Tersimpan")
            .setNegativeButton("Batal", null).setPositiveButton("Ya, Keluar") { _, _ ->
                getSharedPreferences("USER_SESSION", MODE_PRIVATE).edit().putBoolean("isLogin", false).remove("USERNAME").apply()
                android.widget.Toast.makeText(this, "Anda telah berhasil logout.", android.widget.Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LginActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
                finish()
            }.show()
    }
}
