import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartState {
    val items = mutableMapOf(
        "bento" to 0,
        "bento_maxi" to 0,
        "togo3" to 0,
        "togo6" to 0,
        "togo9" to 0,
        "combo2" to 0,
        "combo5" to 0
    )


    // Добавление товара в корзину
    fun addItem(itemName: String) {
        items[itemName] = (items[itemName] ?: 0) + 1
    }

    // Получение общего количества товаров в корзине
    fun getTotalItemCount(): Int {
        return items.values.sum()
    }

    // Считывание количества конкретного товара в корзине
    fun getItemCount(itemName: String): Int {
        return items[itemName] ?: 0
    }

    // Очистка корзины
    fun clear() {
        for (key in items.keys) {
            items[key] = 0
        }
    }



    fun save(context: Context) {
        val prefs = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        val json = Gson().toJson(items)
        prefs.edit().putString("cartItems", json).apply()
    }


    fun restore(context: Context) {
        val prefs = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("cartItems", null)

        if (json != null) {
            val type = object : TypeToken<MutableMap<String, Int>>() {}.type
            val savedItems: MutableMap<String, Int> = Gson().fromJson(json, type)


            for (key in items.keys) {
                items[key] = savedItems[key] ?: 0
            }
        }
    }
}
