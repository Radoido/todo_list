package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.main.todo_list.databinding.ActivityAluguelBinding

class Aluguel : AppCompatActivity() {
        private lateinit var binding: ActivityAluguelBinding
        private lateinit var adapterLivro: ArrayAdapter<Livro>
        private lateinit var adapterCliente: ArrayAdapter<Cliente>
        private lateinit var adapterAlugado: ArrayAdapter<Livro>
        private var clienteSelecionado: Cliente? = null
        private var livroSelecionado: Livro? = null
        private var devolucaoSelecionada: Livro? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAluguelBinding.inflate(layoutInflater)
        setContentView(binding.activityAluguel)

        val db = DAO(this)
        val listaLivros = db.livrosDisponiveis()
        val listaClientes = db.mostrarTodosClientes()
        val listaAlugados = db.livrosAlugados()


        adapterCliente = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaClientes)
        binding.searchCliente.adapter = adapterCliente

        binding.searchCliente.setOnItemClickListener { _, _, position, _ ->
            clienteSelecionado = adapterCliente.getItem(position)
            binding.editBuscaCliente.setText(clienteSelecionado?.nome)
        }

        adapterLivro = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLivros)
        binding.searchLivro.adapter = adapterLivro

        binding.searchLivro.setOnItemClickListener { _, _, position, _ ->
            livroSelecionado = adapterLivro.getItem(position)
            binding.editBuscaTitulo.setText(livroSelecionado?.titulo)
            binding.txtLivro.text = "Qual livro sera alugado?"

        }

        adapterAlugado = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaAlugados)
        binding.listAlugados.adapter = adapterAlugado

        binding.listAlugados.setOnItemClickListener { _, _, position, _ ->
            devolucaoSelecionada = adapterAlugado.getItem(position)
            binding.editBuscaTitulo.setText(devolucaoSelecionada?.titulo)
            binding.txtLivro.text = "Devolver livro: "
        }


        binding.btnAlugar.setOnClickListener(){
            // Verifique se o cliente e o livro foram selecionados
            if (clienteSelecionado != null && livroSelecionado != null) {
                val idCliente = clienteSelecionado!!.id
                val idLivro = livroSelecionado!!.id


                db.alugarLivro(idLivro, idCliente)
                db.atualizarStatusLivro(idLivro, 1)
                Toast.makeText(this, "Livro alugado com sucesso!", Toast.LENGTH_SHORT).show()

                listaLivros.clear()
                listaAlugados.clear()
                listaLivros.addAll(db.livrosDisponiveis())
                listaAlugados.addAll(db.livrosAlugados())
                adapterAlugado.notifyDataSetChanged()
                adapterLivro.notifyDataSetChanged()

            } else {
                Toast.makeText(this, "Por favor, selecione um cliente e um livro.", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnDevolver.setOnClickListener(){
            if (devolucaoSelecionada != null) {
                val livroDevolvido = devolucaoSelecionada
                val idAluguel = livroDevolvido!!.id

                db.atualizarStatusLivro(idAluguel, 0)
                db.devolveLivro(idAluguel)

                val listaLivrosDisponiveis = db.livrosDisponiveis()
                val listaLivrosAlugados = db.livrosAlugados()


                adapterLivro = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLivrosDisponiveis)
                binding.searchLivro.adapter = adapterLivro
                adapterAlugado = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLivrosAlugados)
                binding.listAlugados.adapter = adapterAlugado

                Toast.makeText(this, "Livro devolvido com sucesso!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Por favor, selecione um livro para devolver.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancelar.setOnClickListener(){
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }



    }
}