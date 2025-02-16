package com.example.contactapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecapActivity : AppCompatActivity() {
    private var phoneNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recap)

        val nom = intent.getStringExtra("NOM") ?: ""
        val prenom = intent.getStringExtra("PRENOM") ?: ""
        val age = intent.getStringExtra("AGE") ?: ""
        val domaine = intent.getStringExtra("DOMAINE") ?: ""
        phoneNumber = intent.getStringExtra("TELEPHONE") ?: ""

        findViewById<TextView>(R.id.tv_nom).text = "${getString(R.string.nom)}: $nom"
        findViewById<TextView>(R.id.tv_prenom).text = "${getString(R.string.prenom)}: $prenom"
        findViewById<TextView>(R.id.tv_age).text = "${getString(R.string.age)}: $age"
        findViewById<TextView>(R.id.tv_domaine).text = "${getString(R.string.domaine)}: $domaine"
        findViewById<TextView>(R.id.tv_telephone).text = "${getString(R.string.telephone)}: $phoneNumber"

        findViewById<Button>(R.id.btn_ok).setOnClickListener {

            Intent(this, CallPhoneActivity::class.java).apply {
                putExtra("TELEPHONE", phoneNumber)
                startActivity(this)
            }
        }

        findViewById<Button>(R.id.btn_retour).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

    }
}