package com.main.todo_list

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DAO(context: Context) : SQLiteOpenHelper(context, "biblioteca.db", null, 1) {

    companion object{
        private const val DATABASE_NAME = "sistema.db"
        private const val DATABASE_VERSION = 1

        // Tabelas e colunas
        private const val TABLE_LIVRO = "livro"
        private const val TABLE_CLIENTE = "cliente"
        private const val TABLE_ALUGUEL = "aluguel"
        private const val TABLE_FUNCIONARIOS = "funcionarios"
    }

    private val createTables = arrayOf(
        """
        CREATE TABLE $TABLE_LIVRO (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            titulo TEXT NOT NULL,
            autor TEXT NOT NULL,
            alugado INTEGER DEFAULT 0
        )
        """.trimIndent(),

        """
        CREATE TABLE $TABLE_CLIENTE (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            email TEXT
        )
        """.trimIndent(),

        """
        CREATE TABLE $TABLE_ALUGUEL (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_cliente INTEGER NOT NULL,
            id_livro INTEGER NOT NULL,
            data_aluguel DATE DEFAULT CURRENT_DATE,
            FOREIGN KEY (id_cliente) REFERENCES $TABLE_CLIENTE (id),
            FOREIGN KEY (id_livro) REFERENCES $TABLE_LIVRO (id)
        )
        """.trimIndent(),

        """
        CREATE TABLE $TABLE_FUNCIONARIOS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            senha TEXT NOT NULL,
            cargo TEXT NOT NULL
        )
        """.trimIndent()
    )

    private val insertAdmin = """
        INSERT INTO $TABLE_FUNCIONARIOS (nome, senha, cargo)
        VALUES ('admin', 'admin', 'Admin')
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        createTables.forEach { sql ->
            db.execSQL(sql)
        }
        db.execSQL(insertAdmin)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTables = arrayOf(
            "DROP TABLE IF EXISTS $TABLE_ALUGUEL",
            "DROP TABLE IF EXISTS $TABLE_CLIENTE",
            "DROP TABLE IF EXISTS $TABLE_LIVRO",
            "DROP TABLE IF EXISTS $TABLE_FUNCIONARIOS"
        )

        dropTables.forEach {
            sql ->db.execSQL(sql)
        }
        onCreate(db)
    }



    //Funções CRUD clientes

    fun clienteInsert(nome: String, email: String) : Long{
        if(nome.isBlank() && email.isBlank()){
            return -1
        }
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
         db.close()// Fecha o banco
        return listaClientes
    }

    //Funções CRUD livros
    fun livroInsert(titulo: String, autor: String): Long {
        if (titulo.isBlank() || autor.isBlank()) {
            return -1
        }

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
        db.close()// Fecha o banco
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


    fun atualizarStatusLivro(livroId: Int, alugado: Int) {
        val db = writableDatabase
        val values = ContentValues()

        values.put("alugado", alugado)

        db.update("livro", values, "id = ?", arrayOf(livroId.toString()))
        db.close()
    }

    fun listarAlugueis(): List<String> {
        val db = readableDatabase
        val sql = """
        SELECT cliente.nome, livro.titulo
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
                //val dataAluguel = cursor.getString(cursor.getColumnIndex("data_aluguel"))
                lista.add("cliente: $cliente, livro: $titulo")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun devolveLivro(idLivro: Int) : Int {
        val db = writableDatabase
        val resultado = db.delete("aluguel", "id_livro = ?", arrayOf(idLivro.toString()))
        db.close()
        return resultado
    }

    //Funções CRUD funcionarios
    fun funcionarioInsert(funcionario: Funcionario) {
        val db = writableDatabase
        val values = ContentValues()

        values.put("nome", funcionario.nome)
        values.put("senha", funcionario.senha)
        values.put("cargo", funcionario.cargo)

        db.insert("funcionarios", null, values)
        db.close()
    }

    fun funcionarioUpdate(id: Int ,nome: String, email: String) : Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("titulo", nome)
        contentValues.put("autor", email)
        val resultado = db.update("cliente", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

    fun funcionarioDelete(id: Int) : Int {
        val db = writableDatabase
        val resultado = db.delete("cliente", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

    fun verificarLogin(nome: String, senha: String): Funcionario? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM funcionarios WHERE nome = ? AND senha = ?", arrayOf(nome, senha))

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val nome = cursor.getString(cursor.getColumnIndex("nome"))
            val senha = cursor.getString(cursor.getColumnIndex("senha"))
            val cargo = cursor.getString(cursor.getColumnIndex("cargo"))

            Funcionario(id, nome, senha, cargo)
        } else {
            null
        }
    }

}





/* EM CASOS EXTREMOS
fun dropTable(tabela1: String, tabela2: String) {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $tabela1")
        db.execSQL("DROP TABLE IF EXISTS $tabela2")
        db.close()
    }
   */