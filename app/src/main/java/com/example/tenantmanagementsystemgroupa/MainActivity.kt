package com.example.tenantmanagementsystemgroupa

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.example.tenantmanagementsystemgroupa.databinding.ActivityMainBinding

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private var lastTenant: Tenant? = null
    private val apartment = Apartment(101)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The login screen sends this email with an explicit Intent.
        intent.getStringExtra(EMAIL_EXTRA)?.let { email ->
            Toast.makeText(this, "Logged in as $email", Toast.LENGTH_SHORT).show()
        }

        binding.saveButton.setOnClickListener {
            val name = binding.tenantNameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val rentText = binding.rentEditText.text.toString().trim()
            var hasMissingField = false

            // Mark each empty input so the user can see which details are required.
            if (name.isEmpty()) {
                binding.tenantNameEditText.error = "Required"
                hasMissingField = true
            }
            if (phone.isEmpty()) {
                binding.phoneEditText.error = "Required"
                hasMissingField = true
            }
            if (rentText.isEmpty()) {
                binding.rentEditText.error = "Required"
                hasMissingField = true
            }
            if (hasMissingField) return@setOnClickListener

            val rentAmount = rentText.toDoubleOrNull()
            if (rentAmount == null || rentAmount < 0.0) {
                binding.rentEditText.error = "Enter a valid non-negative amount"
                return@setOnClickListener
            }

            val tenant = Tenant(name, apartment.apartmentNumber, phone, rentAmount)
            binding.tenant = tenant
            lastTenant = tenant
            apartment.addTenant(tenant)
            apartment.showTenants()

            // Clear the form after the saved tenant appears in the data-bound result.
            binding.tenantNameEditText.text.clear()
            binding.phoneEditText.text.clear()
            binding.rentEditText.text.clear()
        }

        binding.payRentButton.setOnClickListener {
            val tenant = lastTenant
            if (tenant == null) {
                Toast.makeText(this, "Save a tenant first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tenant.payRent()
            binding.invalidateAll()
        }

        binding.callButton.setOnClickListener {
            val tenant = lastTenant
            if (tenant == null) {
                Toast.makeText(this, "Save a tenant first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tenant.phone}")))
        }

        binding.shareButton.setOnClickListener {
            val tenant = lastTenant
            if (tenant == null) {
                Toast.makeText(this, "Save a tenant first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, tenant.summary())
            }
            startActivity(Intent.createChooser(shareIntent, "Share tenant"))
        }
    }

    companion object {
        const val EMAIL_EXTRA = "LOGIN_EMAIL"
    }
}
