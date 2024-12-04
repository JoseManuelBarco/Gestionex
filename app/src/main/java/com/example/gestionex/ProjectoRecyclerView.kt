package com.example.gestionex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectoRecyclerView  ( val proyectos: List<Projectos>,
 val onClickListener: (Projectos) -> Unit
) : RecyclerView.Adapter<ProjectoRecyclerView.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val proyecto = proyectos[position]
        holder.bind(proyecto)
        holder.itemView.setOnClickListener {
            onClickListener(proyecto)
        }
    }

    override fun getItemCount(): Int {
        return proyectos.size
    }

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(android.R.id.text1)
        private val descriptionTextView: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(proyecto: Projectos) {
            titleTextView.text = proyecto.nombreProyecto
            descriptionTextView.text = proyecto.descripcionProyecto
        }
    }
}
