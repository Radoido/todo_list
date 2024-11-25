package com.main.todo_list

class Livro(val id : Int = 0, var titulo: String = "",var autor: String = "",var alugado: Int = 0, var imgUri: String = "") {

    override fun toString(): String {
        return "$id. $titulo - $autor | Alugado: ${if (alugado == 1 ) "Sim" else "Não"}"
    }


}