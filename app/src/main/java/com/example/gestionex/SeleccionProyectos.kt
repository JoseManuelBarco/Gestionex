package com.example.gestionex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class SeleccionProyectos : AppCompatActivity() {
    private lateinit var proyectoslist: List<Projectos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionproyectos)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val gridLayoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = gridLayoutManager

        loadJsonFromInternalStorage()

        val adapter = ProjectoRecyclerView(proyectoslist) { proyecto ->
            val intent = Intent(this, SeleccioTareas::class.java)
            intent.putExtra("PROYECTO_ID", proyecto.proyectoID)
            intent.putExtra("projectName", proyecto.nombreProyecto)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

    }

    private fun loadJsonFromInternalStorage() {
        val fileName = "proyectos.json"
        val file = File(filesDir, fileName)
        try {
            val jsonText = file.bufferedReader().use { it.readText() }
            val gson = Gson()
            val projectObjectType = object : TypeToken<List<Projectos>>() {}.type
            proyectoslist = gson.fromJson(jsonText, projectObjectType)
        } catch (e: Exception) {
            e.printStackTrace()
            proyectoslist = emptyList()
        }
    }
}
