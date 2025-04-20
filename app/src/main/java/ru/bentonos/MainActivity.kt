package ru.bentonos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import ru.bentonos.network.RegisterReceiveRemote
import ru.bentonos.network.RegisterResponseRemote
import ru.bentonos.network.client

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val loginField: EditText = findViewById(R.id.login)
        val passwordField: EditText = findViewById(R.id.password)
        val checkBox: CheckBox = findViewById(R.id.check_agreement)
        val registerButton: Button = findViewById(R.id.btn_register)
        val loginLink: TextView = findViewById(R.id.login_link)

        registerButton.setOnClickListener {
            val login = loginField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val isChecked = checkBox.isChecked


            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isChecked) {
                Toast.makeText(this, "Подтвердите согласие с условиями", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            lifecycleScope.launch {
                try {
                    val response = client.post("http://10.0.2.2:8080/register") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            RegisterReceiveRemote(
                                login = login,
                                email = "$login@example.com",
                                password = password
                            )
                        )
                    }.body<RegisterResponseRemote>()

                    Toast.makeText(
                        this@MainActivity,
                        "Регистрация успешна! Токен: ${response.token}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Переход в каталог
                    startActivity(Intent(this@MainActivity, CatalogActivity::class.java))
                    finish()

                } catch (e: Exception) {
                    Log.e("RegisterError", "Ошибка регистрации", e)  // Лог ошибки в Logcat
                    Toast.makeText(
                        this@MainActivity,
                        "Ошибка регистрации: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
