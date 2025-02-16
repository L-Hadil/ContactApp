package com.example.contactapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

class XMLActivity : AppCompatActivity() {
    private lateinit var etNom: EditText
    private lateinit var etPrenom: EditText
    private lateinit var etAge: EditText
    private lateinit var etDomaine: EditText
    private lateinit var etTelephone: EditText
    private lateinit var btnValider: Button
    private lateinit var btnChangeLang: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupérer la langue sélectionnée
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("LANGUAGE", "fr") ?: "fr"
        setAppLocale(selectedLanguage)

        setContentView(R.layout.activity_xml)

        initializeViews()
        setupListeners()
        setupLanguageButton()
    }

    private fun initializeViews() {
        etNom = findViewById(R.id.etNom)
        etPrenom = findViewById(R.id.etPrenom)
        etAge = findViewById(R.id.etAge)
        etDomaine = findViewById(R.id.etDomaine)
        etTelephone = findViewById(R.id.etTelephone)
        btnValider = findViewById(R.id.btnValiderXML)
        btnChangeLang = findViewById(R.id.btnChangeLangXml)
    }

    private fun setupListeners() {
        btnValider.setOnClickListener { validateForm() }

        etTelephone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString()
                if (phone.isNotEmpty() && !phone.matches(Regex("^[0-9+ -]{8,}$"))) {
                    etTelephone.setBackgroundResource(R.drawable.edit_text_background_error)
                } else {
                    etTelephone.setBackgroundResource(R.drawable.edit_text_background)
                }
            }
        })
    }

    private fun setupLanguageButton() {
        btnChangeLang.apply {
            text = if (resources.configuration.locale.language == "fr") "English" else "Français"
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            setOnClickListener { toggleLanguage() }
        }
    }

    private fun validateForm() {
        var isValid = true
        var emptyFields = false

        val fields = listOf(
            etNom to R.string.nom,
            etPrenom to R.string.prenom,
            etAge to R.string.age,
            etDomaine to R.string.domaine,
            etTelephone to R.string.telephone
        )

        fields.forEach { (editText, _) ->
            if (editText.text.isNullOrBlank()) {
                editText.setBackgroundResource(R.drawable.edit_text_background_error)
                emptyFields = true
                isValid = false
            } else {
                editText.setBackgroundResource(R.drawable.edit_text_background)
            }
        }

        val age = etAge.text.toString().toIntOrNull()
        if (age == null || age < 0 || age > 120) {
            etAge.setBackgroundResource(R.drawable.edit_text_background_error)
            isValid = false
            Toast.makeText(this, "L'âge doit être entre 0 et 120", Toast.LENGTH_SHORT).show()
            return
        }

        if (emptyFields) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        if (isValid) {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        val message = buildString {
            append("Confirmez-vous les informations suivantes ?\n\n")
            append("${getString(R.string.nom)}: ${etNom.text}\n")
            append("${getString(R.string.prenom)}: ${etPrenom.text}\n")
            append("${getString(R.string.age)}: ${etAge.text}\n")
            append("${getString(R.string.domaine)}: ${etDomaine.text}\n")
            append("${getString(R.string.telephone)}: ${etTelephone.text}")
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage(message)
            .setPositiveButton("Confirmer") { _, _ ->
                saveContact()

                // Ouvrir `RecapActivity` après la validation
                val intent = Intent(this, RecapActivity::class.java).apply {
                    putExtra("NOM", etNom.text.toString())
                    putExtra("PRENOM", etPrenom.text.toString())
                    putExtra("AGE", etAge.text.toString())
                    putExtra("DOMAINE", etDomaine.text.toString())
                    putExtra("TELEPHONE", etTelephone.text.toString())
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun saveContact() {
        val sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val contactsSet = sharedPreferences.getStringSet("contacts", mutableSetOf()) ?: mutableSetOf()

        val newContact = "${etNom.text} ${etPrenom.text} - ${etTelephone.text}"
        contactsSet.add(newContact)

        editor.putStringSet("contacts", contactsSet)
        editor.apply()

        Toast.makeText(this, getString(R.string.contact_enregistre), Toast.LENGTH_SHORT).show()
    }

    private fun toggleLanguage() {
        val newLocale = if (resources.configuration.locale.language == "fr") "en" else "fr"

        sharedPreferences.edit().putString("LANGUAGE", newLocale).apply()

        setAppLocale(newLocale)

        recreate()
    }

    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
