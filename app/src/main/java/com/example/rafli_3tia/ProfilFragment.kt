package com.example.rafli_3tia

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.rafli_3tia.databinding.FragmentProfilBinding

class ProfilFragment : Fragment(R.layout.fragment_profil) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentProfilBinding.bind(view)
        b.root.post {
            b.profileHeader.layoutParams.height = (b.root.height * .76f).toInt().coerceAtLeast((352 * resources.displayMetrics.density).toInt())
            b.profileHero.layoutParams.height = b.profileHeader.layoutParams.height - (48 * resources.displayMetrics.density).toInt()
            b.profileHeader.requestLayout()
        }
        b.btnLogout.setOnClickListener { (requireActivity() as MainActivity).logout() }
        b.btnRiwayat.setOnClickListener { (requireActivity() as MainActivity).showReports(true) }
        b.btnEditProfil.setOnClickListener { editProfile() }
        refresh()
    }
    override fun onHiddenChanged(hidden: Boolean) { super.onHiddenChanged(hidden); if (!hidden) refresh() }
    private fun refresh() {
        val b = view?.let { FragmentProfilBinding.bind(it) } ?: return
        val host = requireActivity() as MainActivity
        val name = host.account.nama(host.username)
        b.tvUsername.text = name; b.tvEmail.text = host.account.email(host.username)
        b.tvAvatar.text = name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase(java.util.Locale.ROOT)
        val own = PengaduanRepository(requireContext()).riwayat(host.username)
        b.tvTotal.text = own.size.toString(); b.tvSelesai.text = own.count { it.status == "Selesai" }.toString()
        b.tvDiproses.text = own.count { it.status == "Diproses" }.toString()
    }
    private fun editProfile() {
        val host = requireActivity() as MainActivity
        val box = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL; val dp = (24 * resources.displayMetrics.density).toInt(); setPadding(dp, dp, dp, dp) }
        box.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        val name = EditText(requireContext()).apply { hint = "Nama lengkap"; setText(host.account.nama(host.username)); inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS }
        val email = EditText(requireContext()).apply { hint = "Email (opsional)"; setText(host.account.email(host.username).takeUnless { it.startsWith("@") } ?: ""); inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS }
        box.addView(name); box.addView(email)
        val dialog = AlertDialog.Builder(requireContext()).setTitle("Edit Profil").setView(box).setNegativeButton("Batal", null).setPositiveButton("Simpan", null).create()
        dialog.setOnShowListener { dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (name.text.isNullOrBlank()) name.error = "Nama wajib diisi"
            else if (!email.text.isNullOrBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) email.error = "Email tidak valid"
            else if (host.account.update(host.username, name.text.toString(), email.text.toString())) { refresh(); dialog.dismiss() }
            else Toast.makeText(requireContext(), "Profil gagal disimpan", Toast.LENGTH_SHORT).show()
        } }
        dialog.show()
    }
}
