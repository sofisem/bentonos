package ru.bentonos


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.bentonos.network.CreateOrderResponseRemote
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson
import kotlinx.serialization.Serializable
import java.io.OutputStreamWriter

class CartActivity : AppCompatActivity() {

    private lateinit var priceTotal: TextView

    private lateinit var backButton: ImageButton
    private lateinit var titleTextView: TextView
    private lateinit var subtitleTextView: TextView
    private lateinit var quantityBento: TextView
    private lateinit var quantityBentoMaxi: TextView
    private lateinit var quantityTogo3: TextView
    private lateinit var quantityTogo6: TextView
    private lateinit var quantityTogo9: TextView
    private lateinit var quantityCombo2: TextView
    private lateinit var quantityCombo5: TextView
    private lateinit var priceBento: TextView
    private lateinit var priceBentoMaxi: TextView
    private lateinit var priceTogo3: TextView
    private lateinit var priceTogo6: TextView
    private lateinit var priceTogo9: TextView
    private lateinit var priceCombo2: TextView
    private lateinit var priceCombo5: TextView
    private lateinit var cardBento: LinearLayout
    private lateinit var cardBentoMaxi: LinearLayout
    private lateinit var cardTogo3: LinearLayout
    private lateinit var cardTogo6: LinearLayout
    private lateinit var cardTogo9: LinearLayout
    private lateinit var cardCombo2: LinearLayout
    private lateinit var cardCombo5: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private var cartItemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_activity)

        val createOrderButton: Button = findViewById(R.id.button_create_order)
        createOrderButton.setOnClickListener {
            val totalItems = CartState.items.values.sum()

            if (totalItems == 0) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            } else {
                sendOrder()
            }
        }

        // Инициализация всех UI элементов
        priceTotal = findViewById(R.id.price_total)

        cardBento = findViewById(R.id.item_bento)
        cardBentoMaxi = findViewById(R.id.item_bentomaxi)
        cardTogo3 = findViewById(R.id.item_togo3)
        cardTogo6 = findViewById(R.id.item_togo6)
        cardTogo9 = findViewById(R.id.item_togo9)
        cardCombo2 = findViewById(R.id.item_combo2)
        cardCombo5 = findViewById(R.id.item_combo5)

        backButton = findViewById(R.id.backButton)
        titleTextView = findViewById(R.id.title)
        subtitleTextView = findViewById(R.id.subtitle)
        quantityBento = findViewById(R.id.quantity_bento)
        quantityBentoMaxi = findViewById(R.id.quantity_bentomaxi)
        quantityTogo3 = findViewById(R.id.quantity_togo3)
        quantityTogo6 = findViewById(R.id.quantity_togo6)
        quantityTogo9 = findViewById(R.id.quantity_togo9)
        quantityCombo2 = findViewById(R.id.quantity_combo2)
        quantityCombo5 = findViewById(R.id.quantity_combo5)
        priceBento = findViewById(R.id.price_bento)
        priceBentoMaxi = findViewById(R.id.price_bentomaxi)
        priceTogo3 = findViewById(R.id.price_togo3)
        priceTogo6 = findViewById(R.id.price_togo6)
        priceTogo9 = findViewById(R.id.price_togo9)
        priceCombo2 = findViewById(R.id.price_combo2)
        priceCombo5 = findViewById(R.id.price_combo5)

        backButton.setOnClickListener {
            finish()
        }

        initializeQuantities()

        quantityBento.addTextChangedListener {
            updateCartState(quantityBento, "bento")
        }
        quantityBentoMaxi.addTextChangedListener {
            updateCartState(quantityBentoMaxi, "bento_maxi")
        }
        quantityTogo3.addTextChangedListener {
            updateCartState(quantityTogo3, "togo3")
        }
        quantityTogo6.addTextChangedListener {
            updateCartState(quantityTogo6, "togo6")
        }
        quantityTogo9.addTextChangedListener {
            updateCartState(quantityTogo9, "togo9")
        }
        quantityCombo2.addTextChangedListener {
            updateCartState(quantityCombo2, "combo2")
        }
        quantityCombo5.addTextChangedListener {
            updateCartState(quantityCombo5, "combo5")
        }
    }

    private fun initializeQuantities() {
        quantityBento.text = CartState.getItemCount("bento").toString()
        quantityBentoMaxi.text = CartState.getItemCount("bento_maxi").toString()
        quantityTogo3.text = CartState.getItemCount("togo3").toString()
        quantityTogo6.text = CartState.getItemCount("togo6").toString()
        quantityTogo9.text = CartState.getItemCount("togo9").toString()
        quantityCombo2.text = CartState.getItemCount("combo2").toString()
        quantityCombo5.text = CartState.getItemCount("combo5").toString()
        updateTotalPrice()
        updatePrice(quantityBento, priceBento)
        updatePrice(quantityBentoMaxi, priceBentoMaxi)
        updatePrice(quantityTogo3, priceTogo3)
        updatePrice(quantityTogo6, priceTogo6)
        updatePrice(quantityTogo9, priceTogo9)
        updatePrice(quantityCombo2, priceCombo2)
        updatePrice(quantityCombo5, priceCombo5)
    }

    private fun updateCartState(quantityTextView: TextView, itemName: String) {
        val newQuantity = quantityTextView.text.toString().replace("×", "").trim().toIntOrNull() ?: 0
        CartState.items[itemName] = newQuantity
        updatePrice(quantityTextView, getPriceTextViewForItem(itemName))
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val total = CartState.items.entries.sumOf { (itemName, quantity) ->
            val price = itemPrices[itemName] ?: 1400 // Если цена не найдена, использовать 1400
            quantity * price
        }
        priceTotal.text = "Сумма заказа: $total ₽"
    }

    private fun getPriceTextViewForItem(itemName: String): TextView {
        return when (itemName) {
            "bento" -> priceBento
            "bento_maxi" -> priceBentoMaxi
            "togo3" -> priceTogo3
            "togo6" -> priceTogo6
            "togo9" -> priceTogo9
            "combo2" -> priceCombo2
            "combo5" -> priceCombo5
            else -> priceBento
        }

    }
    private fun clearCart() {
        CartState.items.clear()

        sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putInt("cartItemCount", 0).apply()

        initializeQuantities() // Обновить UI
        CartState.save(this)
        Toast.makeText(this, "Корзина очищена", Toast.LENGTH_SHORT).show()
    }


    val itemPrices = mapOf(
        "bento" to 1400,
        "bento_maxi" to 2200,
        "togo3" to 1700,
        "togo6" to 3200,
        "togo9" to 4900,
        "combo2" to 2300,
        "combo5" to 3900
    )

    private fun updatePrice(quantityTextView: TextView, priceTextView: TextView) {
        val quantity = quantityTextView.text.toString().replace("×", "").trim().toIntOrNull() ?: 0

        val cardView = when (quantityTextView) {
            this.quantityBento -> cardBento
            this.quantityBentoMaxi -> cardBentoMaxi
            this.quantityTogo3 -> cardTogo3
            this.quantityTogo6 -> cardTogo6
            this.quantityTogo9 -> cardTogo9
            this.quantityCombo2 -> cardCombo2
            this.quantityCombo5 -> cardCombo5
            else -> null
        }

        if (quantity == 0) {
            cardView?.visibility = View.GONE
            return
        }

        cardView?.visibility = View.VISIBLE
        quantityTextView.text = "×$quantity"

        val itemName = when (quantityTextView) {
            this.quantityBento -> "bento"
            this.quantityBentoMaxi -> "bento_maxi"
            this.quantityTogo3 -> "togo3"
            this.quantityTogo6 -> "togo6"
            this.quantityTogo9 -> "togo9"
            this.quantityCombo2 -> "combo2"
            this.quantityCombo5 -> "combo5"
            else -> "bento" // Default item
        }

        val totalPrice = quantity * (itemPrices[itemName] ?: 1400)
        priceTextView.text = "$totalPrice ₽"
    }

    private fun sendOrder() {
        CoroutineScope(Dispatchers.IO).launch {
            try {


                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                val userLogin = sharedPreferences.getString("login", null)

                if (userLogin == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CartActivity, "Ошибка: не найден логин", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }



                val orderItems = CartState.items
                    .filter { it.value > 0 }
                    .map { entry ->
                        OrderItemReceiveRemote(
                            productName = entry.key,
                            quantity = entry.value.toString(),
                            price = "1400"
                        )
                    }


                val request = CreateOrderReceiveRemote(
                    userId = userLogin,
                    items = orderItems
                )

                val gson = Gson()
                val json = gson.toJson(request)


                val url = URL("http://10.0.2.2:8080/order/create") // для эмулятора Android
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(json.toString())
                writer.flush()
                writer.close()

                val responseCode = conn.responseCode

                if (responseCode == 201) {
                    val response = conn.inputStream.bufferedReader().use { it.readText() }
                    val orderResponse = gson.fromJson(response, CreateOrderResponseRemote::class.java)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CartActivity,
                            "Заказ успешно оформлен! Номер заказа: ${orderResponse.orderId}",
                            Toast.LENGTH_SHORT
                        ).show()

                        clearCart()

                        finish()
                    }
                } else {
                    val errorStream = conn.errorStream
                    val errorResponse = errorStream?.bufferedReader()?.use { it.readText() } ?: "Неизвестная ошибка"


                    Log.e("CartActivity", "Ошибка оформления заказа: $errorResponse")

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CartActivity,
                            "Ошибка оформления заказа: $errorResponse",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}

@Serializable
data class CreateOrderReceiveRemote(
    val userId: String,
    val items: List<OrderItemReceiveRemote>
)

@Serializable
data class OrderItemReceiveRemote(
    val productName: String,
    val quantity: String,
    val price: String
)

@Serializable
data class CreateOrderResponseRemote(
    val orderId: String
)