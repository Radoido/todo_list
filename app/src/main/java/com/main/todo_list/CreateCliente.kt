package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.main.todo_list.databinding.ActivityCreateClienteBinding

class CreateCliente : AppCompatActivity() {
    private lateinit var binding: ActivityCreateClienteBinding
    private lateinit var adapter: ArrayAdapter<Cliente>
    private var p: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClienteBinding.inflate(layoutInflater)
        setContentView(binding.createCliente)


        val db = DAO(this)
        val listaClientes = db.mostrarTodosClientes()


        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaClientes)
        binding.listCliente.adapter = adapter

        binding.listCliente.setOnItemClickListener { _, _, position, _ ->
            binding.editNome.setText(listaClientes[position].nome)
            binding.editEmail.setText(listaClientes[position].email)
            binding.txtId.text = "ID: ${listaClientes[position].id}"
            p = position
        }

        binding.btnSalvar.setOnClickListener {
            val nome = binding.editNome.text.toString()
            val email = binding.editEmail.text.toString()
            val resultado = db.clienteInsert(nome, email)

            if (resultado > 0) {
                listaClientes.add(Cliente(resultado.toInt(), nome, email))
                adapter.notifyDataSetChanged()
                binding.editNome.setText("")
                binding.editEmail.setText("")
                binding.txtId.text = "ID: "

            }else{
                Toast.makeText(this, "Insira o nome e email do cliente", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEditar.setOnClickListener(){
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val nome = binding.editNome.text.toString()
            val email = binding.editEmail.text.toString()

            if (id == null) {
                Toast.makeText(this, "Selecione um cliente para editar", Toast.LENGTH_SHORT).show()
            } else if (nome.isBlank() || email.isBlank()) {
                Toast.makeText(this, "Nome e email não podem estar vazios", Toast.LENGTH_SHORT).show()
            } else if (nome == listaClientes[id].nome && email == listaClientes[id].email) {
                Toast.makeText(this, "Altere as informações que deseja atualizar!", Toast.LENGTH_SHORT).show()
            } else {
                // Chama a função de atualização passando os valores
                val resultado = db.clienteUpdate(id, nome, email)

                if (resultado > 0) {
                    listaClientes[id] = Cliente(id,nome, email)
                    adapter.notifyDataSetChanged()

                    Toast.makeText(this, "Cliente atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao atualizar o cliente", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnExcluir.setOnClickListener{
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val nome = binding.editNome.text.toString()
            db.clienteDelete(id!!)
            Toast.makeText(this, "O cliente $nome foi excluido com sucesso!", Toast.LENGTH_SHORT).show()
        }

        binding.btnCancelar.setOnClickListener {
            binding.editNome.setText("")
            binding.editEmail.setText("")
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

    }



}