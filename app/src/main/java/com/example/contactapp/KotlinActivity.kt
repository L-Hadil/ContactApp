package com.example.contactapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class KotlinActivity : AppCompatActivity() {
    private lateinit var mainLayout: LinearLayout
    private val editTextFields = mutableMapOf<String, EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ScrollView principal
        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Layout principal
        mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(48, 48, 48, 48)
        }

        createFormFields()
        createButtons()

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }

    private fun createFormFields() {
        // Structure des champs avec leurs propriétés
        val fields = listOf(
            FieldInfo("NOM", R.string.nom, R.string.hint_nom),
            FieldInfo("PRENOM", R.string.prenom, R.string.hint_prenom),
            FieldInfo("AGE", R.string.age, R.string.hint_age, InputType.TYPE_CLASS_NUMBER),
            FieldInfo("DOMAINE", R.string.domaine, R.string.hint_domaine),
            FieldInfo("TELEPHONE", R.string.telephone, R.string.hint_telephone, InputType.TYPE_CLASS_PHONE)
        )

        fields.forEach { field ->
            // Label
            TextView(this).apply {
                text = getString(field.labelResId)
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 16
                }
                mainLayout.addView(this)
            }

            // Champ de saisie
            EditText(this).apply {
                inputType = field.inputType
                hint = getString(field.hintResId)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
                minimumHeight = (48 * resources.displayMetrics.density).toInt()
                editTextFields[field.key] = this
                mainLayout.addView(this)
            }
        }
    }

    private fun createButtons() {
        // Bouton Valider
        Button(this).apply {
            text = getString(R.string.valider)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
            }
            setOnClickListener { validateForm() }
            mainLayout.addView(this)
        }

        // Bouton de changement de langue
        Button(this).apply {
            text = if (resources.configuration.locale.language == "fr") "English" else "Français"
            setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
            setTextColor(resources.getColor(android.R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
                gravity = Gravity.START
            }
            setOnClickListener { toggleLanguage() }
            mainLayout.addView(this)
        }
    }

    private fun validateForm() {
        var isValid = true

        // Vérification des champs vides
        editTextFields.forEach { (key, editText) ->
            if (editText.text.isNullOrBlank()) {
                editText.error = "Ce champ est requis"
                isValid = false
            }
        }

        // Vérification de l'âge
        editTextFields["AGE"]?.let {
            val age = it.text.toString().toIntOrNull()
            if (age == null || age < 0 || age > 120) {
                it.error = "L'âge doit être entre 0 et 120"
                isValid = false
                return
            }
        }

        if (isValid) {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        val message = buildString {
            append("Confirmez-vous les informations suivantes ?\n\n")
            editTextFields.forEach { (key, editText) ->
                append("${getString(getStringResourceByKey(key))}: ${editText.text}\n")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage(message)
            .setPositiveButton("Confirmer") { _, _ ->
                // Création de l'Intent avec les données
                Intent(this, RecapActivity::class.java).apply {
                    editTextFields.forEach { (key, editText) ->
                        putExtra(key, editText.text.toString())
                    }
                    startActivity(this)
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun toggleLanguage() {
        val newLocale = if (resources.configuration.locale.language == "fr") {
            Locale("en")
        } else {
            Locale("fr")
        }

        Locale.setDefault(newLocale)
        val config = Configuration(resources.configuration)
        config.setLocale(newLocale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }

    private fun getStringResourceByKey(key: String): Int {
        return when (key) {
            "NOM" -> R.string.nom
            "PRENOM" -> R.string.prenom
            "AGE" -> R.string.age
            "DOMAINE" -> R.string.domaine
            "TELEPHONE" -> R.string.telephone
            else -> throw IllegalArgumentException("Clé inconnue: $key")
        }
    }

    private data class FieldInfo(
        val key: String,
        val labelResId: Int,
        val hintResId: Int,
        val inputType: Int = InputType.TYPE_CLASS_TEXT
    )
}