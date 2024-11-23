package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.main.todo_list.databinding.ActivityCreateLivroBinding

class CreateLivro : AppCompatActivity() {
    private lateinit var binding: ActivityCreateLivroBinding
    private lateinit var adapter: ArrayAdapter<Livro>
    private var p: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateLivroBinding.inflate(layoutInflater)
        setContentView(binding.createLivro)


        val db = DAO(this)
        val listaLivros = db.mostrarTodosLivros()


        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLivros)
        binding.listLivro.adapter = adapter

        binding.listLivro.setOnItemClickListener { _, _, position, _ ->
            binding.editTitulo.setText(listaLivros[position].titulo)
            binding.editAutor.setText(listaLivros[position].autor)
            binding.txtId.text = "ID: ${listaLivros[position].id}"
            p = position
        }

        binding.btnSalvar.setOnClickListener {
            val nome = binding.editTitulo.text.toString()
            val email = binding.editAutor.text.toString()
            val resultado = db.livroInsert(nome, email)

            if (resultado > 0) {
                listaLivros.add(Livro(resultado.toInt(), nome, email))
                adapter.notifyDataSetChanged()
                binding.editTitulo.setText("")
                binding.editAutor.setText("")
                binding.txtId.text = "ID: "

            }
        }

        binding.btnCancelar.setOnClickListener {
            binding.editTitulo.setText("")
            binding.editAutor.setText("")
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

    }
}