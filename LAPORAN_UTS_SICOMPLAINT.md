> **Pembaruan final 25 Oktober 2026:** navigasi kini empat menu dan FAB sesuai Figma. Dokumen di bawah adalah laporan tahap sebelumnya. Lihat [Audit Figma](AUDIT_SINKRONISASI_FIGMA.md) dan [Hasil Pengujian Final](HASIL_PENGUJIAN_FIGMA.md) untuk implementasi terbaru.

# Laporan UTS Mobile Programming — SiComplaint

Nama project lokal: `RafliHanafi_3TIA`. Nama Gradle asli `Rafli_3TIA` tetap dipertahankan. Aplikasi SiComplaint, Kotlin, XML Views, package/applicationId `com.example.rafli_3tia`, ViewBinding, dan Material Components tetap digunakan.

## Analisis project asli

- `SplashActivity`: splash dua detik dengan logo putih, ProgressBar, dan pembacaan `USER_SESSION` untuk menentukan tujuan login/beranda. Tetap dipertahankan.
- `LginActivity`: validasi input, akun demo rafli/12345 dan admin/admin123, penyimpanan `isLogin` dan `USERNAME`, serta Intent ke MainActivity. Nama activity tetap sama.
- `MainActivity`: sebelumnya berisi kartu sambutan, tautan detail contoh, Toolbar, dan dialog logout; sekarang menjadi host lima Fragment. Sambutan dan tautan detail contoh dipindah ke Beranda, logout ke Profil.
- `DetailActivity`: kartu detail sebelumnya statis; sekarang membaca ID dari Intent lalu mengambil data sebenarnya dari repository. Desain kartu, gambar awal, tombol kembali, dan Toolbar dipertahankan.
- Seluruh empat layout awal, manifest, kedua Gradle build script, version catalog, theme siang/malam, warna, strings, dan drawable diperiksa.
- `design_01`: logo lokasi/percakapan/centang, urgensi merah, kejelasan putih.
- `design_02`: konsep pengaduan masyarakat dan pelacakan laporan.
- `design_03`: merah #E53935, background #FAF7F6, font Inter, radius kartu 16dp, warna status semantik. Aset/font asli dipertahankan.
- `design_04`: splash, login, dashboard, formulir, detail, riwayat/status, dan profil menjadi acuan pembagian halaman.
- `design_05`: tombol 56dp, input, kartu, status, dan navigasi menjadi acuan komponen tambahan.
- `design_06`: laporan baru berstatus Menunggu. Versi UTS menyimpan data lokal; verifikasi dinas dan perubahan status otomatis tidak diklaim tersedia.

## File yang dibuat

Semua Kotlin utama berada di `app/src/main/java/com/example/rafli_3tia/`:

| File | Fungsi |
|---|---|
| `Pengaduan.kt` | Model data laporan, termasuk ID, enam informasi utama, deskripsi, pemilik, prioritas, dan anonim |
| `PengaduanRepository.kt` | Enam laporan contoh, enam kategori, pencarian ID, riwayat pemilik, serialisasi JSON ke SharedPreferences |
| `PengaduanAdapter.kt` | BaseAdapter khusus, ViewHolder, binding baris, warna status, tombol Detail |
| `BerandaFragment.kt` | Ringkasan dinamis, daftar kategori ArrayAdapter, Toast, akses formulir/detail contoh |
| `PengaduanFragment.kt` | ListView pengaduan masyarakat dan empty state |
| `BuatLaporanFragment.kt` | Formulir, validasi, penyimpanan lokal, dialog konfirmasi |
| `RiwayatFragment.kt` | Memakai ulang tampilan daftar dengan filter pengguna aktif |
| `ProfilFragment.kt` | Nama pengguna, statistik pribadi, tautan riwayat, logout |

Resource baru pada `app/src/main/res/`:

- `layout/fragment_beranda.xml`, `layout/header_beranda.xml`.
- `layout/fragment_pengaduan.xml`, `layout/item_pengaduan.xml`.
- `layout/fragment_buat_laporan.xml`, `layout/fragment_profil.xml`.
- `menu/bottom_navigation.xml`, `color/nav_item_color.xml`.
- Lima vector `drawable/ic_nav_beranda.xml`, `ic_nav_pengaduan.xml`, `ic_nav_buat.xml`, `ic_nav_riwayat.xml`, `ic_nav_profil.xml`.
- `drawable/ic_sicomplaint_toolbar.xml`: varian 32dp dari logo putih asli agar logo Toolbar tidak terpotong; aset 96dp untuk splash tetap utuh.
- `app/src/androidTest/java/com/example/rafli_3tia/SiComplaintFlowTest.kt`: pengujian alur aplikasi pada emulator.
- Dokumen laporan ini dan `HASIL_PENGUJIAN.md`.

## File yang diubah

