package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
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

            }
        }

        binding.btnCancelar.setOnClickListener {
            binding.editNome.setText("")
            binding.editEmail.setText("")
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

    }
}