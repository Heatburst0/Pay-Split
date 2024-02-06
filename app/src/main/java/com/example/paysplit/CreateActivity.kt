package com.example.paysplit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.paysplit.databinding.ActivityCreateBinding

class CreateActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
    }
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCreate)
        binding.toolbarCreate.setNavigationIcon(R.drawable.ic_white_color_back_24dp)

        binding.toolbarCreate.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}