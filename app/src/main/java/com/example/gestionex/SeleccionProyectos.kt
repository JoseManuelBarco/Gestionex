package com.example.gestionex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class SeleccionProyectos : AppCompatActivity() {
    private lateinit var proyectoslist: List<Projectos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionproyectos)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val gridLayoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = gridLayoutManager

        val key = intent.getStringExtra("Decrypt_Key")!!
        createProjectList(key)

        val adapter = ProjectoRecyclerView(proyectoslist) { proyecto ->
            val intent = Intent(this, SeleccioTareas::class.java)
            intent.putExtra("PROYECTO_ID", proyecto.proyectoID)
            intent.putExtra("projectName", proyecto.nombreProyecto)
            intent.putExtra("Decrypt_Key",key)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

    }
    private fun createProjectList(key: String) {
        val fileName = "proyectos.json"
        val file = File(filesDir, fileName)

        try {
            val encryptedText = file.bufferedReader().use { it.readText() }
            val decryptedJson = decryptAES(encryptedText, key)

            val gson = Gson()
            val projectsObjects = object : TypeToken<List<Projectos>>() {}.type
            proyectoslist = gson.fromJson(decryptedJson, projectsObjects)

        } catch (e: Exception) {
            e.printStackTrace()
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
