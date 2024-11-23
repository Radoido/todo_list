package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.main.todo_list.databinding.ActivityAluguelBinding

class Aluguel : AppCompatActivity() {
        private lateinit var binding: ActivityAluguelBinding
        private lateinit var adapterLivro: ArrayAdapter<Livro>
        private lateinit var adapterCliente: ArrayAdapter<Cliente>
        private var clienteSelecionado: Cliente? = null
        private var livroSelecionado: Livro? = null

        private var p: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAluguelBinding.inflate(layoutInflater)
        setContentView(binding.activityAluguel)

        val db = DAO(this)
        val listaLivros = db.livrosDisponiveis()
        val listaClientes = db.mostrarTodosClientes()

        adapterLivro = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLivros)
        binding.searchLivro.adapter = adapterLivro

        binding.searchLivro.setOnItemClickListener { _, _, position, _ ->
            livroSelecionado = adapterLivro.getItem(position) // Armazena o livro selecionado
            binding.editBuscaTitulo.setText(livroSelecionado?.titulo) // Mostra o título do livro no campo de texto
        }


        adapterCliente = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaClientes)
        binding.searchCliente.adapter = adapterCliente

        binding.searchCliente.setOnItemClickListener { _, _, position, _ ->
            clienteSelecionado = adapterCliente.getItem(position) // Armazena o cliente selecionado
            binding.editBuscaCliente.setText(clienteSelecionado?.nome) // Mostra o nome do cliente no campo de texto
        }



        binding.btnCancelar.setOnClickListener(){
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }



    }
}