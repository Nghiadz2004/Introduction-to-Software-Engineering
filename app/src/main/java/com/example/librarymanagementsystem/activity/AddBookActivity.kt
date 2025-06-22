package com.example.librarymanagementsystem.activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.Book
import com.google.firebase.firestore.FirebaseFirestore
class AddBookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_newbooks) // Gắn layout

        // Khai báo các EditText và Button
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val authorEditText = findViewById<EditText>(R.id.authorEditText)
        val publisherEditText = findViewById<EditText>(R.id.publisherEditText)
        val publishYearEditText = findViewById<EditText>(R.id.publishDateEditText)
        val quantityEditText = findViewById<EditText>(R.id.copyNumEditText)
        val priceEditText = findViewById<EditText>(R.id.totalPagesEditText)
        val categoryEditText = findViewById<EditText>(R.id.seriesEditText)
        val summaryEditText = findViewById<EditText>(R.id.ratingEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Xử lý khi người dùng nhấn nút "ADD"
        submitButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val author = authorEditText.text.toString().trim()
            val publisher = publisherEditText.text.toString().trim()
            val publishYear = publishYearEditText.text.toString().trim().toIntOrNull()
            val quantity = quantityEditText.text.toString().trim().toIntOrNull()
            val price = priceEditText.text.toString().trim().toIntOrNull()
            val category = categoryEditText.text.toString().trim()
            val summary = summaryEditText.text.toString().trim()

            if (title.isEmpty() || publishYear == null || quantity == null || price == null) {
                Toast.makeText(this, "Please fill all required fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newBook = Book(
                title = title,
                author = author,
                publisher = publisher,
                publishYear = publishYear,
                quantity = quantity,
                price = price,
                category = category,
                summary = summary
            )

            FirebaseFirestore.getInstance()
                .collection("books")
                .add(newBook)
                .addOnSuccessListener {
                    Toast.makeText(this, "Book added successfully!", Toast.LENGTH_SHORT).show()
                    // Reset form nếu muốn
                    titleEditText.setText("")
                    authorEditText.setText("")
                    publisherEditText.setText("")
                    publishYearEditText.setText("")
                    quantityEditText.setText("")
                    priceEditText.setText("")
                    categoryEditText.setText("")
                    summaryEditText.setText("")
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add book: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}