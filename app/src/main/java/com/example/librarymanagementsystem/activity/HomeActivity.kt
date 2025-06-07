package com.example.librarymanagementsystem.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.fragment.HomeFragment
import com.google.firebase.auth.FirebaseAuth

private const val HOME_ID = "HOME"
private const val MYBOOK_ID = "MYBOOK"
private const val MYFAVORITE_ID = "MYFAVORITE"
private const val PROFILE_ID = "PROFILE"
class HomeActivity : AppCompatActivity() {

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
        myBookBtn = findViewById(R.id.myBookBtn)
        favoriteBtn = findViewById(R.id.favoriteBtn)
        profileBtn = findViewById(R.id.profileBtn)
        categoryButtonContainer = findViewById(R.id.categoryButtonContainer)

        loadActivity(pageID)
        handleMenuButton()
    }
    private fun handleMenuButton() {
//        myBookBtn.setOnClickListener {
//            val intent = Intent(this, ActivityBase2::class.java)
//            intent.putExtra("PAGE_ID", MYBOOK_ID)
//            startActivity(intent)
//            finish()
//        }

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
    }
}