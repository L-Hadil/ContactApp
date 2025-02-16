package com.example.contactapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtons()
        updateLanguageButtonText()
    }

    private fun setupButtons() {
        // Configuration des boutons de navigation
        findViewById<Button>(R.id.btnVersionXML).apply {
            setOnClickListener {
                startActivity(Intent(context, XMLActivity::class.java))
            }
        }

        findViewById<Button>(R.id.btnVersionKotlin).apply {
            setOnClickListener {
                startActivity(Intent(context, KotlinActivity::class.java))
            }
        }

        // Configuration du bouton de changement de langue
        findViewById<Button>(R.id.btnChangeLang).apply {
            setOnClickListener {
                toggleLanguage()
            }
        }
    }

    private fun updateLanguageButtonText() {
        findViewById<Button>(R.id.btnChangeLang).apply {
            text = if (resources.configuration.locale.language == "fr") "English" else "Français"
            setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
            setTextColor(resources.getColor(android.R.color.white))
        }
    }

    private fun toggleLanguage() {
        // Obtenir la configuration actuelle
        val configuration = resources.configuration

        // Déterminer la nouvelle locale
        val newLocale = if (configuration.locale.language == "fr") {
            Locale("en")
        } else {
            Locale("fr")
        }

        // Mettre à jour la configuration
        Locale.setDefault(newLocale)
        val config = Configuration(configuration)
        config.setLocale(newLocale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Recréer l'activité
        recreate()
    }
}