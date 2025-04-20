package ru.bentonos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CatalogActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cartCountText: TextView
    private var cartItemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catalog_activity)
        CartState.restore(this)


        sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE)
        cartItemCount = sharedPreferences.getInt("cartItemCount", 0)


        cartCountText = findViewById(R.id.cartCount)
        updateCartCounter()


        val itemBento = findViewById<View>(R.id.item_bento)
        val itemBentoMaxi = findViewById<View>(R.id.item_bento_maxi)
        val itemCakeToGo = findViewById<View>(R.id.item_cake_to_go)
        val itemCombo = findViewById<View>(R.id.item_combo)



        itemBento.setOnClickListener {
            Toast.makeText(this, "Бенто выбрано", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, BentoActivity::class.java)
            startActivity(intent)
        }

        itemBentoMaxi.setOnClickListener {
            Toast.makeText(this, "Бенто Макси выбрано", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MaxiActivity::class.java)
            startActivity(intent)
        }

        itemCakeToGo.setOnClickListener {
            Toast.makeText(this, "Cake To Go выбран", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TogoActivity::class.java)
            startActivity(intent)
        }

        itemCombo.setOnClickListener {
            Toast.makeText(this, "Комбо-набор выбран", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ComboActivity::class.java)
            startActivity(intent)

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
    override fun onResume() {
        super.onResume()
        cartItemCount = sharedPreferences.getInt("cartItemCount", 0)
        updateCartCounter()
    }


}
