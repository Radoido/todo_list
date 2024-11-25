package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.main.todo_list.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.login)

        val db = DAO(this)
        binding.btnLogin.setOnClickListener {
            val nome = binding.editTextNome.text.toString().trim()
            val senha = binding.editTextSenha.text.toString()

            if (nome.isNotBlank() && senha.isNotBlank()) {
                val funcionario = db.verificarLogin(nome, senha)

                if (funcionario != null) {
                    salvarLogin(funcionario)
                    val intent = Intent(this, Menu::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Nome de usuário e/ou senha incorreto!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }

        }

    }


        private fun salvarLogin(funcionario: Funcionario) {
            val sharedPreferences = getSharedPreferences("usuario_logado", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("id", funcionario.id)
            editor.putString("nome", funcionario.nome)
            editor.putString("cargo", funcionario.cargo)
            editor.apply()
        }






}