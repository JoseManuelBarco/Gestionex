package com.example.gestionex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader

class SeleccionProyectos : AppCompatActivity() {
    private lateinit var proyectoslist : List<Projectos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionproyectos)



        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = gridLayoutManager

        val adapter = ProjectoRecyclerView(proyectoslist) { _ ->
            val intent = Intent(this, SeleccionTareas::class.java)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }
    private fun loadJsonFromAssets() {
        val inputStream = this.assets.open("proyectos.json")
        val bufferedReader = BufferedReader(inputStream.reader())
        val jsonText = bufferedReader.use { it.readText() }

        val gson = Gson()
        val proyectoslist = object : TypeToken<List<Projectos>>() {}.type
    }
}