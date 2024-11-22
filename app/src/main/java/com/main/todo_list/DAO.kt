package com.main.todo_list

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DAO(context: Context) : SQLiteOpenHelper(context, "biblioteca.db", null, 1) {

    val sql = arrayOf("CREATE TABLE livro(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, autor TEXT)",
            "create table cliente(id integer primary key autoincrement, nome text,cpf integer, email text)")

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

    fun livroInsert(titulo: String, autor: String) : Long{
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("titulo", titulo)
        contentValues.put("autor", autor)
        val resultado = db.insert("livro", null, contentValues)
        db.close()
        return resultado
    }
    fun clienteInsert(nome: String, cpf: Int, email: String) : Long{
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("nome", nome)
        contentValues.put("cpf", cpf)
        contentValues.put("email", email)
        val resultado = db.insert("cliente", null, contentValues)
        db.close()
        return resultado
    }


    @SuppressLint("Range")
    fun livrosMostrarTodos(): ArrayList<Livro> {
        val db = readableDatabase
        val sql = db.rawQuery("SELECT titulo, autor FROM livro", null)
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
        val resultado = db.delete("livro", "titulo = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }
}