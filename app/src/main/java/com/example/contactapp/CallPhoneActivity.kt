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


        phoneNumber = intent.getStringExtra("TELEPHONE") ?: ""


        findViewById<TextView>(R.id.tvPhoneNumber).text = getString(R.string.numero_telephone, phoneNumber)


        findViewById<Button>(R.id.btnCall).setOnClickListener {
            checkCallPermission()
        }

        findViewById<Button>(R.id.btnRetour).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun checkCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                PERMISSION_CALL_PHONE
            )
        } else {

            makePhoneCall()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALL_PHONE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(this, getString(R.string.permission_refusee), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makePhoneCall() {
        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        } catch (e: SecurityException) {
            Toast.makeText(this, getString(R.string.erreur_appel), Toast.LENGTH_SHORT).show()
        }
    }
}
