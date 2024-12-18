package com.example.gestionex

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
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

class SeleccioTareas: AppCompatActivity() {
    private lateinit var tareaslist : List<Tarea>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleciontareas)

        val returnbuttonImageView: ImageView = findViewById(R.id.returnbutton)
        val proyectitleTextView: TextView = findViewById(R.id.nombreproyecto)
        val key = intent.getStringExtra("Decrypt_Key")!!


        proyectitleTextView.text = intent.getStringExtra("projectName")

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val gridLayoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = gridLayoutManager

        val proyectoID = intent.getStringExtra("PROYECTO_ID")
        if (proyectoID != null) {
            createTareasList(key,proyectoID)
            val adapter = TareasRecyclerView(tareaslist) { tarea ->
                onTareaClick(tarea, proyectoID,key)
            }
            recyclerView.adapter = adapter
        }
        returnbuttonImageView.setOnClickListener()
        {
                finish()
            }
    }
    private fun createTareasList(key: String,proyectoID: String) {
        val fileName = "proyectos.json"
        val file = File(filesDir, fileName)

        try {
            val encryptedText = file.bufferedReader().use { it.readText() }

            val decryptedJson = decryptAES(encryptedText, key)

            val gson = Gson()
            val projectListType = object : TypeToken<List<Projectos>>() {}.type
            val proyectos = gson.fromJson<List<Projectos>>(decryptedJson, projectListType)
            val selectedProyecto = proyectos.find { it.proyectoID == proyectoID }
            tareaslist = selectedProyecto?.tareas ?: emptyList()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun onTareaClick(tarea: Tarea, proyectoID: String, key:String) {
        val tareaFragment = TareaInfo()
        val bundle = Bundle()
        bundle.putParcelable("tarea", tarea)
        bundle.putString("proyectoID", proyectoID)
        bundle.putString("Decrypt_Key",key)
        tareaFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.tareainfofragment, tareaFragment)
            .addToBackStack(null)
            .commit()

        val fragmentDetails = findViewById<FragmentContainerView>(R.id.tareainfofragment)
        fragmentDetails.visibility = View.VISIBLE
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


