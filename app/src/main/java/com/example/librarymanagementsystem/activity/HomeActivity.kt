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

class HomeActivity : AppCompatActivity() {

    private lateinit var myBookBtn: Button
    private lateinit var favoriteBtn: Button
    private lateinit var profileBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        myBookBtn = findViewById(R.id.myBookBtn)
        favoriteBtn = findViewById(R.id.favoriteBtn)
        profileBtn = findViewById(R.id.profileBtn)

        myBookBtn.setOnClickListener {
            // Handle My Books button click
            val intent = Intent(this, ActivityBase2::class.java)
            intent.putExtra("PAGE_ID", "MYBOOK_ID")
            startActivity(intent)
            finish()
        }

        favoriteBtn.setOnClickListener {
            // Handle Favorites button click
            val intent = Intent(this, ActivityBase2::class.java)
            intent.putExtra("PAGE_ID", "MYFAVORITE_ID")
            startActivity(intent)
            finish()
        }

        profileBtn.setOnClickListener {
            // Handle Profile button click
            val intent = Intent(this, ActivityBase2::class.java)
            intent.putExtra("PAGE_ID", "PROFILE_ID")
            startActivity(intent)
            finish()
        }
        val categoryButtonContainer = findViewById<LinearLayout>(R.id.categoryButtonContainer)
        val categories = listOf("Fiction", "Detective", "Horror", "Romance", "Science")

        for (category in categories) {
            val button = Button(this).apply {
                text = category
                setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue))
                setTextColor(ContextCompat.getColor(context, R.color.light_purple))
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                // Thêm icon nếu muốn (setCompoundDrawablesWithIntrinsicBounds)
                // setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fiction, 0, 0)
                setPadding(16, 8, 16, 8)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 16
                }
                setOnClickListener {
                    // Xử lý sự kiện click button ở đây
                }
            }
            categoryButtonContainer.addView(button)
        }

    }
}