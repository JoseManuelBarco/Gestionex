package com.example.gestionex

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class LoginActivity : AppCompatActivity() {
    private lateinit var userList : List<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.loginlayout)
        copyJsonUsersToInternalStorage()
        copyJsonProyectsToInternalStorage()
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val usernameEditText = findViewById<EditText>(R.id.UsernameLogin)
        val passwordEditText = findViewById<EditText>(R.id.PasswordLogin)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)
        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)
        val jsonpasswordTextView = findViewById<EditText>(R.id.JSONpasswordLogin)
        val jsonpasswordToggle = findViewById<ImageView>(R.id.JSONpasswordToggle)


        var isPasswordVisible = false

        var jsonPasswordisVisible = false

        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                passwordToggle.setImageResource(R.drawable.showpasswd)
            } else {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.hidepasswd)
            }

            passwordEditText.setSelection(passwordEditText.text.length)
        }
        jsonpasswordToggle.setOnClickListener {
            jsonPasswordisVisible = !jsonPasswordisVisible
            if (jsonPasswordisVisible) {
                jsonpasswordTextView.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                jsonpasswordToggle.setImageResource(R.drawable.showpasswd)
            } else {
                jsonpasswordTextView.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                jsonpasswordToggle.setImageResource(R.drawable.hidepasswd)
            }

            jsonpasswordTextView.setSelection(jsonpasswordTextView.text.length)
        }
        loginButton.setOnClickListener {
            val decryptPassword = jsonpasswordTextView.text.toString()
            try {
                createUserList(decryptPassword)

                var i = 0
                var check = false

                do {
                    if (userList[i].Correo == usernameEditText.text.toString() && BCrypt.checkpw(
                            passwordEditText.text.toString(),
                            userList[i].Contrasenya

                        )
                    ) {
                        check = true
                        errorTextView.visibility = View.INVISIBLE
                        val intent = Intent(this, SeleccionProyectos::class.java)
                        intent.putExtra("Decrypt_Key",decryptPassword)
                        startActivity(intent)
                        finish()
                    } else {
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = "CONTRASENYA I/O EMAIL INCORRECTES"
                    }
                    i++
                    if (i == userList.size) {
                        check = true
                    }
                } while (!check)
            } catch (e: Exception) {
                errorTextView.text = "Formato de los datos incorrecto o contraseña de desbloqueo incorrecta."
                errorTextView.visibility = View.VISIBLE
            }
        }
    }
    private fun createUserList(password: String) {
        val fileName = "usuarios.json"
        val file = File(filesDir, fileName)

            try {
                val encryptedText = file.bufferedReader().use { it.readText() }

                val decryptedJson = decryptAES(encryptedText, password)

                val gson = Gson()
                val userObjects = object : TypeToken<List<User>>() {}.type

                userList = gson.fromJson(decryptedJson, userObjects)
            } catch (e: Exception) {
                e.printStackTrace()
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

    @OptIn(ExperimentalEncodingApi::class)
    private fun decryptAES(encryptedText: String, key: String): String {
        try {
            val keyBytes = key.padEnd(32, ' ').substring(0, 32).toByteArray(Charsets.UTF_8)
            val iv = ByteArray(16)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            val secretKeySpec = SecretKeySpec(keyBytes, "AES")
            val ivParameterSpec = IvParameterSpec(iv)

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

            val encryptedBytes = Base64.decode(encryptedText)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error al desencriptar datos", e)
        }
    }
}

