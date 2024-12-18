package com.example.gestionex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class subtareaadapter (val subtarea: List<Subtarea> ,

) : RecyclerView.Adapter<subtareaadapter.SubTareaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTareaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return SubTareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubTareaViewHolder, position: Int) {
        val subtareas = subtarea[position]
        holder.bind(subtareas)
    }

    override fun getItemCount(): Int {
        return subtarea.size
    }

    class SubTareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(android.R.id.text1)
        private val personaasignadaTextView: TextView = itemView.findViewById(android.R.id.text2)


        fun bind(subtareas: Subtarea) {
            titleTextView.text = subtareas.nombreSubtarea
            personaasignadaTextView.text = subtareas.personaAsignada


        }
    }
}
