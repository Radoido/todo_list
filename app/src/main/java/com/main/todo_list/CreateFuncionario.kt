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
import com.main.todo_list.databinding.ActivityCreateFuncionarioBinding

class CreateFuncionario : AppCompatActivity() {
    private lateinit var binding: ActivityCreateFuncionarioBinding
    private lateinit var adapterFunci: ArrayAdapter<Funcionario>
    private var p = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateFuncionarioBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.createFuncionario)

        val db = DAO(this)
        var listaFuncionarios = db.mostrarTodosFuncionarios()


        adapterFunci = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaFuncionarios)

        binding.listFuncionario.setOnItemClickListener { _, _, position, _ ->
            binding.editNome.setText(listaFuncionarios[position].nome)
            binding.editSenha.setText(listaFuncionarios[position].senha)
            binding.editCargo.setText(listaFuncionarios[position].cargo)
            binding.txtId.text = "ID: ${listaFuncionarios[position].id}"
            Log.i("funcionario", "os funcionarios: $db.mostrarTodosFuncionarios()")

        }
        binding.btnSalvar.setOnClickListener {
            val nome = binding.editNome.text.toString().trim()
            val senha = binding.editSenha.text.toString().trim()
            val cargo = binding.editCargo.text.toString().trim()
            val resultado = db.funcionarioInsert(nome, senha, cargo)

            if (resultado > 0) {
                listaFuncionarios.add(Funcionario(resultado.toInt(),nome, senha, cargo))
                adapterFunci.notifyDataSetChanged()
                binding.editNome.setText("")
                binding.editCargo.setText("")
                binding.editSenha.setText("")
                binding.txtId.text = "ID: "

            } else {
                Toast.makeText(this, "Insira o nome e email do funcionario", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEditar.setOnClickListener() {
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val nome = binding.editNome.text.toString().trim()
            val cargo = binding.editCargo.text.toString().trim()
            val senha = binding.editSenha.text.toString().trim()

            if (id == null) {
                Toast.makeText(this, "Selecione um funcionario para editar", Toast.LENGTH_SHORT).show()
            } else if (nome.isBlank() || cargo.isBlank()) {
                Toast.makeText(this, "Nome e email não podem estar vazios", Toast.LENGTH_SHORT)
                    .show()
            } else if (nome == listaFuncionarios[p].nome && cargo == listaFuncionarios[p].cargo && senha == listaFuncionarios[id].senha) {
                Toast.makeText(
                    this,
                    "Altere as informações que deseja atualizar!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Chama a função de atualização passando os valores
                val resultado = db.funcionarioUpdate(id, nome, cargo, senha)
                listaFuncionarios.clear()

                if (resultado > 0) {
                    listaFuncionarios = db.mostrarTodosFuncionarios()
                    adapterFunci.notifyDataSetChanged()

                    Toast.makeText(this, "Funcionario atualizado com sucesso!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Erro ao atualizar o funcionario", Toast.LENGTH_SHORT).show()
                }
            }
            listaFuncionarios.clear()
            listaFuncionarios.addAll(db.mostrarTodosFuncionarios())
            adapterFunci.notifyDataSetChanged()
        }

        binding.btnExcluir.setOnClickListener {
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val nome = binding.editNome.text.toString()
            db.funcionarioDelete(id!!)
            listaFuncionarios.clear()
            listaFuncionarios.addAll(db.mostrarTodosFuncionarios())
            adapterFunci.notifyDataSetChanged()
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