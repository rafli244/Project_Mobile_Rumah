# Audit dan sinkronisasi Figma — SiComplaint

Dokumen ini menggantikan bagian desain/navigasi pada laporan UTS sebelumnya. Instruksi final memakai **empat menu: Beranda, Laporan Saya, Notifikasi, Profil**, dengan **FAB plus merah** untuk Buat Laporan. Nama aplikasi, package `com.example.rafli_3tia`, Kotlin, XML Views, ViewBinding, dan project lokal tetap dipertahankan.

## Sumber desain dan logo

Sumber utama adalah enam PNG yang disediakan pengguna pada drive D:, disalin tanpa perubahan ke `docs/figma-assets/`:

| File sumber | Isi |
|---|---|
| `Image - 02 - Proses Bisnis SiComplaint.png` | Filosofi brand dan anatomi logo |
| `Sheet.png` | Konsep dan fitur aplikasi |
| `Sheet2.png` | Brand Identity, warna, tipografi, ikon, radius |
| `Sheet3.png` | Delapan layar aplikasi |
| `Sheet4.png` | UI Component Kit, empat tab dan FAB |
| `Image - 02 - Proses Bisnis SiComplaint2.png` | Alur bisnis pelaporan dan tindak lanjut |

`D:\Logo.svg` ditemukan dan digunakan langsung. Tiga path sumber dikonversi menjadi `res/drawable/ic_sicomplaint_logo.xml` dengan viewport **42 × 56**, tanpa menggambar ulang atau menebak bentuk. Fill pin #E53935, fill balon #FFFFFF, dan stroke centang #E53935 dengan lebar **5.16558**, cap/join **round** dipertahankan.

Varian putih memakai geometri yang sama dengan pertukaran merah/putih; varian Toolbar hanya mengubah ukuran intrinsik. Launcher icon juga memakai logo asli di atas latar putih. SVG sumber disimpan pada `docs/figma-assets/Logo.svg`.

