package com.main.todo_list

class Livro(val id : Int = 0, var titulo: String = "",var autor: String = "",var alugado: Boolean = false, var imgUri: String = "") {

    override fun toString(): String {
        return "$id. $titulo - $autor | Alugado: ${if (alugado) "Sim" else "Não"}"
    }


}