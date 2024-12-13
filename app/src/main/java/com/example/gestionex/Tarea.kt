package com.example.gestionex

class Tarea(var tareaid: String,
            var nombreTarea: String,
            var personaAsignada: String,
            var estado: String,
            var fechaInicio: String,
            var fechaFin: String,
            val subtareas: List<Subtarea>){
}