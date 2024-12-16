package com.example.gestionex

import android.os.Parcel
import android.os.Parcelable

data class Tarea(var tareaid: String, var nombreTarea: String, var personaAsignada: String, var estado: String, var fechaInicio: String, var fechaFin: String, val subtareas: List<Subtarea>) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        mutableListOf<Subtarea>().apply {
            parcel.readList(this, Tarea::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tareaid)
        parcel.writeString(nombreTarea)
        parcel.writeString(personaAsignada)
        parcel.writeString(fechaInicio)
        parcel.writeString(fechaFin)
        parcel.writeString(estado)
        parcel.writeList(subtareas)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tarea> {
        override fun createFromParcel(parcel: Parcel): Tarea {
            return Tarea(parcel)
        }

        override fun newArray(size: Int): Array<Tarea?> {
            return arrayOfNulls(size)
        }
    }
}