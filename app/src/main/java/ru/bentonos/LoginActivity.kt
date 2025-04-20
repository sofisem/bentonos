package ru.bentonos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import ru.bentonos.network.LoginReceiveRemote
import ru.bentonos.network.LoginResponseRemote

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val emailInput = findViewById<EditText>(R.id.login)
        val passwordInput = findViewById<EditText>(R.id.password_login)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val registerLink = findViewById<TextView>(R.id.login_link)

        loginButton.setOnClickListener {
            val login = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // запуск корутины
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: LoginResponseRemote = client.post("http://10.0.2.2:8080/login") {
                        contentType(ContentType.Application.Json)
                        setBody(LoginReceiveRemote(login, password))
                    }.body()

                    withContext(Dispatchers.Main) {
                        // Сохраняем токен
                        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        sharedPreferences.edit().apply {
                            putString("token", response.token)
                            putString("login", login)  // Сохраняем логин
                            apply()
                        }

                        Toast.makeText(this@LoginActivity, "Вход выполнен", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, CatalogActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}
