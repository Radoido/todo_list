package com.main.todo_list

class Cliente (var nome: String = "",val cpf: Int = 0, var email: String){

    override fun toString(): String {
        return "$cpf - $nome - $email"

    }



}