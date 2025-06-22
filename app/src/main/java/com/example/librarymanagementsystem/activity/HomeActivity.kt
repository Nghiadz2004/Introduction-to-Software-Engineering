package com.example.librarymanagementsystem.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.cache.BookOperateCache
import com.example.librarymanagementsystem.cache.FavoriteCache
import com.example.librarymanagementsystem.cache.LibraryCardCache
import com.example.librarymanagementsystem.fragment.HomeFragment
import com.example.librarymanagementsystem.fragment.MyBookFragment
import com.example.librarymanagementsystem.fragment.SearchHome
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.service.UIService
import com.example.librarymanagementsystem.repository.FavoriteRepository
import com.example.librarymanagementsystem.repository.LibraryCardRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository
import kotlinx.coroutines.launch

// Home menu id
private const val HOME_ID = "HOME"
private const val MYBOOK_ID = "MYBOOK"
private const val MYFAVORITE_ID = "MYFAVORITE"
private const val PROFILE_ID = "PROFILE"

// My book section id
private const val BORROWED_ID = "BORROWED"
private const val PENDING_ID = "PENDING"
private const val LOST_ID = "LOST"

class HomeActivity : AppCompatActivity() {

    private lateinit var homeBtn: Button
    private lateinit var myBookBtn: Button
    private lateinit var favoriteBtn: Button
    private lateinit var profileBtn: Button
    private lateinit var categoryButtonContainer: LinearLayout
    private lateinit var searchET: EditText

    private val bookRepository = BookRepository()
    private lateinit var pageID: String
    private lateinit var readerId: String

    private val favoriteRepository = FavoriteRepository()
    private val libraryCardRepository = LibraryCardRepository()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        readerId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        pageID = intent.getStringExtra("PAGE_ID") ?: HOME_ID
        lifecycleScope.launch {
            val libraryCard = libraryCardRepository.getLatestLibraryCard(readerId)
            if (libraryCard != null) {
                val pendingBorrowList = RequestBorrowRepository().getReaderPendingRequests(readerId)
                val pendingMap = pendingBorrowList.associate { it.bookId to "PENDING" }

                val borrowingList  = BorrowingRepository().getBorrowBooksByCard(libraryCard.id!!)
                val borrowingMap = borrowingList.associate { it.bookId!! to "BORROWING" }

                BookOperateCache.statusMap.putAll(borrowingMap)
                BookOperateCache.statusMap.putAll(pendingMap)
            }

            val favBookList = favoriteRepository.getFavoriteBooksId(readerId)
            FavoriteCache.favoriteBookIds =
                favBookList?.bookIdList?.toSet()?.toMutableSet() ?: mutableSetOf()
            LibraryCardCache.libraryCard = libraryCard
        }

