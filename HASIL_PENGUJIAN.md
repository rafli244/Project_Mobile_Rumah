> **Pembaruan final 25 Oktober 2026:** navigasi kini empat menu dan FAB sesuai Figma. Dokumen di bawah adalah laporan tahap sebelumnya. Lihat [Audit Figma](AUDIT_SINKRONISASI_FIGMA.md) dan [Hasil Pengujian Final](HASIL_PENGUJIAN_FIGMA.md) untuk implementasi terbaru.

# Hasil pengujian SiComplaint

Tanggal: 25 Oktober 2026 (Asia/Jakarta).

## Lingkungan

- Project asli: `C:\Users\Predator\Documents\Rafli_3TIA\RafliHanafi_3TIA`.
- Android Studio Quail 3 / 2026.1.3, JBR bawaan Android Studio.
- Gradle Wrapper 9.5.0, Android Gradle Plugin 9.3.3, compile/target SDK 37, min SDK 29: konfigurasi awal project dipertahankan.
- Emulator Pixel_6, Android 13 / API 33, serial `emulator-5554`.

## Gradle Sync

Gradle Sync benar-benar dijalankan dari tautan **Sync Now** di Android Studio. Log IDE mencatat `onSuccess(RESOLVE_PROJECT:1)`, `onImportFinished` untuk path project asli, dan `Gradle sync finished in 18 s` pada 25 Oktober 2026 pukul 01:01:48 WIB.

## Build dan pemeriksaan

Perintah final dari root project dengan JAVA_HOME menunjuk JBR Android Studio:

```powershell
.\gradlew.bat --no-configuration-cache :app:assembleDebug :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug --continue
```

Hasil final dan bukti laporan otomatis dicatat pada bagian hasil akhir di bawah.

### Hasil akhir

| Pemeriksaan | Hasil |
|---|---|
| Gradle Sync di Android Studio | Berhasil, 18 detik, dibuktikan log IDE |
| `assembleDebug` | BUILD SUCCESSFUL; APK debug dihasilkan |
| `testDebugUnitTest` | 1 test, 0 gagal, 0 error |
| `connectedDebugAndroidTest` | 2 test, 0 gagal; satu test konteks lama dan satu alur fungsional lengkap |
| `lintDebug` | Berhasil; 0 error, 135 warning |
| Parse seluruh XML source | Berhasil |

Run gabungan build/unit/UI/Lint selesai `BUILD SUCCESSFUL`. Setelah pemeriksaan screenshot, ukuran logo Toolbar dan skala ilustrasi detail disesuaikan, serta error field formulir dibersihkan ketika pengguna mengisi teks. Seluruh rangkaian build/unit/UI/Lint kemudian dijalankan kembali pada implementasi akhir.

APK adalah varian debug untuk demonstrasi/pengujian, bukan release bertanda tangan untuk distribusi toko aplikasi.

## Cakupan pengujian emulator

`SiComplaintFlowTest.splashLoginNavigationListsFormDetailPersistenceAndLogout` menguji satu alur lengkap:

1. Sesi awal dikosongkan sementara; SplashActivity membawa pengguna ke login setelah dua detik.
2. Klik login kosong tidak meninggalkan halaman login.
3. Login rafli dengan password salah ditolak, kemudian password benar membuka beranda dan menampilkan username.
4. Klik kategori Jalan Rusak pada ArrayAdapter sederhana. Listener Toast dieksekusi; tampilan Toast sistem tidak memakai assertion visual khusus.
5. Tab Pengaduan dipilih dan indikator aktif diperiksa.
6. Tombol Detail dari baris Custom Adapter membuka DetailActivity; tombol Back kembali ke daftar.
7. Kirim formulir kosong menunjukkan error Wajib diisi pada judul.
8. Form diisi judul unik, lokasi, deskripsi; pindah ke Profil dan kembali ke Buat Laporan untuk memeriksa draft judul tetap ada.
9. Pilih kategori Drainase Tersumbat, prioritas Mendesak, nama anonim, dan checkbox konfirmasi; kirim dan periksa dialog berhasil.
10. Buka Riwayat dari dialog; periksa status Menunggu, prioritas, flag anonim, dan pastikan laporan tidak masuk riwayat admin.
11. Klik baris riwayat; periksa judul laporan tepat dan teks Pelapor: Anonim pada detail.
12. Periksa username pada Profil.
13. Buka MainActivity baru, pindah Riwayat, lalu recreate activity; periksa tab aktif dan judul Riwayat dipulihkan. Repository baru masih membaca laporan yang dikirim.
14. Batal logout tetap berada di Profil; konfirmasi logout membuka login dan isLogin menjadi false.
15. Data laporan tetap ada setelah logout.

