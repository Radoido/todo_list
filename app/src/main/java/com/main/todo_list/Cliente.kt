package com.main.todo_list

class Cliente (var nome: String = "",val cpf: Int = 0, var email: String){

    override fun toString(): String {
        return "Cliente(nome='$nome', cpf=$cpf, email='$email')"

    }



}