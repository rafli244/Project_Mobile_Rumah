package com.example.rafli_3tia

import android.content.Context
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Checks the corrected four-tab/FAB design and the original UTS features on a device. */
@RunWith(AndroidJUnit4::class)
class SiComplaintFlowTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private fun visible(id: Int) = onView(allOf(withId(id), isDisplayed()))
    // Keep normal touch injection for tabs, rows, FAB and form controls. For modal buttons,
    // invoke the real View click listener to avoid API-33 emulator input acknowledgement races.
    private fun dialogClick() = object : androidx.test.espresso.ViewAction {
        override fun getConstraints() = isDisplayed()
        override fun getDescription() = "invoke the displayed dialog button's click listener"
        override fun perform(controller: androidx.test.espresso.UiController, view: android.view.View) {
            view.performClick()
        }
    }
    private fun capture(name: String, delay: Long = 400) {
        SystemClock.sleep(delay)
        instrumentation.uiAutomation.executeShellCommand("mkdir -p /sdcard/Download/sicomplaint-figma-evidence").use { fd -> ParcelFileDescriptor.AutoCloseInputStream(fd).use { it.readBytes() } }
        instrumentation.uiAutomation.executeShellCommand("screencap -p /sdcard/Download/sicomplaint-figma-evidence/$name.png").use { fd -> ParcelFileDescriptor.AutoCloseInputStream(fd).use { it.readBytes() } }
    }
    private fun withPreferencesRestored(block: () -> Unit) {
        val names = listOf("USER_SESSION", "LOGIN_PREFERENCES", "SICOMPLAINT_REPORTS", "NOTIFICATION_STATE", "LOCAL_ACCOUNTS")
        val backups = names.associateWith { context.getSharedPreferences(it, Context.MODE_PRIVATE).all.toMap() }
        try {
            context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE).edit().clear().commit()
            SystemClock.sleep(4500) // Clear queued Toasts between independent scenarios.
            block()
        } catch (error: Throwable) {
            capture("debug-window-failure", 0)
            android.util.Log.e("SiComplaintFlowTest", instrumentation.uiAutomation.windows.joinToString { "${it.type}:${it.title}:${it.isFocused}" })
            throw error
        } finally {
            instrumentation.runOnMainSync {
                val monitor = androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                androidx.test.runner.lifecycle.Stage.values().filter { it != androidx.test.runner.lifecycle.Stage.DESTROYED }.forEach { stage ->
                    monitor.getActivitiesInStage(stage).toList().forEach { it.finish() }
                }
            }
            instrumentation.waitForIdleSync()
            backups.forEach { (name, values) ->
                val edit = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear()
                values.forEach { (key, value) -> when (value) {
                    is String -> edit.putString(key, value)
                    is Boolean -> edit.putBoolean(key, value)
                    is Int -> edit.putInt(key, value)
                    is Long -> edit.putLong(key, value)
                    is Float -> edit.putFloat(key, value)
                } }
                edit.commit()
            }
        }
    }

    @Test
    fun fourTabsFabListsFormDetailTrackingNotificationsAndPersistence() = withPreferencesRestored {
        ActivityScenario.launch(SplashActivity::class.java)
        capture("01-splash", 800)
        SystemClock.sleep(1500)
        visible(R.id.btnLogin).check(matches(isDisplayed()))
        capture("02-login")
        visible(R.id.btnLogin).perform(click())
        visible(R.id.editTextText).perform(replaceText("rafli"))
        visible(R.id.editTextTextPassword).perform(replaceText("salah"), closeSoftKeyboard())
        visible(R.id.btnLogin).perform(click())
        visible(R.id.btnLogin).check(matches(isDisplayed()))
        visible(R.id.editTextTextPassword).perform(replaceText("12345"), closeSoftKeyboard())
        visible(R.id.btnLogin).perform(click())
        visible(R.id.tvWelcomeTitle).check(matches(withText("Halo, Rafli")))
        instrumentation.runOnMainSync {
            val activity = androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED).filterIsInstance<MainActivity>().single()
            val menu = activity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation).menu
            assertEquals(4, menu.size())
            assertEquals(listOf("Beranda", "Laporan Saya", "Notifikasi", "Profil"), (0 until menu.size()).map { menu.getItem(it).title.toString() })
        }
        capture("03-beranda", 8000)
        onData(equalTo("Jalan Rusak")).inAdapterView(withId(R.id.listKategori)).perform(click())
        visible(R.id.nav_pengaduan).perform(click())
        visible(R.id.nav_pengaduan).check(matches(isSelected()))
        visible(R.id.rbMasyarakat).perform(click())
        visible(R.id.etFilter).perform(replaceText("Merdeka"), closeSoftKeyboard())
        capture("04-laporan", 2400)
        onData(anything()).inAdapterView(withId(R.id.listPengaduan)).atPosition(0).onChildView(withId(R.id.btnDetail)).perform(click())
        visible(R.id.tvReportTitle).check(matches(withText("Jalan Berlubang Parah di Jl. Merdeka")))
        capture("05-detail")
        onView(withId(R.id.btnLacak)).perform(scrollTo(), click())
        visible(R.id.stepCircle2).check(matches(withText("✓")))
        visible(R.id.stepCircle3).check(matches(withText("4")))
        capture("06-lacak")
        pressBack(); pressBack()
        visible(R.id.fabBuat).perform(click())
        onView(withId(R.id.bottomNavigation)).check(matches(not(isDisplayed())))
        onView(withId(R.id.btnKirim)).perform(scrollTo(), click())
        onView(withId(R.id.etJudul)).check(matches(hasErrorText("Wajib diisi")))
        val title = "Laporan UTS Figma " + System.currentTimeMillis()
        onView(withId(R.id.etJudul)).perform(scrollTo(), replaceText(title), closeSoftKeyboard())
        onView(withId(R.id.etDeskripsi)).perform(scrollTo(), replaceText("Saluran air tersumbat di dekat persimpangan."), closeSoftKeyboard())
        onView(withId(R.id.etLokasi)).perform(scrollTo(), replaceText("Jl. Uji RT 01"), closeSoftKeyboard())
        onView(withId(R.id.spinnerKategori)).perform(scrollTo(), click())
        onData(equalTo("Drainase Tersumbat")).perform(click())
        onView(withId(R.id.tvFotoLabel)).perform(scrollTo())
        capture("07-buat-laporan", 2400)
        pressBack()
        visible(R.id.fabBuat).perform(click())
        onView(withId(R.id.etJudul)).check(matches(withText(title)))
        onView(withId(R.id.btnOpsi)).perform(scrollTo(), click())
        onView(withId(R.id.rbMendesak)).perform(scrollTo(), click())
        onView(withId(R.id.switchAnonim)).perform(scrollTo(), click())
        onView(withId(R.id.cbKonfirmasi)).perform(scrollTo(), click())
        onView(withId(R.id.btnKirim)).perform(scrollTo(), click())
        onView(withText("Laporan berhasil dikirim")).inRoot(androidx.test.espresso.matcher.RootMatchers.isDialog()).check(matches(isDisplayed()))
        onView(withText("Lihat riwayat")).inRoot(androidx.test.espresso.matcher.RootMatchers.isDialog()).perform(dialogClick())
        SystemClock.sleep(800)
        visible(R.id.nav_pengaduan).check(matches(isSelected()))
        val stored = PengaduanRepository(context).riwayat("rafli").first { it.judul == title }
        assertEquals("Menunggu", stored.status); assertEquals("Mendesak", stored.prioritas); assertTrue(stored.anonim)
        assertFalse(PengaduanRepository(context).riwayat("admin").any { it.id == stored.id })
        onData(anything()).inAdapterView(allOf(withId(R.id.listPengaduan), isDisplayed())).atPosition(0).perform(click())
        visible(R.id.tvReportTitle).check(matches(withText(title)))
        onView(withId(R.id.tvPelapor)).perform(scrollTo()).check(matches(withText("Pelapor: Anonim")))
        pressBack()
        // Non-home tab restoration and repository persistence.
        instrumentation.runOnMainSync {
            androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED).filterIsInstance<MainActivity>()
                .forEach { it.finish() }
        }
        instrumentation.waitForIdleSync()
        val fresh = ActivityScenario.launch(MainActivity::class.java)
        visible(R.id.nav_pengaduan).perform(click()); fresh.recreate()
        visible(R.id.nav_pengaduan).check(matches(isSelected()))
        visible(R.id.tvHeading).check(matches(withText("Riwayat laporan saya")))
        assertEquals(title, PengaduanRepository(context).cari(stored.id)?.judul)
        visible(R.id.nav_notifikasi).perform(click())
        capture("08-notifikasi")
        val unread = NotifikasiRepository(context).unread("rafli")
        onData(anything()).inAdapterView(withId(R.id.listNotifikasi)).atPosition(0).perform(click())
        visible(R.id.tvReportTitle).check(matches(withText(title)))
        pressBack()
        assertEquals(unread-1, NotifikasiRepository(context).unread("rafli"))
        visible(R.id.btnBacaSemua).perform(click())
        assertEquals(0, NotifikasiRepository(context).unread("rafli"))
        visible(R.id.nav_profil).perform(click())
        visible(R.id.tvUsername).check(matches(withText("Rafli Hanafi")))
        capture("09-profil")
        assertNotNull(PengaduanRepository(context).cari(stored.id))
        fresh.close()
    }

    @Test
    fun logoutClearsSessionAndKeepsReports() = withPreferencesRestored {
        context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE).edit()
            .putBoolean("isLogin", true).putString("USERNAME", "rafli").commit()
        val count = PengaduanRepository(context).semua().size
        ActivityScenario.launch(MainActivity::class.java)
        visible(R.id.nav_profil).perform(click())
        onView(withId(R.id.btnLogout)).perform(scrollTo(), click())
        onView(withText("Ya, Keluar")).inRoot(androidx.test.espresso.matcher.RootMatchers.isDialog()).perform(dialogClick())
        SystemClock.sleep(800)
        visible(R.id.btnLogin).check(matches(isDisplayed()))
        assertFalse(context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE).getBoolean("isLogin", true))
        assertEquals(count, PengaduanRepository(context).semua().size)
    }

    @Test
    fun registrationLoginAndEmptyLists() = withPreferencesRestored {
        ActivityScenario.launch(LginActivity::class.java)
        onView(withId(R.id.tvDaftar)).perform(scrollTo(), click())
        val user = "uts" + System.currentTimeMillis()
        onView(withId(R.id.etNamaDaftar)).perform(replaceText("Warga Uji"))
        onView(withId(R.id.etUsernameDaftar)).perform(replaceText(user))
        onView(withId(R.id.etPasswordDaftar)).perform(replaceText("uji12345"), closeSoftKeyboard())
        onView(withText("Daftar")).inRoot(androidx.test.espresso.matcher.RootMatchers.isDialog()).perform(dialogClick())
        visible(R.id.editTextText).check(matches(withText(user)))
        visible(R.id.editTextTextPassword).perform(click(), replaceText("uji12345"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(scrollTo(), click())
        visible(R.id.nav_pengaduan).perform(click())
        visible(R.id.tvEmpty).check(matches(isDisplayed()))
        visible(R.id.nav_notifikasi).perform(click())
        visible(R.id.tvEmpty).check(matches(isDisplayed()))
        visible(R.id.nav_profil).perform(click())
        visible(R.id.tvUsername).check(matches(withText("Warga Uji")))
        assertTrue(AkunRepository(context).valid(user, "uji12345"))
        assertFalse(AkunRepository(context).valid(user, "salah"))
    }

    @Test
    fun legacyReportSchemaAndLocalNotificationReadState() = withPreferencesRestored {
        val raw = """[{"id":"legacy-uts","judul":"Data laporan lama","kategori":"Jalan Rusak","lokasi":"Jl. Lama","tanggal":"24 Oktober 2026","status":"Menunggu","deskripsi":"Laporan sebelum sinkronisasi","pelapor":"rafli","prioritas":"Mendesak","anonim":true}]"""
        context.getSharedPreferences("SICOMPLAINT_REPORTS", Context.MODE_PRIVATE).edit().putString("reports", raw).commit()
        val legacy = PengaduanRepository(context).cari("legacy-uts")!!
        assertEquals("", legacy.fotoPath); assertTrue(legacy.anonim); assertEquals("Mendesak", legacy.prioritas)
        val event = NotifikasiRepository(context).semua("rafli").first { it.reportId == legacy.id }
        NotifikasiRepository(context).mark("rafli", event.id)
        assertTrue(NotifikasiRepository(context).semua("rafli").first { it.id == event.id }.dibaca)
        assertFalse(NotifikasiRepository(context).semua("admin").any { it.reportId == legacy.id })
    }
}
