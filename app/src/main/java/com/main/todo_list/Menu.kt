package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.main.todo_list.databinding.ActivityMenuBinding

class Menu : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMenuBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.menu)


        val funcionario = recuperarLogin()

        if (funcionario != null) {
            // Exibir mensagem de boas-vindas
            binding.txtBemVindo.text = "Bem-vindo, ${funcionario.nome}"
            binding.txtCargo.text = "Cargo: ${funcionario.cargo}"

            // Controle de visibilidade baseado no cargo
            configurarCargo(funcionario.cargo)
        }

        // Botão de Logout
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun configurarCargo(tipo : String){

        if (tipo == "Admin") {
            // Configurar ações para os botões
            binding.btnCadCliente.setOnClickListener {
                val intent = Intent(this, CreateCliente::class.java)
                startActivity(intent)
                finish()
            }

            binding.btnCadLivro.setOnClickListener {
                val intent = Intent(this, CreateLivro::class.java)
                startActivity(intent)
                finish()
            }

            binding.btnListaAluguel.setOnClickListener {
                val intent = Intent(this, ListaAluguel::class.java)
                startActivity(intent)
            }

            binding.btnLivro.setOnClickListener {
                val intent = Intent(this, Aluguel::class.java)
                startActivity(intent)
                finish()
            }

        } else if (tipo == "Bibliotecário") {
            binding.btnCadCliente.visibility = View.GONE
            binding.btnCadLivro.visibility = View.GONE

            binding.btnListaAluguel.setOnClickListener {
                val intent = Intent(this, ListaAluguel::class.java)
                startActivity(intent)
            }

            binding.btnLivro.setOnClickListener {
                val intent = Intent(this, Aluguel::class.java)
                startActivity(intent)
                finish()
            }
        }

    }


    private fun recuperarLogin(): Funcionario? {
        val sharedPreferences = getSharedPreferences("usuario_logado", MODE_PRIVATE)
        val id = sharedPreferences.getInt("id", -1)  // -1 significa que não está logado
        val nome = sharedPreferences.getString("nome", null)
        val cargo = sharedPreferences.getString("cargo", null)

        return if (id != -1 && nome != null && cargo != null) {
            Funcionario(id, nome, "", cargo)
        } else {
            null
        }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("usuario_logado", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}