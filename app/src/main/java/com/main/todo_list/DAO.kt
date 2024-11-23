package com.main.todo_list

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DAO(context: Context) : SQLiteOpenHelper(context, "biblioteca.db", null, 1) {

    val sql = arrayOf("CREATE TABLE livro(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, autor TEXT, alugado BOOL)",
            "create table cliente(id integer primary key autoincrement, nome text, email text)",
        "CREATE TABLE aluguel(id INTEGER PRIMARY KEY AUTOINCREMENT, id_cliente INTEGER NOT NULL,id_livro INTEGER NOT NULL, data_aluguel DATE DEFAULT CURRENT_DATE,\n" +
                " FOREIGN KEY (id_cliente) REFERENCES Cliente (id),\n" +
                " FOREIGN KEY (id_livro) REFERENCES Livro (id))")

    override fun onCreate(db: SQLiteDatabase) {
        sql.forEach {
            db.execSQL(it)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql = arrayOf("DROP TABLE IF EXISTS livro", "DROP TABLE IF EXISTS cliente")
        sql.forEach {
            db.execSQL(it)
        }
    }


    //Funções CRUD clientes
    fun clienteInsert(nome: String, email: String) : Long{
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("nome", nome)
        contentValues.put("email", email)
        val resultado = db.insert("cliente", null, contentValues)
        db.close()
        return resultado
    }

    fun clienteUpdate(id: Int ,nome: String, email: String) : Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("titulo", nome)
        contentValues.put("autor", email)
        val resultado = db.update("cliente", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

    fun clienteDelete(id: Int) : Int {
        val db = writableDatabase
        val resultado = db.delete("cliente", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

    fun mostrarTodosClientes(): ArrayList<Cliente> {
        val db = readableDatabase
        val sql = db.rawQuery("SELECT * FROM cliente", null)
        val listaClientes: ArrayList<Cliente> = ArrayList()
        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id")) // Obtém o valor da coluna "id"
                val nome = sql.getString(sql.getColumnIndex("nome")) // Obtém o valor da coluna "titulo"
                val email = sql.getString(sql.getColumnIndex("email")) // Obtém o valor da coluna "autor"
                listaClientes.add(Cliente(id, nome, email)) // Adiciona à lista
            } while (sql.moveToNext())
        }
        sql.close() // Fecha o cursor
        db.close() // Fecha o banco
        return listaClientes
    }

    fun clientePorNome(nome: String): ArrayList<Cliente> {
        val db = readableDatabase
        val sql = db.rawQuery("SELECT nome, email FROM cliente WHERE nome LIKE ?", arrayOf(nome))
        val listaClientes: ArrayList<Cliente> = ArrayList()
        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id"))
                val nome = sql.getString(sql.getColumnIndex("nome"))
                val email = sql.getString(sql.getColumnIndex("email"))
                listaClientes.add(Cliente(id, nome, email))
            } while (sql.moveToNext())
        }
        return listaClientes
    }


    //Funções CRUD livros
    fun livroInsert(titulo: String, autor: String) : Long{
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("titulo", titulo)
        contentValues.put("autor", autor)
        val resultado = db.insert("livro", null, contentValues)
        db.close()
        return resultado
    }

    fun livroUpdate(id: Int ,titulo: String, autor: String) : Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("titulo", titulo)
        contentValues.put("autor", autor)
        val resultado = db.update("livro", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

    fun livroDelete(id: Int) : Int {
        val db = writableDatabase
        val resultado = db.delete("livro", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

    fun mostrarTodosLivros(): ArrayList<Livro> {
        val db = readableDatabase
        val sql = db.rawQuery("SELECT * FROM livro", null)
        val listaLivros: ArrayList<Livro> = ArrayList()
        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id")) // Obtém o valor da coluna "id"
                val titulo = sql.getString(sql.getColumnIndex("titulo")) // Obtém o valor da coluna "titulo"
                val autor = sql.getString(sql.getColumnIndex("autor")) // Obtém o valor da coluna "autor"

                listaLivros.add(Livro(id, titulo, autor)) // Adiciona à lista
            } while (sql.moveToNext())
        }
        sql.close() // Fecha o cursor
        db.close() // Fecha o banco
        return listaLivros
    }

    @SuppressLint("Range")
    fun livrosPorAutor(autor: String): ArrayList<Livro> {
        val db = readableDatabase
        val sql = db.rawQuery("SELECT titulo, autor FROM livro WHERE autor LIKE ?", arrayOf(autor))
        val listaLivros: ArrayList<Livro> = ArrayList()
        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id"))
                val titulo = sql.getString(sql.getColumnIndex("titulo"))
                val autor = sql.getString(sql.getColumnIndex("autor"))
                listaLivros.add(Livro(id, titulo, autor))
            } while (sql.moveToNext())
        }
        return listaLivros
    }

    @SuppressLint("Range")
    fun livrosPorTitulo(titulo: String): ArrayList<Livro> {
        val db = readableDatabase
        val sql = db.rawQuery(
            "SELECT titulo, autor FROM livro WHERE titulo LIKE ?",
            arrayOf(titulo)
        )
        val listaLivros: ArrayList<Livro> = ArrayList()
        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id"))
                val titulo = sql.getString(sql.getColumnIndex("titulo"))
                val autor = sql.getString(sql.getColumnIndex("autor"))
                listaLivros.add(Livro(id, titulo, autor))
            } while (sql.moveToNext())
        }
        return listaLivros
    }

    fun livrosDisponiveis(): ArrayList<Livro> {
        val db = readableDatabase
        val sql = db.rawQuery("SELECT * FROM livro WHERE alugado = 0", null)
        val listaLivros: ArrayList<Livro> = ArrayList()
        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id"))
                val titulo = sql.getString(sql.getColumnIndex("titulo"))
                val autor = sql.getString(sql.getColumnIndex("autor"))
                listaLivros.add(Livro(id, titulo, autor))
            } while (sql.moveToNext())
        }
        return listaLivros
    }

    fun livrosAlugados(): ArrayList<Livro> {
        val db = readableDatabase
        val sql = db.rawQuery("SELECT * FROM livro WHERE alugado = 1", null)
        val listaLivros: ArrayList<Livro> = ArrayList()

        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id"))
                val titulo = sql.getString(sql.getColumnIndex("titulo"))
                val autor = sql.getString(sql.getColumnIndex("autor"))
                listaLivros.add(Livro(id, titulo, autor))
            } while (sql.moveToNext())
        }
        return listaLivros
    }

