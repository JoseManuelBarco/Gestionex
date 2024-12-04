package com.example.gestionex

class Tarea( val nombreTarea: String,
             val personaAsignada: String,
             val estado: String,
             val fechaInicio: String,
             val fechaFin: String,
             val subtareas: List<Subtarea>){
}