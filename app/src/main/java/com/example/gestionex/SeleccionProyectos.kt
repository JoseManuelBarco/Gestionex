package com.example.gestionex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader

class SeleccionProyectos : AppCompatActivity() {
    private lateinit var proyectoslist : List<Projectos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionproyectos)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val gridLayoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = gridLayoutManager

        loadJsonFromAssets()

        val adapter = ProjectoRecyclerView(proyectoslist) { proyecto ->
            val intent = Intent(this, SeleccioTareas::class.java)
            intent.putExtra("PROYECTO_ID", proyecto.proyectoID)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(BorderItemDecoration(this))

    }

    private fun loadJsonFromAssets() {
        val inputStream = this.assets.open("proyectos.json")
        val bufferedReader = BufferedReader(inputStream.reader())
        val jsonText = bufferedReader.use { it.readText() }

        val gson = Gson()
        val projectobject = object : TypeToken<List<Projectos>>() {}.type
        proyectoslist = gson.fromJson(jsonText, projectobject)
    }

}