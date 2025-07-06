package com.example.librarymanagementsystem.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    @DocumentId
    val id: String? = null,
    var cover: String? = null,
    val title: String = "",
    val category: String = "",
    val summary: String? = null,
    var author: String? = null,
    val publishYear: Int? = null,
    val publisher: String? = null,
    val price: Int? = 0,
    var quantity: Int? = 0,
): Parcelable {
    constructor() : this(
        id = null,
        cover = null,
        title = "",
        category = "",
        summary = null,
        author = null,
        publishYear = null,
        publisher = null,
        price = 0,
        quantity = 0
    )
}