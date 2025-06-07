package com.example.librarymanagementsystem.configuration

import android.app.Application
import com.cloudinary.android.MediaManager
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

class CloudinaryConfiguration : Application() {

    lateinit var cloudinary: Cloudinary

    override fun onCreate() {
        super.onCreate()

        val config = hashMapOf(
            "cloud_name" to "dvv00sb4g",
            "api_key" to "238231141862436",
            "api_secret" to "9y9gncqOU8RI_F2iW0y5hV3qcw8",
            "secure" to true

        )
        MediaManager.init(this, config)

        cloudinary = Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dvv00sb4g",
            "api_key", "238231141862436",
            "api_secret", "9y9gncqOU8RI_F2iW0y5hV3qcw8"
        ))
    }
}