package com.main.todo_list

class Funcionario (var id: Int = 0, var nome: String = "", var senha: String = "", var cargo: String = ""){

    override fun toString(): String {
        return "$id - Nome: $nome | Cargo: $cargo | Senha: $senha"

    }


}