package com.example.librarymanagementsystem.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.ErrorDialog
import com.example.librarymanagementsystem.fragment.AllBookFragment
import com.example.librarymanagementsystem.fragment.ProfileFragment
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch

class LibrarianHomeActivity: AppCompatActivity() {
    //Initialize necessary variable
    private lateinit var auth: FirebaseAuthException
    private lateinit var bookID: String
    private lateinit var bookRepository: BookRepository
    private lateinit var errorDialog: ErrorDialog
    private lateinit var btnAllBook: AppCompatImageButton
    private lateinit var btnRdcard: AppCompatImageButton
    private lateinit var btnAddRdcard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_book)

        // Initialize book repository
        bookRepository = BookRepository()

        // Initialize error dialog
        errorDialog = ErrorDialog(this, "Error")

        // Initialize necessary variables
        btnAllBook = findViewById(R.id.mnbAllBookBtn)
        btnRdcard = findViewById(R.id.mnbRdcardBtn)
        btnAddRdcard = findViewById(R.id.mnbAddRdcardBtn)

        //Handle all book button
        btnAllBook.setOnClickListener {
            //Handle all book button
            supportFragmentManager.beginTransaction()
                .replace(R.id.lib_fragment_container, AllBookFragment())
                .addToBackStack(null)
                .commit()
        }

        //Handle ReaderCard button
        btnRdcard.setOnClickListener {
            //Handle ReaderCard button
            supportFragmentManager.beginTransaction()
                .replace(R.id.lib_fragment_container, AllBookFragment())
                .addToBackStack(null)
                .commit()
        }

        //Handle Add ReaderCard button
        btnAddRdcard.setOnClickListener {
            //Handle Add ReaderCard button
            supportFragmentManager.beginTransaction()
                .replace(R.id.lib_fragment_container, AllBookFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}