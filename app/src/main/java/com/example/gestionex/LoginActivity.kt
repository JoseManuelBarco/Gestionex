package com.example.gestionex

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LoginActivity : AppCompatActivity() {
    private lateinit var userList : List<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.loginlayout)
        copyJsonUsersToInternalStorage()
        copyJsonProyectsToInternalStorage()
        window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN      // Hides the status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)


                        val loginButton = findViewById<Button>(R.id.loginButton)
        val usernameEditText = findViewById<EditText>(R.id.UsernameLogin)
        val passwordEditText = findViewById<EditText>(R.id.PasswordLogin)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)

        createUserList()
        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateLogin(email, password)) {
                errorTextView.visibility = View.GONE
                startActivity(Intent(this, SeleccionProyectos::class.java)) // Replace with your target activity
                finish()
            } else {
                errorTextView.text = "Invalid username or password"
                errorTextView.visibility = View.VISIBLE
            }
        }

    }
    private fun createUserList() {
        val fileName = "usuarios.json"
        val file = File(filesDir, fileName)

        if (file.exists()) {
            try {
                val jsonText = file.bufferedReader().use { it.readText() }

                val gson = Gson()
                val userObjects = object : TypeToken<List<User>>() {}.type

                userList = gson.fromJson(jsonText, userObjects)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            userList = emptyList()
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        val encryptedInputPassword = AESUtils.encrypt(password)
        return userList.any { user ->
            user.Correo == email && user.Contrasenya == encryptedInputPassword
        }
    }
    object AESUtils {
        private const val SECRET_KEY = "1234567890123456"
        private const val IV = "6543210987654321"

        fun encrypt(input: String): String {
            require(SECRET_KEY.toByteArray().size == 16) { "Secret key must be 16 bytes long" }
            require(IV.toByteArray().size == 16) { "IV must be 16 bytes long" }

            val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(IV.toByteArray(Charsets.UTF_8))
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

            val outputStream = ByteArrayOutputStream()
            CipherOutputStream(outputStream, cipher).use { cipherStream ->
                cipherStream.write(input.toByteArray(Charsets.UTF_8)) // Encrypt input
            }
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT).trim() // Encode to Base64
        }
        }

    private fun copyJsonUsersToInternalStorage() {
        val fileName = "usuarios.json"
        val file = File(filesDir, fileName)

        if (!file.exists()) {
            try {
                val inputStream = assets.open(fileName)
                val outputStream = FileOutputStream(file)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun copyJsonProyectsToInternalStorage() {
        val fileName = "proyectos.json"
        val file = File(filesDir, fileName)

        if (!file.exists()) {
            try {
                val inputStream = assets.open(fileName)
                val outputStream = FileOutputStream(file)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}

