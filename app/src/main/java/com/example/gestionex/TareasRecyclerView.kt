package com.example.gestionex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class TareasRecyclerView(val tareas: List<Tarea> ,
                         val onClickListener: (Tarea) -> Unit
) : RecyclerView.Adapter<TareasRecyclerView.TareaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selecciontareasbox, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tareas = tareas[position]
        holder.bind(tareas)
        holder.itemView.setOnClickListener {
            onClickListener(tareas)
        }
    }

    override fun getItemCount(): Int {
        return tareas.size
    }

    class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tareatitleTextView: TextView = itemView.findViewById(R.id.titulotarea)
        private val tareanameTextView: TextView = itemView.findViewById(R.id.tareanombre)
        private val personaasignadaTitle: TextView = itemView.findViewById(R.id.personaassignadatitulo)
        private val personaasignadaTextView : TextView = itemView.findViewById(R.id.personaassignada)
        private val subtareasTitleTextView: TextView = itemView.findViewById(R.id.subtareastitle)
        private val estadoTitleTextView: TextView = itemView.findViewById(R.id.estadotitle)
        private val subtareasCountTextView: TextView = itemView.findViewById(R.id.numerotareas)
        private val estadoTextView:TextView = itemView.findViewById(R.id.estadotarea)

        fun bind(tareas: Tarea) {
            tareatitleTextView.text = "Nombre de la tarea"
            tareanameTextView.text = tareas.nombreTarea
            personaasignadaTitle.text = "Persona asignada"
            personaasignadaTextView.text = tareas.personaAsignada
            subtareasTitleTextView.text = "Sub-tareas"
            subtareasCountTextView.text= "${tareas.subtareas.size}"
            estadoTitleTextView.text = "Estado"
            estadoTextView.text= tareas.estado
        }
    }
}
