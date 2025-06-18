package com.example.librarymanagementsystem.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.ErrorDialog
import com.example.librarymanagementsystem.fragment.AllBookFragment
import com.example.librarymanagementsystem.repository.BookRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

private const val ALLBOOK_ID = "ALLBOOK"
private const val RDCARD_ID = "RDCARD"
private const val ADD_RDCARD_ID = "ADD_RDCARD"

class LibrarianHomeActivity: AppCompatActivity() {
    //Initialize necessary variable
    private lateinit var auth: FirebaseAuth
    private lateinit var bookID: String
    private var currentFragment: Fragment? = null
    private lateinit var bookRepository: BookRepository
    private lateinit var errorDialog: ErrorDialog
    private lateinit var btnAllBook: Button
    private lateinit var btnRdcard: Button
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
            switchFragment(ALLBOOK_ID)
        }

        //Handle ReaderCard button
        btnRdcard.setOnClickListener {
            switchFragment(RDCARD_ID)
        }

        //Handle Add ReaderCard button
        btnAddRdcard.setOnClickListener {
            switchFragment(ADD_RDCARD_ID)
        }

        // Mặc định hiển thị AllBook
        if (savedInstanceState == null) {
            switchFragment(ALLBOOK_ID)
        }
    }

    private fun switchFragment(fragmentId: String) {
        // Kiểm tra nếu currentFragment là AllBookFragment và có cùng ID thì không làm gì
        if (currentFragment is AllBookFragment &&
            (currentFragment as AllBookFragment).getFragmentId() == fragmentId) {
            return
        }

        val fragment = AllBookFragment().apply {
            arguments = Bundle().apply {
                putString("FRAGMENT_ID", fragmentId) // Dùng putString thay vì putSerializable
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fgReaderCards, fragment)
            .addToBackStack(null)
            .commit()

        currentFragment = fragment
    }

}