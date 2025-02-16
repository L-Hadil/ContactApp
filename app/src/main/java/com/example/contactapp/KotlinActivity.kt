package com.example.contactapp

import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences
    private val editTextFields = mutableMapOf<String, EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Charger la langue enregistrée
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("LANGUAGE", "fr") ?: "fr"
        setAppLocale(selectedLanguage)

        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

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
        val fields = listOf(
            FieldInfo("NOM", R.string.nom, R.string.hint_nom),
            FieldInfo("PRENOM", R.string.prenom, R.string.hint_prenom),
            FieldInfo("AGE", R.string.age, R.string.hint_age, InputType.TYPE_CLASS_NUMBER),
            FieldInfo("DOMAINE", R.string.domaine, R.string.hint_domaine),
            FieldInfo("TELEPHONE", R.string.telephone, R.string.hint_telephone, InputType.TYPE_CLASS_PHONE)
        )

        fields.forEach { field ->
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
        Button(this).apply {
            text = getString(R.string.valider)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
            }

            mainLayout.addView(this)
        }

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



    private fun showConfirmationDialog() {
        val message = buildString {
            append(getString(R.string.confirmation) + "\n\n")
            editTextFields.forEach { (key, editText) ->
                append("${getString(getStringResourceByKey(key))}: ${editText.text}\n")
            }
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirmation))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                saveContact()

                // **🔄 Redirection vers `RecapActivity` après validation**
                val intent = Intent(this, RecapActivity::class.java).apply {
                    putExtra("NOM", editTextFields["NOM"]?.text.toString())
                    putExtra("PRENOM", editTextFields["PRENOM"]?.text.toString())
                    putExtra("AGE", editTextFields["AGE"]?.text.toString())
                    putExtra("DOMAINE", editTextFields["DOMAINE"]?.text.toString())
                    putExtra("TELEPHONE", editTextFields["TELEPHONE"]?.text.toString())
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.retour), null)
            .show()
    }

    private fun saveContact() {
        val sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val contactsSet = sharedPreferences.getStringSet("contacts", mutableSetOf()) ?: mutableSetOf()

        val newContact = "${editTextFields["NOM"]?.text} ${editTextFields["PRENOM"]?.text} - ${editTextFields["TELEPHONE"]?.text}"
        contactsSet.add(newContact)

        editor.putStringSet("contacts", contactsSet)
        editor.apply()
    }

    private fun toggleLanguage() {
        val newLocale = if (resources.configuration.locale.language == "fr") {
            Locale("en")
        } else {
            Locale("fr")
        }

        setAppLocale(newLocale.language)
        recreate()
    }

    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        val editor = sharedPreferences.edit()
        editor.putString("LANGUAGE", language)
        editor.apply()
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