Heading memakai Poppins SemiBold/Bold, body memakai aset Inter project. Poppins dan lisensi OFL diambil dari [repositori resmi Google Fonts](https://github.com/google/fonts/tree/main/ofl/poppins); lisensi disertakan di `docs/licenses/Poppins-OFL.txt`.

## Audit perbedaan sebelum perubahan

| Area | Sebelum | Perbaikan berdasarkan Figma |
|---|---|---|
| Logo | Balon merah berbentuk berbeda, tanpa siluet pin asli | VectorDrawable dari path SVG asli |
| Navigasi | Lima menu, formulir menjadi tab | Empat tab, Buat Laporan melalui FAB merah berikon plus |
| Tipografi | Heading Inter | Heading Poppins, body Inter |
| Beranda | Dashboard kartu putih, tombol penuh | Hero merah, sapaan, judul dua baris, pencarian putih, ikon kategori |
| Login | Kartu dengan banyak penjelasan teknis | Latar #FAF7F6, logo, heading dan input sesuai susunan referensi |
| Kartu laporan | Semua informasi ditumpuk vertikal, tombol besar | Ikon dalam kotak soft-red di kiri, metadata ringkas, status chip, Detail kecil |
| Detail | Ilustrasi logo/user besar | Foto bukti, kategori/status sejajar, judul dan metadata, kartu pelapor |
| Lacak Status | Belum ada | Activity dengan kartu ID dan linimasa empat tahap |
| Notifikasi | Belum ada | Tab daftar notifikasi lokal, indikator belum dibaca, mark-as-read |
| Profil | Kartu putih biasa | Header merah, avatar inisial putih, statistik tumpang tindih, baris aksi |
| Formulir | Field dasar | Foto, judul, kategori, deskripsi, lokasi; opsi UTS tetap tersedia |

## Sistem desain yang diterapkan

- Primary #E53935; Primary Dark #B71C1C; background #FAF7F6; Text Dark #1F2937.
- Soft background #FDECEA, gray text #6B7280, border #E5E7EB.
- Status Menunggu jingga, Dalam Proses biru, Selesai hijau, Ditolak merah gelap.
- Penyimpanan status lama `Diproses` tetap didukung dan ditampilkan sebagai **Dalam Proses**.
- Radius komponen utama 16dp; status chip berbentuk pill, FAB/avatar berbentuk lingkaran seperti referensi.
- Spacing utama 8/16/24/32/48dp; tombol utama 56dp.
- Ikon navigasi berupa garis; indikator aktif berwarna merah.
- Theme light mengikuti desain yang diberikan, termasuk ketika perangkat sedang menggunakan mode gelap.

## Delapan layar dan interaksinya

1. **Splash**: merah penuh, varian putih logo asli, nama/tagline di tengah, ProgressBar dan routing session asli tetap berfungsi.
2. **Login/Daftar**: akun demo tetap berlaku; Daftar Akun membuka formulir akun lokal. Password akun baru disimpan sebagai hash dengan salt; aplikasi tidak mengklaim autentikasi server. Ingat username dan bantuan akun tetap berfungsi.
3. **Beranda**: hero merah dan pencarian yang membuka daftar masyarakat terfilter. Empat ikon kategori membuka formulir dengan kategori terpilih. Enam kategori Simple ListView tetap bisa digulir dan diklik untuk Toast.
4. **Buat Laporan**: dibuka melalui FAB. Formulir penuh dengan app bar kembali; bottom navigation disembunyikan agar sesuai referensi. Foto JPG/PNG maksimal 5 MB disalin ke penyimpanan internal. Judul/kategori/deskripsi/lokasi divalidasi. Prioritas dan anonim tersedia pada Opsi laporan; checkbox kebenaran tetap wajib.
5. **Detail Laporan**: tetap `DetailActivity`, menerima ID melalui Intent. Menampilkan data laporan yang dipilih, foto/ilustrasi, status, pelapor/anonim, prioritas, dan tombol Lacak Status.
6. **Lacak Status**: `LacakStatusActivity`, Intent membawa ID yang sama; empat tahap ditampilkan sesuai status tersimpan. Tanggal awal berasal dari laporan; tanggal verifikasi/pengerjaan yang tidak tersedia tidak dibuat-buat.
7. **Notifikasi**: diturunkan dari laporan milik pengguna. Klik membuka detail dan menandai dibaca. Tanda baca serta badge disimpan di SharedPreferences. Ini notifikasi lokal, bukan push dari dinas/backend.
8. **Profil**: avatar inisial, nama, identitas akun/email opsional, statistik aktual, Edit Profil, Riwayat Laporan, dan Logout. Edit Profil disimpan lokal.

Tab **Laporan Saya** menampung ListView laporan pribadi serta pilihan **Masyarakat** untuk mempertahankan fitur daftar publik sebelumnya. Pencarian menyaring judul, kategori, dan lokasi. Daftar ini merupakan layar pendukung fitur UTS; tidak ada mockup khusus pada delapan layar referensi.

## Fitur UTS yang dipertahankan

- `ListView` dengan `ArrayAdapter<String>` dan `android.R.layout.simple_list_item_1` pada kategori Beranda.
- `setOnItemClickListener` untuk Toast kategori.
- `PengaduanAdapter : BaseAdapter`, data class Pengaduan, ViewHolder dengan ViewBinding, dan enam contoh laporan.
- Klik baris maupun tombol Detail membuka DetailActivity.
- Laporan lokal JSON pada SharedPreferences `SICOMPLAINT_REPORTS`, riwayat per username, session `USER_SESSION`, Intent, validasi, dialog konfirmasi, login/logout.
- TextView, EditText, Button, ImageView, Toast, Toolbar, Fragment, BottomNavigationView, Spinner, RadioGroup, CheckBox, Switch, ProgressBar, ScrollView, CardView; ditambah FAB yang berfungsi.
- Nama `LginActivity`, package/applicationId, project, SDK, dan pendekatan Kotlin/XML tetap sama. Tidak ada RecyclerView atau Compose.

Field `fotoPath` ditambahkan dengan nilai default kosong. Reader JSON memakai `optString`, sehingga laporan lama yang belum memiliki field ini tetap terbaca. Data laporan tidak dihapus saat logout.

Autofill sistem dinonaktifkan pada form lokal utama, login/daftar, dan dialog profil agar popup layanan sistem tidak mengambil fokus saat form/dialog ditutup. Fitur Ingat username milik aplikasi tetap tersedia.

## File utama yang berubah/ditambahkan

Kotlin pada `app/src/main/java/com/example/rafli_3tia/`:

- Diubah: MainActivity, SplashActivity (warna ikon system bar), LginActivity, BerandaFragment, PengaduanFragment, BuatLaporanFragment, ProfilFragment, DetailActivity, Pengaduan, PengaduanAdapter, PengaduanRepository.
- Baru: LacakStatusActivity, NotifikasiFragment, NotifikasiRepository, AkunRepository, PhotoStore.
- `RiwayatFragment` lama tetap tersedia untuk kompatibilitas kode; akses riwayat sekarang masuk ke tab Laporan Saya.

Resource utama:

- Layout: activity_main, activity_splash, activity_lgin, activity_detail, activity_lacak_status, header_beranda, fragment_beranda, fragment_pengaduan, fragment_buat_laporan, fragment_notifikasi, fragment_profil, item_pengaduan, item_notifikasi, dialog_register.
- Theme/styles: values/themes, values-night/themes, values/figma_styles, values/ids, color/input_stroke.
- Menu: menu/bottom_navigation.
- Aset: logo XML asli/putih/Toolbar, ikon garis `ic_figma_*`, background/selector/status chip, foto referensi `laporan_jalan.jpg`, font Poppins dan launcher icon.
- Manifest: pendaftaran LacakStatusActivity; semua activity lama dipertahankan.
- `app/build.gradle.kts`: `testOptions.animationsDisabled = true` khusus instrumentation test untuk menghindari race transisi dialog emulator; versi/dependency produksi tetap dipertahankan.
- Test: `SiComplaintFlowTest.kt` diperbarui untuk empat tab, FAB, linimasa dan notifikasi.

File asli sebelum perubahan disimpan di `backup_figma_20261025_074816`. Backup UTS sebelumnya juga tetap tersedia.

## Perbandingan visual dan keterbatasan

Screenshot emulator dibandingkan dengan potongan delapan layar pada `Sheet3.png`; potongan referensi berada di `docs/figma-reference/`. Galeri perbandingan dan bukti hasil akhir tersedia di `docs/figma-comparison.html` dan `docs/figma-evidence/`.

Perbaikan dari perbandingan awal meliputi inset ganda yang memotong label navigasi, bayangan abu-abu tombol outline, pemisahan judul hero menjadi dua baris, dan proporsi header merah Beranda/Profil.

**Hasil tidak diklaim identik pixel-per-pixel.** Sumber berupa gambar presentasi, bukan file Figma dengan ukuran komponen/layer lengkap. System bar, skala layar/perangkat, isi dinamis, serta komponen UTS tambahan memengaruhi hasil. Semua perbedaan ini harus dibaca bersama screenshot nyata.

Foto jalan contoh diekstrak dari area foto yang memang ada pada Sheet3.png, bukan foto pengganti yang ditebak. Resolusi sumbernya terbatas; foto milik pengguna akan ditampilkan dari berkas lokal yang dipilih.

Riwayat status dan notifikasi pada data contoh merupakan representasi status lokal. Aplikasi tidak melakukan verifikasi dinas, GPS otomatis, atau pengiriman ke backend. Lokasi tetap diisi pengguna.

Hasil build dan pengujian final dicatat di `HASIL_PENGUJIAN_FIGMA.md`.

## Pertanyaan UTS sesuai implementasi final

1. **Berapa menu Bottom Navigation?** Empat: Beranda, Laporan Saya, Notifikasi, Profil. Formulir dibuka lewat FAB, bukan tab kelima.
2. **Bagaimana perpindahan halaman?** MainActivity menerima ID menu dan menampilkan Fragment bertag yang sesuai. Fragment lama disembunyikan dan dipakai ulang; mode formulir mempunyai tombol kembali.
3. **Di mana Simple List Item digunakan?** ListView kategori di Beranda memakai ArrayAdapter dan `android.R.layout.simple_list_item_1`. Header hero tidak dapat dipilih; posisi klik kategori dikurangi jumlah header.
4. **Mengapa memakai Custom Adapter?** Laporan mempunyai ikon, judul, kategori, lokasi, tanggal, status, dan tombol Detail, sehingga membutuhkan `item_pengaduan.xml` dan binding dari data class.
5. **Apa fungsi ViewHolder?** Menyimpan binding baris di tag convertView; saat baris dipakai ulang, view tidak perlu di-inflate ulang. Isi dan listener tetap diikat ke laporan yang tepat.
6. **Bagaimana laporan dibuka di Detail dan Lacak Status?** Intent membawa `PENGADUAN_ID`. Activity mengambil objek dari repository, lalu menampilkan detail atau tahapan berdasarkan status lokal.
7. **Bagaimana data bertahan setelah logout?** Laporan JSON berada di SharedPreferences terpisah dari USER_SESSION. Logout hanya menghapus status login/username dan membersihkan back stack.
8. **Bagaimana kompatibilitas laporan lama dijaga?** Field fotoPath bersifat opsional dengan default kosong dan dibaca melalui optString; laporan tanpa foto tetap valid.
9. **Bagaimana validasi formulir bekerja?** Judul, deskripsi, lokasi harus terisi, kategori harus dipilih, dan checkbox kebenaran harus aktif. ID UUID dan status Menunggu dibuat setelah validasi; kegagalan simpan tidak mengosongkan form.
10. **Apakah notifikasi dan lacak status berasal dari backend?** Tidak. Notifikasi diturunkan dari laporan lokal, status baca disimpan per akun, dan linimasa mewakili status tersimpan. Tanggal tindak lanjut yang belum ada tidak dibuat-buat.
