package com.main.todo_list

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.main.todo_list.databinding.ActivityCreateClienteBinding

class CreateCliente : AppCompatActivity() {
    private lateinit var binding: ActivityCreateClienteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClienteBinding.inflate(layoutInflater)
        setContentView(binding.createCliente)

    }
}