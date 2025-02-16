package com.example.contactapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("LANGUAGE", "fr") ?: "fr"
        setAppLocale(selectedLanguage)

        setContentView(R.layout.activity_home)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnViewContacts = findViewById<Button>(R.id.btnViewContacts)
        val btnAddContact = findViewById<Button>(R.id.btnAddContact)

        // Met à jour le texte après le changement de langue
        tvWelcome.text = getString(R.string.bienvenue)
        btnViewContacts.text = getString(R.string.voir_contacts)
        btnAddContact.text = getString(R.string.ajouter_contact)

        btnViewContacts.setOnClickListener {
            startActivity(Intent(this, ContactListActivity::class.java))
        }

        btnAddContact.setOnClickListener {
            startActivity(Intent(this, XMLActivity::class.java))
        }
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
}
