package ru.bentonos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ComboActivity : AppCompatActivity() {

    private lateinit var cartCountText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var cartItemCount: Int = 0

    // Spinner
    private lateinit var quantitySpinner: Spinner
    private var selectedQuantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.combo_acticity)
        val cartButton: ImageView = findViewById(R.id.cartIcon)
        cartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE)
        cartItemCount = sharedPreferences.getInt("cartItemCount", 0)


        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, CatalogActivity::class.java)
            startActivity(intent)
        }


        cartCountText = findViewById(R.id.cartCount)
        updateCartCounter()


        quantitySpinner = findViewById(R.id.quantitySpinner)


        val quantityOptions = listOf("2 шт", "5 шт")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, quantityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        quantitySpinner.adapter = adapter

        quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                selectedQuantity = quantityOptions[position].split(" ")[0].toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedQuantity = 2
            }
        }



        val orderButton: Button = findViewById(R.id.orderButton)
        orderButton.setOnClickListener {

            when (selectedQuantity) {
                2 -> CartState.addItem("combo2")
                5 -> CartState.addItem("combo5")

            }


            cartItemCount = CartState.getTotalItemCount()
            cartItemCount++ // только один набор
            CartState.save(this)
            updateCartCounter()
            saveCartCount()
            saveLastSelectedQuantity(selectedQuantity) // сохраняем что выбрано
            Toast.makeText(this, "Набор добавлен в корзину", Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveLastSelectedQuantity(quantity: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("lastSelectedQuantity", quantity)
        editor.apply()
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
