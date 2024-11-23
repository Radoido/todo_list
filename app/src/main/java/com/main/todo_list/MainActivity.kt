package com.main.todo_list

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.main.todo_list.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArrayAdapter<Livro>
    private var p: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.main)

        val db = DAO(this)
        val listaLivros = db.mostrarTodosLivros()
        if (listaLivros.isEmpty()) {
            binding.txtId.text = "ID: Nenhum livro cadastrado"
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLivros)
        binding.listItem.adapter = adapter

        binding.listItem.setOnItemClickListener { _, _, position, _ ->
            binding.editTitle.setText(listaLivros[position].titulo)
            binding.editAuthor.setText(listaLivros[position].autor)
            binding.txtId.text = "ID: ${listaLivros[position].id}"
            p = position
        }

        binding.btnSave.setOnClickListener {
            val titulo = binding.editTitle.text.toString()
            val autor = binding.editAuthor.text.toString()
            val resultado = db.livroInsert(titulo, autor)

            if (resultado > 0) {
                listaLivros.add(Livro(resultado.toInt(), titulo, autor))
                adapter.notifyDataSetChanged()
                binding.editTitle.setText("")
                binding.editAuthor.setText("")
                binding.txtId.text = "ID: "

            }

        }

        binding.btnEdit.setOnClickListener {

            val intent = Intent(this, CreateLivro::class.java)
            startActivity(intent)
        }

        binding.btnDelete.setOnClickListener{

        }
    }
}