package com.example.contactapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Charger la langue enregistrée
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("LANGUAGE", "fr") ?: "fr"
        setAppLocale(selectedLanguage)

        setContentView(R.layout.activity_main)

        setupButtons()
        updateLanguageButtonText()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnVersionXML).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<Button>(R.id.btnVersionKotlin).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<Button>(R.id.btnChangeLang).setOnClickListener {
            toggleLanguage()
        }


    }

    private fun saveContact() {
        val etNom = findViewById<EditText>(R.id.etNom)
        val etPrenom = findViewById<EditText>(R.id.etPrenom)
        val etTelephone = findViewById<EditText>(R.id.etTelephone)

        val nom = etNom.text.toString().trim()
        val prenom = etPrenom.text.toString().trim()
        val telephone = etTelephone.text.toString().trim()

        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
            Toast.makeText(this, getString(R.string.erreur_contact), Toast.LENGTH_SHORT).show()
            return
        }

        // Sauvegarde du contact dans SharedPreferences
        val contactsPrefs = getSharedPreferences("contacts_prefs", MODE_PRIVATE)
        val editor = contactsPrefs.edit()
        val contacts = contactsPrefs.getStringSet("contacts", mutableSetOf())?.toMutableSet()
        contacts?.add("$nom $prenom - $telephone")
        editor.putStringSet("contacts", contacts)
        editor.apply()

        Toast.makeText(this, getString(R.string.contact_enregistre), Toast.LENGTH_SHORT).show()

        // Redirection vers RecapActivity
        val intent = Intent(this, RecapActivity::class.java)
        intent.putExtra("NOM", nom)
        intent.putExtra("PRENOM", prenom)
        intent.putExtra("TELEPHONE", telephone)
        startActivity(intent)
    }

    private fun updateLanguageButtonText() {
        findViewById<Button>(R.id.btnChangeLang).apply {
            text = if (resources.configuration.locale.language == "fr") "English" else "Français"
        }
    }

    private fun toggleLanguage() {
        val newLanguage = if (resources.configuration.locale.language == "fr") "en" else "fr"

        // Sauvegarde la langue sélectionnée
        sharedPreferences.edit().putString("LANGUAGE", newLanguage).apply()

        // Applique la nouvelle langue
        setAppLocale(newLanguage)

        // Redémarre l'activité pour appliquer les changements
        recreate()
    }

    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