//Funções aluguel
    fun alugarLivro(idLivro: Int, idCliente: Int) {
        val db = writableDatabase
        val values = ContentValues()

        values.put("id_cliente", idCliente)
        values.put("id_livro", idLivro)

        db.insert("aluguel", null, values)
        db.close()
    }

    fun atualizarStatusLivro(livroId: Int, alugado: Boolean) {
        val db = writableDatabase
        val values = ContentValues()

        values.put("alugado", if (alugado) 1 else 0)

        db.update("livro", values, "id = ?", arrayOf(livroId.toString()))
        db.close()
    }

    fun listarAlugueis(): List<String> {
        val db = readableDatabase
        val sql = """
        SELECT cliente.nome, livro.titulo, aluguel.data_aluguel 
        FROM aluguel
        INNER JOIN cliente ON cliente.id = aluguel.id_cliente
        INNER JOIN livro ON Livro.id = aluguel.id_livro
    """
        val cursor = db.rawQuery(sql, null)
        val lista: ArrayList<String> = ArrayList()

        if (cursor.moveToFirst()) {
            do {
                val cliente = cursor.getString(cursor.getColumnIndex("nome"))
                val titulo = cursor.getString(cursor.getColumnIndex("titulo"))
                val dataAluguel = cursor.getString(cursor.getColumnIndex("data_aluguel"))
                lista.add("cliente: $cliente, livro: $titulo, alugado em: $dataAluguel")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    fun dropTable(tabela1: String, tabela2: String) {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $tabela1")
        db.execSQL("DROP TABLE IF EXISTS $tabela2")
        db.close()
    }

}





/* EM CASOS EXTREMOS

   */