- `MainActivity.kt`: host Fragment, BottomNavigationView, pemeriksaan sesi, navigasi, dialog logout.
- `LginActivity.kt`: checkbox kini benar-benar mengingat username; bantuan akun demo pada tautan lupa sandi/daftar. Password tidak disimpan. Perilaku sesi login lama tetap berlaku.
- `DetailActivity.kt`: pengisian detail melalui ID laporan pada Intent, pemilik/anonim dan prioritas dinamis, skala ilustrasi logo FIT_CENTER.
- `layout/activity_main.xml`: Toolbar, FragmentContainerView, dan BottomNavigationView.
- `layout/activity_lgin.xml`: label checkbox menjadi Ingat username; autofill sistem dimatikan pada formulir akun demo agar tidak mengganggu interaksi.
- `layout/activity_detail.xml`: ID untuk teks pemilik dan status sehingga bisa diisi dinamis.
- `app/build.gradle.kts`: deklarasi dependency langsung `androidx.fragment:fragment:1.5.4`, versi yang sebelumnya telah dibawa AppCompat secara transitif.

`SplashActivity.kt`, `activity_splash.xml`, manifest, package, nama aplikasi, aset desain, font, serta konfigurasi SDK asli tidak diganti. Backup file yang ditimpa tersedia pada folder `backup_uts_20261025_005823`.

## Bottom Navigation

Menu selalu terlihat di bawah host Fragment: Beranda, Pengaduan, Buat Laporan, Riwayat, Profil. `setOnItemSelectedListener` menentukan Fragment berdasarkan ID menu. Fragment diberi tag ID lalu ditampilkan/disembunyikan menggunakan FragmentManager. Pemilihan menu mengubah indikator serta subtitle Toolbar.

Fragment yang sudah dibuat dipakai kembali agar isian formulir tidak hilang saat pindah tab. FragmentManager dan state view Android membantu pemulihan saat activity dibuat ulang. Ringkasan, daftar, dan profil dimuat ulang ketika tab ditampilkan agar laporan baru langsung muncul. ListView memiliki area scroll sendiri; tidak ditempatkan di dalam ScrollView.

## ArrayAdapter dan Custom Adapter

| ArrayAdapter sederhana | Custom Adapter |
|---|---|
| `ArrayAdapter<String>` pada Beranda | `PengaduanAdapter : BaseAdapter` pada Pengaduan/Riwayat |
| `android.R.layout.simple_list_item_1` | `item_pengaduan.xml` |
| Satu string kategori per baris | Ikon, judul, kategori, lokasi, tanggal, status, Detail |
| `setOnItemClickListener` menampilkan Toast | Klik baris atau tombol membuka DetailActivity |

Kategori: Jalan Rusak, Sampah Menumpuk, Lampu Jalan Mati, Drainase Tersumbat, Fasilitas Umum Rusak, Gangguan Ketertiban. Dashboard merupakan header ListView; posisi kategori dikurangi jumlah header sebelum mengakses list kategori.

## Cara kerja ViewHolder

`getView()` menerima `convertView`. Jika null, adapter meng-inflate binding `ItemPengaduanBinding`, membuat ViewHolder, lalu menyimpannya dalam `row.tag`. Ketika baris digunakan ulang saat scrolling, ViewHolder diambil dari tag sehingga view tidak perlu dicari/inflate berulang. Semua teks, warna status, deskripsi ikon, dan listener Detail diikat ulang ke laporan pada posisi saat ini untuk menghindari data baris tertukar.

## Formulir dan penyimpanan

Judul, lokasi, dan deskripsi wajib diisi setelah trim. Spinner harus memilih kategori selain placeholder. Checkbox menyatakan kebenaran laporan. RadioGroup memilih prioritas Normal/Mendesak; Switch menyembunyikan nama di detail tanpa menghapus pemilik asli. Tombol Kirim membuat ID UUID, tanggal lokal perangkat, dan status Menunggu.

Repository menyimpan JSONArray di SharedPreferences `SICOMPLAINT_REPORTS`, key `reports`. Enam contoh publik disediakan oleh kode dan digabung dengan data lokal. Riwayat memfilter `pelapor` menggunakan username tanpa membedakan huruf besar/kecil. Commit yang gagal tidak mengosongkan formulir; pengguna mendapat pesan gagal. Setelah berhasil, formulir direset dan dialog menawarkan membuka riwayat.

Data bertahan saat logout dan activity dibuat ulang. Menghapus data aplikasi/uninstall akan menghapus penyimpanan lokal. Ini prototype akun demo, bukan autentikasi server. Opsi anonim menyembunyikan nama di layar detail; tidak berarti menghapus identitas dari penyimpanan.

## Komponen Android dan fungsi nyata

