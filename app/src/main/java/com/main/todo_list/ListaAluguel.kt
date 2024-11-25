package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.main.todo_list.databinding.ActivityListaAluguelBinding

class ListaAluguel : AppCompatActivity() {

    private lateinit var binding: ActivityListaAluguelBinding
    private lateinit var livrosAdapter: ArrayAdapter<Livro>
    private lateinit var listaLivros: ArrayList<Livro>

    private val db = DAO(this)  // Supondo que você tenha um DAO para acessar o banco de dados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaAluguelBinding.inflate(layoutInflater)
        setContentView(binding.listaAluguel)

        // Inicializar as views
        val listView = binding.listLivros
        val btnTodos = binding.btnTodos
        val btnDisponiveis = binding.btnDisponiveis
        val btnAlugados = binding.btnAlugados

        // Inicializar a lista de livros e o Adapter
        listaLivros = ArrayList()
        livrosAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLivros)
        listView.adapter = livrosAdapter

        // Carregar todos os livros inicialmente
        carregarLivros("todos")

        // Configurar o clique nos botões de filtro
        btnTodos.setOnClickListener {
            carregarLivros("todos")
        }

        btnDisponiveis.setOnClickListener {
            carregarLivros("disponiveis")
        }

        btnAlugados.setOnClickListener {
            carregarLivros("alugados")
        }


        binding.listLivros.setOnItemClickListener { _, _, position, _ ->
            val livroSelecionado = listaLivros[position]


            val intent = Intent(this, LivroDetalhes::class.java).apply {
                putExtra("id", livroSelecionado.id)
                putExtra("titulo", livroSelecionado.titulo) ?: "Titulo não encontrado"
                putExtra("autor", livroSelecionado.autor) ?: "Autor não encontrado"
                putExtra("imgUri", livroSelecionado.imgUri) // Passa o URI da imagem
            }

            startActivity(intent)
        }

        binding.btnCancelar.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }
    }


    // Função para carregar os livros por filtro
    private fun carregarLivros(filtro: String) {

        listaLivros.clear()

        // Buscar livros com base no filtro
        val livros: List<Livro> = when (filtro) {
            "disponiveis" -> db.livrosDisponiveis()  // Método que retorna apenas livros disponíveis
            "alugados" -> db.livrosAlugados()       // Método que retorna apenas livros alugados
            else -> db.mostrarTodosLivros()                // Método que retorna todos os livros
        }

        // Adicionar os livros encontrados na lista
        listaLivros.addAll(livros)

        // Notificar o Adapter para atualizar a UI
        livrosAdapter.notifyDataSetChanged()
    }

}