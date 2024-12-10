package com.example.gestionex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ProjectoRecyclerView  ( val proyectos: List<Projectos>,
 val onClickListener: (Projectos) -> Unit
) : RecyclerView.Adapter<ProjectoRecyclerView.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_view, parent, false)
        return ProjectViewHolder(view)

    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val proyecto = proyectos[position]
        holder.bind(proyecto)
        holder.itemView.setOnClickListener {
            onClickListener(proyecto)
        }
    }

    override fun getItemCount(): Int = proyectos.size

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.projectNameText)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.projectDescriptionText)
        private val pieChart: PieChart = itemView.findViewById(R.id.piechart)

        fun bind(proyecto: Projectos) {
            titleTextView.text = proyecto.nombreProyecto
            descriptionTextView.text = proyecto.descripcionProyecto

            setupPieChart(proyecto)
        }
        private fun setupPieChart(proyecto: Projectos) {
            val estadosCount = proyecto.tareas.groupingBy { it.estado }.eachCount()

            val entries = estadosCount.map { (estado, count) ->
                PieEntry(count.toFloat(), estado)
            }
            val pieDataSet = PieDataSet(entries, "Task States").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList() // Use predefined colors
                valueTextSize=14f
            }

            val pieData = PieData(pieDataSet)

            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.setDrawEntryLabels(false)
            pieChart.centerText = "Task States"
            pieChart.legend.isEnabled = true
            pieChart.invalidate()

        }
    }
}
