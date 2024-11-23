package com.main.todo_list

class Cliente (var id: Int = 0, var nome: String = "", var email: String, var alugados: ArrayList<Livro> = ArrayList()){

    override fun toString(): String {
        return "$id. $nome - $email"

    }



}