        // Ánh xạ view
        homeBtn = findViewById(R.id.homeBtn)
        myBookBtn = findViewById(R.id.myBookBtn)
        favoriteBtn = findViewById(R.id.favoriteBtn)
        profileBtn = findViewById(R.id.profileBtn)
        categoryButtonContainer = findViewById(R.id.categoryButtonContainer)
        searchET = findViewById(R.id.searchET)
        val addBookBtn = findViewById<Button>(R.id.addBookBtn)
        addBookBtn.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }
        loadActivity(pageID)
        handleMenuButton()

        // Xử lý khi nhấn "Enter"/"Search"
        searchET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val keyword = searchET.text.toString().trim()
                if (keyword.isNotEmpty()) performSearch(keyword)
                true
            } else false
        }

        // Xử lý khi chạm vào icon search (drawableEnd)
        searchET.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = 2 // vị trí drawableEnd
                if (event.rawX >= (searchET.right - searchET.compoundDrawables[drawableRight].bounds.width())) {
                    val keyword = searchET.text.toString().trim()
                    if (keyword.isNotEmpty()) performSearch(keyword)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun performSearch(keyword: String) {
        // Ẩn bàn phím
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchET.windowToken, 0)

        lifecycleScope.launch {
            try {
                val searchResults: List<Book> = bookRepository.searchBooksByKeyword(keyword)

                if (searchResults.isEmpty()) {
                    Toast.makeText(this@HomeActivity, "Không tìm thấy sách phù hợp.", Toast.LENGTH_SHORT).show()
                }
                val fragment = SearchHome.newInstance(searchResults)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@HomeActivity, "Lỗi tìm kiếm: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleMenuButton() {
        homeBtn.setOnClickListener {
            UIService.setButtonColor(
                this@HomeActivity,
                homeBtn,
                listOf(myBookBtn)
            )

            homeBtn.invalidate()
            pageID = HOME_ID
            categoryButtonContainer.removeAllViews()
            loadActivity(pageID)
        }

        myBookBtn.setOnClickListener {
            UIService.setButtonColor(
                this@HomeActivity,
                myBookBtn,
                listOf(homeBtn)
            )

            myBookBtn.invalidate()
            pageID = MYBOOK_ID
            categoryButtonContainer.removeAllViews()
            loadActivity(pageID)
        }

        favoriteBtn.setOnClickListener {
            // Handle Favorites button click
            val intent = Intent(this, ActivityBase2::class.java)
            intent.putExtra("PAGE_ID", MYFAVORITE_ID)
            startActivity(intent)
            finish()
        }

        profileBtn.setOnClickListener {
            // Handle Profile button click
            val intent = Intent(this, ActivityBase2::class.java)
            intent.putExtra("PAGE_ID", PROFILE_ID)
            startActivity(intent)
            finish()
        }
    }

    private fun loadActivity(pageID: String) {
        if (pageID == HOME_ID) {
            UIService.setButtonColor(
                this@HomeActivity,
                homeBtn,
                listOf(myBookBtn)
            )

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragment())
                .addToBackStack(null)
                .commit()
            val categoryButtonContainer = findViewById<LinearLayout>(R.id.categoryButtonContainer)
            val categoryIcons = listOf(
                "Art & Photography" to R.drawable.art_icon,
                "Biographies & Memoirs" to R.drawable.biography_icon,
                "Business & Economics" to R.drawable.economy,
                "How-to - Self Help" to R.drawable.self_help,
                "Education - Teaching" to R.drawable.education_icons,
                "Fiction - Literature" to R.drawable.fiction_icons,
                "Magazines" to R.drawable.magazines_icons,
                "Medical Books" to R.drawable.medical,
                "Science - Technology" to R.drawable.science,
                "History, Politics & Social Sciences" to R.drawable.history_icons,
                "Travel & Holiday" to R.drawable.travel,
                "Cookbooks, Food & Wine" to R.drawable.cook
            )

            categoryButtonContainer.removeAllViews()

            for ((categoryName, drawableRes) in categoryIcons) {
                val button = Button(this).apply {
                    text = categoryName
                    setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue))
                    setTextColor(ContextCompat.getColor(context, R.color.light_purple))
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                    setPadding(16, 8, 16, 8)

                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { marginEnd = 16 }

                    // Gán icon vào bên trái của text
                    val drawableLeft = ContextCompat.getDrawable(context, drawableRes)
                    setCompoundDrawablesWithIntrinsicBounds(null, drawableLeft, null, null)
                    compoundDrawablePadding = 12

                    setOnClickListener {
                        // Xử lý khi click
                    }
                }

                categoryButtonContainer.addView(button)
            }
        }
        else if (pageID == MYBOOK_ID) {
            // Change My Book menu button color
            UIService.setButtonColor(
                this@HomeActivity,
                myBookBtn,
                listOf(homeBtn)
            )

            // Fragment transaction
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MyBookFragment())
                .addToBackStack(null)
                .commit()

            categoryButtonContainer.removeAllViews()

            val myBookSectionIcon = listOf(
                BORROWED_ID to R.drawable.all_books_icon,
                PENDING_ID to R.drawable.queues_icon,
                LOST_ID to R.drawable.lost_book_icon
            )

            val density = resources.displayMetrics.density
            val pixelSize = (24 * density).toInt()
            val sectionButtons = mutableListOf<Button>()

            for ((sectionName, drawableRes) in myBookSectionIcon) {
                val icon: Drawable? = ContextCompat.getDrawable(this, drawableRes)
                val bitmap = Bitmap.createBitmap(pixelSize, pixelSize, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                icon?.setBounds(0, 0, canvas.width, canvas.height)
                icon?.draw(canvas)

                val tinted = DrawableCompat.wrap(bitmap.toDrawable(resources))
                DrawableCompat.setTint(tinted, ContextCompat.getColor(this, R.color.light_purple))

                val button = Button(this).apply {
                    if (sectionName == BORROWED_ID) {
                        UIService.setButtonColor(
                            this@HomeActivity,
                            this@apply,  // selected button
                            selectedColorResId = R.color.pink,
                        )
                    }

                    text = sectionName
                    setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue))
                    setTextColor(ContextCompat.getColor(context, R.color.light_purple))
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                    setPadding(16, 8, 16, 8)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { marginEnd = 16 }

                    setCompoundDrawablesWithIntrinsicBounds(null, tinted, null, null)
                    compoundDrawablePadding = 12

                    setOnClickListener {
                        UIService.setButtonColor(
                            this@HomeActivity,
                            this@apply,  // selected button
                            sectionButtons.filterNot { it == this@apply },
                            R.color.pink,
                            R.color.light_purple
                        )

                        val fragment = MyBookFragment.newInstance(sectionName)
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }

                sectionButtons.add(button)
                categoryButtonContainer.addView(button)
            }
        }
    }
}
