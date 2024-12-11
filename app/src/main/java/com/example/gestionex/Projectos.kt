package com.example.gestionex

import com.github.mikephil.charting.data.PieEntry

class Projectos( val proyectoID: String,
                 val nombreProyecto: String,
                 val descripcionProyecto: String,
                 val tareas: List<Tarea>,
              ) {
}