| Komponen | Pemakaian |
|---|---|
| TextView, ImageView | Label, ringkasan, status, logo, ikon, ilustrasi detail |
| EditText, Button | Login, formulir, detail, logout |
| Toast | Validasi login, kategori dipilih, kegagalan simpan, logout |
| Intent | Splash/login/main dan ID laporan ke DetailActivity |
| AlertDialog | Konfirmasi pengiriman berhasil, bantuan akun demo, konfirmasi logout |
| Toolbar | Identitas aplikasi, nama tab aktif, tombol kembali detail |
| SharedPreferences | Sesi, ingat username, JSON laporan lokal |
| Fragment, BottomNavigationView | Lima tujuan navigasi |
| ListView, ArrayAdapter | Daftar sederhana enam kategori |
| ListView, Custom Adapter | Daftar laporan masyarakat dan riwayat |
| Spinner | Pilihan kategori formulir |
| RadioGroup | Pilihan prioritas laporan |
| CheckBox | Ingat username dan pernyataan kebenaran laporan |
| SwitchCompat | Sembunyikan nama pelapor |
| ProgressBar | Indikator pada splash asli |
| ScrollView | Formulir, profil, detail agar konten bisa digulir |
| CardView | Dashboard, profil, baris laporan, login, detail |
| ViewBinding | Akses view bertipe pada activity, fragment, header, adapter |

## Alur aplikasi dan urutan demonstrasi

1. Buka aplikasi: splash tampil dua detik, lalu login atau beranda sesuai session.
2. Login dengan `rafli / 12345` atau `admin / admin123`.
3. Beranda menampilkan ringkasan; klik kategori untuk memperlihatkan Toast ArrayAdapter sederhana.
4. Pengaduan menampilkan laporan contoh/lokal dengan tiga status: Menunggu, Diproses, Selesai.
5. Klik baris/Detail untuk membuka isi laporan yang dipilih, lalu kembali.
6. Buat Laporan: peragakan validasi kosong, pilih kategori, isi data, prioritas, anonim, dan konfirmasi.
7. Kirim; buka Riwayat untuk melihat laporan baru berstatus Menunggu.
8. Profil menampilkan username dan statistik; coba batal logout lalu konfirmasi logout.
9. Login kembali untuk memperlihatkan laporan tetap tersimpan.

## Sepuluh pertanyaan UTS dan jawaban

1. **Bagaimana BottomNavigationView memindahkan halaman?** Listener menerima ID menu dan memilih Fragment. FragmentManager menambah atau menampilkan Fragment dengan tag ID, lalu menyembunyikan yang lain.
2. **Mengapa menggunakan Fragment?** Lima halaman bisa berbagi satu MainActivity, Toolbar, dan navigasi bawah tanpa membuat lima activity baru.
3. **Di mana Simple List Item diterapkan?** Pada `BerandaFragment`, `ArrayAdapter<String>` memakai `android.R.layout.simple_list_item_1` dan enam kategori.
4. **Apa fungsi setOnItemClickListener?** Mengambil posisi kategori untuk Toast atau posisi laporan untuk membuka DetailActivity. Pada kategori, posisi dikurangi header ListView.
5. **Mengapa membutuhkan Custom Adapter?** Satu laporan memiliki banyak properti dan tombol sehingga layout satu TextView bawaan tidak cukup; `PengaduanAdapter` mengikat model ke `item_pengaduan.xml`.
6. **Apa manfaat ViewHolder?** Menyimpan binding baris di tag untuk memakai ulang view saat scrolling, mengurangi inflate dan pencarian view. Data dan listener tetap diperbarui pada setiap getView.
7. **Bagaimana mengirim data ke DetailActivity?** Intent membawa extra `PENGADUAN_ID`. Activity mencari laporan melalui repository dan mengisi kartu; ID tak ditemukan menghasilkan Toast lalu kembali.
8. **Bagaimana laporan bertahan setelah aplikasi ditutup?** Repository mengubah data menjadi JSON lalu menyimpannya ke SharedPreferences. Objek repository baru membaca key yang sama. Data bukan hanya list di memori.
9. **Bagaimana formulir divalidasi?** Trim input, cek tiga field wajib, cek kategori, lalu checkbox kebenaran. Data hanya disimpan setelah semua syarat terpenuhi; status awal selalu Menunggu.
10. **Bagaimana sesi dan logout bekerja?** Login menyimpan `isLogin` dan `USERNAME` di `USER_SESSION`. Splash memeriksanya. Logout menghapus sesi dan membersihkan back stack melalui flags Intent; laporan berada di preferences terpisah sehingga tidak ikut terhapus.

## Hasil pengujian

Lihat `HASIL_PENGUJIAN.md` untuk hasil aktual, lingkungan emulator, perintah build, dan batas pengujian. Build debug pertama berhasil; hasil akhir setelah perbaikan dicatat terpisah agar tidak mencampur tahap awal dengan verifikasi final.
