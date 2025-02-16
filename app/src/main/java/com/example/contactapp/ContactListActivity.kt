package com.example.contactapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ContactListActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var contacts: MutableList<String>
    private lateinit var tvNoContacts: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE)
        val contactListView = findViewById<ListView>(R.id.contactListView)
        tvNoContacts = findViewById(R.id.tvNoContacts)

        // Charger les contacts
        contacts = loadContacts().toMutableList()
        updateNoContactsMessage()

        // Adapter pour afficher les contacts
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contacts)
        contactListView.adapter = adapter

        // Clic court pour appeler 📞
        contactListView.setOnItemClickListener { _, _, position, _ ->
            val selectedContact = contacts[position]
            val phoneNumber = selectedContact.substringAfter(" - ")
            makePhoneCall(phoneNumber)
        }

        // Appui long pour supprimer ❌
        contactListView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedContact = contacts[position]

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.supprimer_contact))
                .setMessage(getString(R.string.confirmation))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    removeContact(position, selectedContact)
                }
                .setNegativeButton(getString(R.string.retour), null)
                .show()

            true
        }
    }

    private fun loadContacts(): Set<String> {
        return sharedPreferences.getStringSet("contacts", mutableSetOf()) ?: emptySet()
    }

    private fun removeContact(position: Int, contact: String) {
        contacts.removeAt(position)
        adapter.notifyDataSetChanged()

        val editor = sharedPreferences.edit()
        val updatedContacts = sharedPreferences.getStringSet("contacts", mutableSetOf())?.toMutableSet()
        updatedContacts?.remove(contact)
        editor.putStringSet("contacts", updatedContacts)
        editor.apply()

        Toast.makeText(this, getString(R.string.contact_supprime), Toast.LENGTH_SHORT).show()
        updateNoContactsMessage()
    }

    private fun updateNoContactsMessage() {
        tvNoContacts.visibility = if (contacts.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permission_acceptee), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.permission_refusee), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
