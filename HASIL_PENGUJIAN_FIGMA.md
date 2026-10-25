# Hasil pengujian final sinkronisasi Figma — SiComplaint

Tanggal: 25 Oktober 2026, Asia/Jakarta. Project yang diuji: `C:\Users\Predator\Documents\Rafli_3TIA\RafliHanafi_3TIA`.

## Hasil akhir

| Pemeriksaan | Hasil |
|---|---|
| `assembleDebug` | BUILD SUCCESSFUL; APK debug dihasilkan |
| `testDebugUnitTest` | 1 test, 0 gagal, 0 error |
| `connectedDebugAndroidTest` | 5 test, 0 gagal, 0 error |
| `lintDebug` | 0 error, 220 warning |
| Seluruh XML source | Berhasil di-parse |
| Menu bawah | Tepat 4 item dengan nama sesuai instruksi final |
| Konversi logo | Ketiga path VectorDrawable sama persis dengan `D:\Logo.svg` |

Lingkungan perangkat: emulator Pixel_6, Android 13 / API 33, `emulator-5554`. Build menggunakan JBR Android Studio, Gradle Wrapper 9.5.0, AGP 9.3.3, min SDK 29, compile/target SDK 37. Versi project/SDK/dependency asli tidak dimigrasikan pada sinkronisasi desain ini.

Perintah dari root project:

```powershell
.\gradlew.bat --offline --no-configuration-cache :app:assembleDebug :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug --continue
```

APK: `app/build/outputs/apk/debug/app-debug.apk`, 17,324,086 byte. SHA-256: `93ff3e2ea821a83f80e6d7b3d8c0d8d42b86b14f87d1538802fd5a925e248bec`.

## Pengujian perangkat yang benar-benar dilakukan

1. `ExampleInstrumentedTest.useAppContext`: package aplikasi benar.
2. `SiComplaintFlowTest.fourTabsFabListsFormDetailTrackingNotificationsAndPersistence`: splash ke login; input kosong/password salah; login benar; sapaan pengguna; tepat empat menu; klik Simple ListView kategori; pilihan daftar masyarakat; pencarian Merdeka; tombol Detail pada Custom Adapter; data detail benar; Lacak Status menampilkan tiga tahap selesai untuk contoh Diproses; kembali; FAB membuka formulir tanpa bottom navigation; validasi judul kosong; pengisian kategori/deskripsi/lokasi; draft tetap ada setelah kembali dan membuka FAB; prioritas Mendesak/anonim/konfirmasi; penyimpanan dan dialog berhasil; riwayat pengguna; isolasi dari akun admin; klik baris riwayat dan pelapor anonim; recreate MainActivity pada tab Laporan Saya; laporan tetap terbaca; notifikasi membuka laporan yang tepat; status baca dan tandai semua; nama profil benar.
3. `SiComplaintFlowTest.registrationLoginAndEmptyLists`: membuka Daftar Akun, mengisi akun lokal, membuat akun, login dengan akun tersebut, daftar pribadi/notifikasi kosong, nama profil benar, password benar/salah diverifikasi oleh repository.
4. `SiComplaintFlowTest.legacyReportSchemaAndLocalNotificationReadState`: laporan JSON lama tanpa fotoPath tetap terbaca, anonim/prioritas terjaga, baca notifikasi bertahan pada instance repository baru, dan notifikasi tidak bocor ke akun admin.
5. `SiComplaintFlowTest.logoutClearsSessionAndKeepsReports`: skenario logout terisolasi membuka Profil, mengonfirmasi logout, memeriksa halaman login, isLogin=false, dan jumlah laporan tetap sama.

Unit test lama ExampleUnitTest tetap lulus. Unit test tersebut sederhana; bukti fungsional berasal dari instrumentation test di atas.

Tab, baris ListView, FAB, dan kontrol form diuji dengan injeksi sentuhan Espresso biasa. Untuk tombol modal, test memanggil listener klik View yang tampil melalui custom ViewAction, karena injeksi sentuhan modal pada emulator API 33 sempat tertunda. Jadi hasil dialog memverifikasi listener, perpindahan layar, dan data; bukan klaim bahwa semua transisi sentuhan modal telah diuji secara fisik pada perangkat nyata.

SharedPreferences yang disentuh test dibackup dan dipulihkan oleh fixture. Percobaan awal yang tertunda dihentikan untuk diagnosis, lalu test dijalankan ulang; angka hasil akhir di tabel berasal dari rangkaian final yang lulus, bukan percobaan awal.

## Pemeriksaan visual

Sembilan screenshot native berada di `docs/figma-evidence/`: Splash, Login, Beranda, daftar Laporan Saya, Detail, Lacak Status, Buat Laporan, Notifikasi, Profil. Potongan referensi berasal dari Sheet3.png pada `docs/figma-reference/`.

