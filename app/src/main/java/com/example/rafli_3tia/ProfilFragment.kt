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
        val sharedPreferences = requireContext().getSharedPreferences(
            "USER_SESSION", android.content.Context.MODE_PRIVATE
        )
        val name = sharedPreferences.getString("USERNAME", host.username) ?: host.username
        b.tvUsername.text = name
        b.tvEmail.text = "${name.lowercase(java.util.Locale.ROOT)}@sicomplaint.id"
        b.tvAvatar.text = name.take(2).uppercase(java.util.Locale.ROOT)
        val own = PengaduanRepository(requireContext()).riwayat(host.username)
        b.tvTotal.text = own.size.toString(); b.tvSelesai.text = own.count { it.status == "Selesai" }.toString()
        b.tvDiproses.text = own.count { it.status == "Diproses" }.toString()
    }
    private fun editProfile() {
        val host = requireActivity() as MainActivity
        val sharedPreferences = requireContext().getSharedPreferences(
            "USER_SESSION", android.content.Context.MODE_PRIVATE
        )
        val currentName = sharedPreferences.getString("USERNAME", host.username) ?: host.username
        val box = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL; val dp = (24 * resources.displayMetrics.density).toInt(); setPadding(dp, dp, dp, dp) }
        val name = EditText(requireContext()).apply { hint = "Nama lengkap"; setText(currentName); inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS }
        box.addView(name)
        val dialog = AlertDialog.Builder(requireContext()).setTitle("Edit Nama Pengguna").setView(box).setNegativeButton("Batal", null).setPositiveButton("Simpan", null).create()
        dialog.setOnShowListener { dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (name.text.isNullOrBlank()) name.error = "Nama wajib diisi"
            else {
                requireActivity().getSharedPreferences("USER_SESSION", android.content.Context.MODE_PRIVATE)
                    .edit().putString("USERNAME", name.text.toString().trim()).apply()
                refresh()
                dialog.dismiss()
            }
        } }
        dialog.show()
    }
}
