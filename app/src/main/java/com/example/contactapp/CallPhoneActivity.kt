package com.example.contactapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CallPhoneActivity : AppCompatActivity() {
    private var phoneNumber: String = ""
    private val PERMISSION_CALL_PHONE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_phone)

        // Récupération du numéro de téléphone
        phoneNumber = intent.getStringExtra("TELEPHONE") ?: ""

        // Affichage du numéro
        findViewById<TextView>(R.id.tvPhoneNumber).text = "Numéro de téléphone : $phoneNumber"

        // Configuration du bouton d'appel
        findViewById<Button>(R.id.btnCall).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
                // Demander la permission si elle n'est pas accordée
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    PERMISSION_CALL_PHONE
                )
            } else {
                // Permission déjà accordée, effectuer l'appel
                makePhoneCall()
            }
        }

        // Configuration du bouton retour
        findViewById<Button>(R.id.btnRetour).setOnClickListener {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CALL_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission accordée
                    makePhoneCall()
                } else {
                    // Permission refusée
                    Toast.makeText(
                        this,
                        "Permission refusée pour effectuer l'appel téléphonique",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun makePhoneCall() {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Erreur lors de l'appel téléphonique",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}