- Galeri pasangan: `docs/figma-comparison.html`.
- Lembar perbandingan ringkas: `docs/perbandingan-1.png` dan `docs/perbandingan-2.png`. Salinan screenshot pada lembar ini hanya diperkecil dan dipotong area system bar untuk membandingkan konten; screenshot native tidak diubah.
- Perbaikan dari pemeriksaan: logo asli, font Poppins, empat tab/FAB, inset yang memotong label, tombol outline, proporsi hero, judul Beranda dua baris, posisi Login, ukuran deskripsi, status chip, serta header Profil.

**Tidak ada klaim identik pixel-per-pixel.** Sumber berupa gambar presentasi. System bar, ukuran perangkat, data dinamis, field/opsi UTS, tombol Lacak Status, dan logout menjadi perbedaan yang terdokumentasi. Foto jalan contoh berasal dari gambar referensi dengan resolusi terbatas.

## Kendala awal yang diselesaikan

- Label navigasi terpotong akibat penanganan inset ganda. Root kini menangani inset dan BottomNavigationView tidak menambahkannya lagi; pengujian klik empat menu kemudian lulus.
- Outline button berbayang abu-abu. Elevation/state animator outline dinonaktifkan dan surface diatur putih.
- Penyiapan ulang sempat menambahkan deklarasi Kotlin/manifest dua kali. Source diperbaiki dari backup sebelum verifikasi akhir.
- Satu interaksi Espresso setelah pendaftaran sempat tertunda. Autofill pada dialog lokal dinonaktifkan, perpindahan fokus test dibuat eksplisit, dan alur diuji ulang terpisah serta dalam suite final.
- Transisi dialog pada emulator sempat menyebabkan run uji berikutnya tertunda. Konfigurasi Gradle `testOptions.animationsDisabled = true` dipakai khusus pengujian, dan jeda pelepasan fokus ditambahkan sebelum membuka ulang dialog logout.
- Fixture diperbaiki agar tidak menumpuk dua MainActivity saat memeriksa instance baru/recreate; pencocokan tombol dialog dibatasi ke root dialog. Autofill sistem juga dinonaktifkan pada dialog Edit Profil lokal.
- Autofill dinonaktifkan pula pada root MainActivity yang menampung form laporan lokal untuk menghindari popup sistem mengambil fokus sesudah konfirmasi form. Run final memakai sumber perbaikan ini.
- Pengujian logout dipisahkan menjadi skenario baru agar tidak bergantung pada dialog Edit Profil yang dibuka/ditutup sebelumnya. Batal dialog dan Edit Profil sempat dijalankan pada percobaan awal; run final memusatkan assertion logout pada perpindahan login, sesi, dan data.
- Rekaman splash awal menangkap transisi launch sistem. Capture ditunda singkat agar bukti menampilkan custom splash aplikasi.

## Batas pengujian

- Hanya satu emulator API 33; belum ada pengujian perangkat fisik, semua versi Android, landscape, dan font ekstrem.
- Alur pemilih foto sistem, batas berkas 5 MB, kegagalan disk, dan EXIF orientasi belum diuji end-to-end melalui picker. Implementasi foto dikompilasi dan field opsionalnya tetap kompatibel dengan laporan lama.
- Edit Profil dibuka/dibatalkan pada percobaan awal; rangkaian sentuhan modal tersebut mengalami fokus emulator yang tidak stabil. Run final tidak memasukkan Edit Profil ke skenario inti, dan belum menguji semua kombinasi penyimpanan email/nama.
- Recreate activity diuji pada tab Laporan Saya. Kill process oleh OS dan reboot tidak disimulasikan.
- Toast kategori dieksekusi melalui klik; tidak ada assertion visual khusus Toast sistem.
- Notifikasi/linimasa diturunkan dari status lokal. Tidak ada push notification, GPS otomatis, backend, atau verifikasi dinas yang diklaim berlangsung.
- Warning Lint masih ada, antara lain teks hardcoded, resource desain lama yang dipertahankan, dan saran versi/optimasi. Warning bukan error kompilasi.

## Bukti build

- UI test HTML: `app/build/reports/androidTests/connected/debug/index.html`.
- Hasil XML/log UI test: `app/build/outputs/androidTest-results/connected/debug/`.
- Unit test HTML: `app/build/reports/tests/testDebugUnitTest/index.html`.
- Lint HTML: `app/build/reports/lint-results-debug.html`.
- Ringkasan terstruktur: `docs/verifikasi-figma.json`.
- Audit desain, file, sumber, dan fitur: `AUDIT_SINKRONISASI_FIGMA.md`.
