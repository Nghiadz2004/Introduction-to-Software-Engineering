package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.fragment.HomeFragment
import com.example.librarymanagementsystem.fragment.MyBookFragment
import com.google.firebase.auth.FirebaseAuth
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable

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

    private lateinit var readerId: String
    private lateinit var pageID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Lấy readerId từ Firebase nếu đã đăng nhập
        readerId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        pageID = intent.getStringExtra("PAGE_ID") ?: HOME_ID

        homeBtn = findViewById(R.id.homeBtn)
        myBookBtn = findViewById(R.id.myBookBtn)
        favoriteBtn = findViewById(R.id.favoriteBtn)
        profileBtn = findViewById(R.id.profileBtn)
        categoryButtonContainer = findViewById(R.id.categoryButtonContainer)

        loadActivity(pageID)
        handleMenuButton()
    }
    private fun handleMenuButton() {
        homeBtn.setOnClickListener {
            val drawableTop = AppCompatResources.getDrawable(this, R.drawable.house_solid)
            drawableTop?.let {
                val wrappedDrawable = DrawableCompat.wrap(drawableTop.mutate())
                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.light_blue))

                // Gán lại drawable sau khi đã chỉnh tint
                homeBtn.setCompoundDrawablesWithIntrinsicBounds(null, wrappedDrawable, null, null)
            }
            homeBtn.invalidate()
            pageID = HOME_ID
            categoryButtonContainer.removeAllViews()
            loadActivity(pageID)
        }

        myBookBtn.setOnClickListener {
            val drawableTop = AppCompatResources.getDrawable(this, R.drawable.book_icon)
            drawableTop?.let {
                val wrappedDrawable = DrawableCompat.wrap(drawableTop.mutate())
                DrawableCompat.setTint(
                    wrappedDrawable,
                    ContextCompat.getColor(this, R.color.light_blue)
                )

                // Gán lại drawable sau khi đã chỉnh tint
                myBookBtn.setCompoundDrawablesWithIntrinsicBounds(null, wrappedDrawable, null, null)
            }
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
                    ).apply {
                        marginEnd = 16
                    }

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
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MyBookFragment())
                .addToBackStack(null)
                .commit()

            val categoryButtonContainer = findViewById<LinearLayout>(R.id.categoryButtonContainer)
            val myBookSectionIcon = listOf(
                BORROWED_ID to R.drawable.all_books_icon,
                PENDING_ID to R.drawable.queues_icon,
                LOST_ID to R.drawable.lost_book_icon
            )

            // Kích thước mong muốn cho icon (ví dụ: 64x64 dp)
            val iconWidth = 24 // dp
            val iconHeight = 24 // dp

            // Chuyển đổi dp sang pixel
            val density = resources.displayMetrics.density
            val pixelWidth = (iconWidth * density).toInt()
            val pixelHeight = (iconHeight * density).toInt()

            // Tạo các nút phần sách của tôi động
            for ((myBookSectionName, drawableRes) in myBookSectionIcon) {
                val icon: Drawable? = ContextCompat.getDrawable(this, drawableRes)

                // Resize và tint icon
                val resizedAndTintedIcon: Drawable? = icon?.let {
                    val bitmap = createBitmap(pixelWidth, pixelHeight)
                    val canvas = Canvas(bitmap)
                    it.setBounds(0, 0, pixelWidth, pixelHeight) // Đặt giới hạn cho drawable gốc
                    it.draw(canvas) // Vẽ drawable gốc lên bitmap mới

                    val resizedDrawable = bitmap.toDrawable(resources)
                    val wrapped = DrawableCompat.wrap(resizedDrawable)
                    // Đặt màu tint cho icon đã resize
                    DrawableCompat.setTint(wrapped, ContextCompat.getColor(this, R.color.light_purple))
                    wrapped
                }

                val button = Button(this).apply {
                    text = myBookSectionName
                    setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue))
                    setTextColor(ContextCompat.getColor(context, R.color.light_purple))
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                    setPadding(16, 8, 16, 8)

                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 16
                    }

                    setCompoundDrawablesWithIntrinsicBounds(null, resizedAndTintedIcon, null, null)
                    compoundDrawablePadding = 12

                    setOnClickListener {
                        val fragment = MyBookFragment.newInstance(myBookSectionName)
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }

                categoryButtonContainer.addView(button)
            }
        }
    }
}