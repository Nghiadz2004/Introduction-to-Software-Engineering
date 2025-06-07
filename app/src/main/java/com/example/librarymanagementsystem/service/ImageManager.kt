package com.example.librarymanagementsystem.service

import android.content.Context
import android.net.Uri
import com.cloudinary.utils.ObjectUtils
import com.example.librarymanagementsystem.configuration.CloudinaryConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageManager(private val context: Context) {

    suspend fun uploadImageToCloudinary(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
            ?: throw IllegalArgumentException("Unable to read bytes from URI")

        return withContext(Dispatchers.IO) {
            val config = context.applicationContext as CloudinaryConfiguration
            val result = config.cloudinary.uploader().upload(bytes, ObjectUtils.emptyMap())
            var imageUrl = result["url"] as String
            if (!imageUrl.startsWith("https://")) {
                imageUrl = imageUrl.replace("http://", "https://")
            }
            imageUrl
        }
    }
}
