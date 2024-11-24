package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
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
            val titulo = binding.editTitulo.text.toString()
            val autor = binding.editAutor.text.toString()
            val resultado = db.livroInsert(titulo, autor)

            if (resultado > 0) {
                listaLivros.add(Livro(resultado.toInt(), titulo, autor))
                adapter.notifyDataSetChanged()
                binding.editTitulo.setText("")
                binding.editAutor.setText("")
                binding.txtId.text = "ID: "

            }else{
                Toast.makeText(this, "Insira o titulo e autor do livro", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEditar.setOnClickListener(){
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val titulo = binding.editTitulo.text.toString()
            val autor = binding.editAutor.text.toString()

            if (id == null) {
                Toast.makeText(this, "Selecione um livro para editar", Toast.LENGTH_SHORT).show()
            } else if (titulo.isBlank() || autor.isBlank()) {
                Toast.makeText(this, "Título e autor não podem estar vazios", Toast.LENGTH_SHORT).show()
            } else {
                // Chama a função de atualização passando os valores
                val resultado = db.livroUpdate(id, titulo, autor)
                adapter.notifyDataSetChanged()
                if (resultado > 0) {
                    listaLivros[id] = Livro(id,titulo, autor)

                    Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao atualizar o livro", Toast.LENGTH_SHORT).show()
                }
            }


        }

        binding.btnCancelar.setOnClickListener {
            binding.editTitulo.setText("")
            binding.editAutor.setText("")
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

    }
}