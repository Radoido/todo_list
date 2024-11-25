package com.main.todo_list

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.main.todo_list.databinding.ActivityCreateFuncionarioBinding

class CreateFuncionario : AppCompatActivity() {
    private lateinit var binding: ActivityCreateFuncionarioBinding
    private lateinit var adapter: ArrayAdapter<Funcionario>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateFuncionarioBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.createFuncionario)

        val db = DAO(this)
        val listaFuncionarios = db.mostrarTodosFuncionarios()


        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaFuncionarios)
        binding.listFuncionario.adapter = adapter

        binding.listFuncionario.setOnItemClickListener { _, _, position, _ ->
            binding.editNome.setText(listaFuncionarios[position].nome)
            binding.editCargo.setText(listaFuncionarios[position].cargo)
            binding.editSenha.setText(listaFuncionarios[position].senha)
            binding.txtId.text = "ID: ${listaFuncionarios[position].id}"
        }

        binding.btnSalvar.setOnClickListener {
            val nome = binding.editNome.text.toString()
            val senha = binding.editSenha.text.toString()
            val cargo = binding.editCargo.text.toString()
            val resultado = db.funcionarioInsert(nome, senha, cargo)

            if (resultado > 0) {
                listaFuncionarios.add(Funcionario(resultado.toInt(),nome, senha, cargo))
                adapter.notifyDataSetChanged()
                binding.editNome.setText("")
                binding.editCargo.setText("")
                binding.txtId.text = "ID: "

            } else {
                Toast.makeText(this, "Insira o nome e email do funcionario", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEditar.setOnClickListener() {
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val nome = binding.editNome.text.toString()
            val cargo = binding.editCargo.text.toString()
            val senha = binding.editSenha.text.toString()

            if (id == null) {
                Toast.makeText(this, "Selecione um funcionario para editar", Toast.LENGTH_SHORT).show()
            } else if (nome.isBlank() || cargo.isBlank()) {
                Toast.makeText(this, "Nome e email não podem estar vazios", Toast.LENGTH_SHORT)
                    .show()
            } else if (nome == listaFuncionarios[id].nome && cargo == listaFuncionarios[id].cargo && senha == listaFuncionarios[id].senha) {
                Toast.makeText(
                    this,
                    "Altere as informações que deseja atualizar!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Chama a função de atualização passando os valores
                val resultado = db.funcionarioUpdate(id, nome, cargo, senha)

                if (resultado > 0) {
                    listaFuncionarios[id] = Funcionario(id, nome, cargo)
                    adapter.notifyDataSetChanged()

                    Toast.makeText(this, "Funcionario atualizado com sucesso!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Erro ao atualizar o funcionario", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnExcluir.setOnClickListener {
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val nome = binding.editNome.text.toString()
            db.funcionarioDelete(id!!)
            Toast.makeText(this, "O funcionario $nome foi excluido com sucesso!", Toast.LENGTH_SHORT)
                .show()
        }

        binding.btnCancelar.setOnClickListener {
            binding.editNome.setText("")
            binding.editCargo.setText("")
            binding.editSenha.setText("")
            binding.txtId.text = "ID: "

            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

    }
}