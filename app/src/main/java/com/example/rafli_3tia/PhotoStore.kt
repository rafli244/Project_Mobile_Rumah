package com.example.rafli_3tia

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import java.io.File
import java.util.UUID

object PhotoStore {
    fun importPhoto(context: Context, uri: Uri): String {
        val resolver = context.contentResolver
        val type = resolver.getType(uri)
        require(type == "image/jpeg" || type == "image/png") { "Pilih foto JPG atau PNG." }
        val dir = File(context.filesDir, "report_photos").apply { mkdirs() }
        val file = File(dir, UUID.randomUUID().toString() + if (type == "image/png") ".png" else ".jpg")
        try {
            resolver.openInputStream(uri)!!.use { input ->
                file.outputStream().use { output ->
                    val buffer = ByteArray(8192); var total = 0
                    while (true) {
                        val read = input.read(buffer); if (read < 0) break
                        total += read; require(total <= 5 * 1024 * 1024) { "Ukuran foto maksimal 5 MB." }
                        output.write(buffer, 0, read)
                    }
                }
            }
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            require(bounds.outWidth > 0 && bounds.outHeight > 0) { "File foto tidak dapat dibaca." }
            return file.absolutePath
        } catch (e: Exception) { file.delete(); throw e }
    }
    fun show(image: ImageView, path: String): Boolean {
        if (path.isBlank() || !File(path).isFile) return false
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, options)
        if (options.outWidth <= 0 || options.outHeight <= 0) return false
        var sample = 1
        while (maxOf(options.outWidth, options.outHeight) / sample > 1200) sample *= 2
        options.inJustDecodeBounds = false; options.inSampleSize = sample
        val bitmap = BitmapFactory.decodeFile(path, options) ?: return false
        image.setImageBitmap(bitmap); return true
    }
}
