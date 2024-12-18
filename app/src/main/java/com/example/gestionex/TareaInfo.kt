package com.example.gestionex

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class TareaInfo : Fragment() {

    private lateinit var estadoSpinner: Spinner
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.tareainfo, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataBundle = arguments
        var tarea = dataBundle?.getParcelable<Tarea>("tarea")
        var key = dataBundle?.getString("Decrypt_Key")!!
        val editNombreTarea = view.findViewById<EditText>(R.id.editNombreTarea)
        val editPersonaAsignada = view.findViewById<TextView>(R.id.editPersonaAsignada)
        val editFechaInicio = view.findViewById<TextView>(R.id.editFechaInicio)
        val editFechaFin = view.findViewById<TextView>(R.id.editFechaFin)

        editNombreTarea.setText(tarea?.nombreTarea)
        editPersonaAsignada.text = tarea?.personaAsignada
        editFechaInicio.text = tarea?.fechaInicio
        editFechaFin.text = tarea?.fechaFin

        setDatePicker(editFechaInicio)
        setDatePicker(editFechaFin)

        estadoSpinner = view.findViewById(R.id.spinnerEstado)
        val estados = listOf("Assigned", "In Progress", "Complete")
        estadoSpinner.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, estados
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        estadoSpinner.setSelection(estados.indexOf(tarea?.estado))

        view.findViewById<RecyclerView>(R.id.recyclerViewSubtareas).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tarea?.subtareas?.let { subtareaadapter(it) }
        }

        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            if (tarea != null) {
                saveChanges(tarea,key)
            val taskselectedaFragment = activity?.findViewById<FragmentContainerView>(R.id.tareainfofragment)
                taskselectedaFragment?.visibility = View.GONE

            val taskrecyclerview = activity?.findViewById<RecyclerView>(R.id.recyclerView)
                taskrecyclerview?.adapter?.notifyDataSetChanged()
            }
        }

        view.findViewById<Button>(R.id.btnundochanges).setOnClickListener {
            val taskselectedaFragment = activity?.findViewById<FragmentContainerView>(R.id.tareainfofragment)
            taskselectedaFragment?.visibility = View.GONE
        }

    }

    private fun setDatePicker(textView: TextView) {
        textView.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                textView.text = dateFormat.format(calendar.time)
            },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun saveChanges( tarea : Tarea, key: String) {
        tarea.apply {
            nombreTarea = view?.findViewById<EditText>(R.id.editNombreTarea)?.text.toString()
            personaAsignada = view?.findViewById<TextView>(R.id.editPersonaAsignada)?.text.toString()
            estado = estadoSpinner.selectedItem.toString()
            fechaInicio = view?.findViewById<TextView>(R.id.editFechaInicio)?.text.toString()
            fechaFin = view?.findViewById<TextView>(R.id.editFechaFin)?.text.toString()
        }
        updateTaskInFile(tarea,key)
    }

    private fun updateTaskInFile(tarea: Tarea, key: String) {
        val file = File(requireContext().filesDir, "proyectos.json")
        if (!file.exists()) return

        try {
            val encryptedText = file.bufferedReader().use { it.readText() }
            val decryptedJson = decryptAES(encryptedText, key)

            val gson = Gson()
            val projectListType = object : TypeToken<List<Projectos>>() {}.type
            val proyectos = gson.fromJson<List<Projectos>>(decryptedJson, projectListType)

            val proyecto = proyectos.find { proyecto ->
                proyecto.tareas.any { it.tareaid == tarea.tareaid }
            }
            proyecto?.let { proyecto ->
                val taskIndex = proyecto.tareas.indexOfFirst { it.tareaid == tarea.tareaid }
                if (taskIndex != -1) {
                    proyecto.tareas[taskIndex] = tarea

                    val updatedJson = Gson().toJson(proyectos)

                    val encryptedJson = encryptAES(updatedJson, key)

                    file.writeText(encryptedJson)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @OptIn(ExperimentalEncodingApi::class)
    private fun decryptAES(encryptedText: String, key: String): String {
        try {
            val keyBytes = key.padEnd(32, ' ').substring(0, 32).toByteArray(Charsets.UTF_8)
            val iv = ByteArray(16)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            val secretKeySpec = SecretKeySpec(keyBytes, "AES")
            val ivParameterSpec = IvParameterSpec(iv)

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

            val encryptedBytes = Base64.decode(encryptedText)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error al desencriptar datos", e)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun encryptAES(plainText: String, key: String): String {
        try {
            val keyBytes = key.padEnd(32, ' ').substring(0, 32).toByteArray(Charsets.UTF_8)
            val iv = ByteArray(16)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            val secretKeySpec = SecretKeySpec(keyBytes, "AES")
            val ivParameterSpec = IvParameterSpec(iv)

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            return Base64.encode(encryptedBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error al encriptar datos", e)
        }
    }
}
