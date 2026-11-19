package ua.tft.wishlist.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.text.NumberFormat
import java.util.Locale

object ImageStorage {
    fun saveImageToInternalStorage(context: Context, sourceUri: Uri): String {
        return try {
            val fileName = "wish_${System.currentTimeMillis()}.jpg"
            val destFile = File(context.filesDir, fileName)
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            ""
        }
    }
}

fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("uk", "UA")).apply {
        maximumFractionDigits = if (price % 1.0 == 0.0) 0 else 2
    }
    return "${formatter.format(price)} ₴"
}