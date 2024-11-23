package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.main.todo_list.databinding.ActivityMenuBinding

class Menu : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMenuBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.menu)


        val db = DAO(this)

        binding.btnTeste.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnCliente.setOnClickListener {
            val intent = Intent(this, CreateCliente::class.java)
            startActivity(intent)

        }

        binding.btnLivro.setOnClickListener {
            val intent = Intent(this, CreateLivro::class.java)
            startActivity(intent)
        }

        binding.btnOut.setOnClickListener {
            val intent = Intent(this, Aluguel::class.java)
            startActivity(intent)
        }
    }
}