Pengujian mem-backup dan memulihkan tiga SharedPreferences yang disentuh sehingga data pengujian tidak ditinggalkan dalam daftar pengguna. Akun demo serta implementasi sesi yang sudah ada tetap dipertahankan.

Unit test lama `ExampleUnitTest` dan instrumentation test lama `ExampleInstrumentedTest.useAppContext` juga dijalankan. Unit test lama hanya memeriksa operasi sederhana; pengujian fungsional berada pada SiComplaintFlowTest, bukan pada unit test tersebut.

## Riwayat kendala dan perbaikan

- Build debug pertama berhasil.
- Lint offline sempat gagal karena `graphics-shapes-desktop:1.0.1` belum tersedia di cache. Dependensi kemudian diunduh; pemeriksaan online dilanjutkan.
- Kode test pertama memiliki impor `pressBack`/`closeSoftKeyboard` ambigu. Impor dibuat eksplisit sebelum pengujian dijalankan kembali.
- Percobaan pertama alur UI berhenti dengan `RootViewWithoutFocusException` pada login. Autofill sistem dinonaktifkan hanya pada layout login akun demo. Pengujian berikutnya berhasil.
- Deskripsi aksesibilitas ditambahkan ke ilustrasi DetailActivity.
- Lima screenshot diperiksa secara visual. Logo Toolbar memakai varian 32dp agar tidak terpotong; ilustrasi logo pada detail memakai FIT_CENTER. Screenshot diambil setelah antrean Toast menghilang agar navigasi terbaca.

## Batas pengujian

- Pengujian perangkat dilakukan pada satu emulator API 33, bukan seluruh versi Android/perangkat fisik.
- Pemulihan activity diuji lewat ActivityScenario.recreate; kill process oleh OS dan reboot perangkat tidak disimulasikan.
- Alur login fungsional diuji menggunakan akun rafli. Kredensial admin tetap dipertahankan dari kode asli; isolasi riwayat admin diperiksa dari repository.
- Toast kategori diklik dan listener dijalankan; tidak ada assertion screenshot Toast sistem.
- Cabang kegagalan disk/JSON rusak, semua kombinasi validasi, font sangat besar, landscape, dan mode malam belum diuji secara terpisah.
- Lint dapat memberi warning mengenai teks hardcoded, aset referensi yang tidak digunakan langsung, versi dependency, dan optimasi resource. Warning tidak dianggap sama dengan error kompilasi.
- Status Diproses/Selesai berasal dari data contoh. Laporan baru tersimpan Menunggu; tidak ada backend atau simulasi verifikasi dinas otomatis.

## Bukti yang dapat dibuka

- APK: `app/build/outputs/apk/debug/app-debug.apk`.
- Laporan UI test: `app/build/reports/androidTests/connected/debug/index.html`.
- Hasil mesin UI test: `app/build/outputs/androidTest-results/connected/debug/`.
- Laporan unit test: `app/build/reports/tests/testDebugUnitTest/index.html`.
- Android Lint: `app/build/reports/lint-results-debug.html`.
- Screenshot pengujian: `docs/uts-evidence/01-beranda.png`, `02-pengaduan.png`, `03-formulir.png`, `04-detail.png`, `05-profil.png`.
