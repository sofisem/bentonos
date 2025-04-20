package ru.bentonos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BentoActivity : AppCompatActivity() {

    private lateinit var cartCountText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var cartItemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bento_activity)


        sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE)
        cartItemCount = sharedPreferences.getInt("cartItemCount", 0)

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, CatalogActivity::class.java)
            startActivity(intent)
        }

        val orderButton: Button = findViewById(R.id.orderButton)
        cartCountText = findViewById(R.id.cartCount)
        updateCartCounter()

        orderButton.setOnClickListener {
            cartItemCount++

            CartState.addItem("bento")

            cartItemCount = CartState.getTotalItemCount()
            CartState.save(this)
            updateCartCounter()
            saveCartCount()
            Toast.makeText(this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
        }

        val cartButton: ImageView = findViewById(R.id.cartIcon)
        cartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }


    private fun updateCartCounter() {
        cartCountText.text = cartItemCount.toString()
        cartCountText.visibility = if (cartItemCount > 0) View.VISIBLE else View.INVISIBLE
    }


    private fun saveCartCount() {
        val editor = sharedPreferences.edit()
        editor.putInt("cartItemCount", cartItemCount)
        editor.apply()
    }
    override fun onResume() {
        super.onResume()
        cartItemCount = sharedPreferences.getInt("cartItemCount", 0)
        updateCartCounter()
    }



}
