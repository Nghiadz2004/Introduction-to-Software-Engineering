package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.LibraryCard
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.example.librarymanagementsystem.model.Reader

class ReaderRepository(private val db: FirebaseFirestore) {

    suspend fun createReader(reader: Reader): String = withContext(Dispatchers.IO) {
        db.collection("readers").add(reader).await().id
    }

    suspend fun getReaderByUsername(username: String): Reader? = withContext(Dispatchers.IO) {
        db.collection("readers")
            .whereEqualTo("username", username)
            .get()
            .await()
            .toObjects(Reader::class.java)
            .firstOrNull()
    }

    suspend fun createLibraryCard(card: LibraryCard): String = withContext(Dispatchers.IO) {
        db.collection("library_cards").add(card).await().id
    }

    suspend fun getLibraryCardByReader(readerId: String): List<LibraryCard> = withContext(
        Dispatchers.IO) {
        db.collection("library_cards")
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(LibraryCard::class.java)
    }
}
