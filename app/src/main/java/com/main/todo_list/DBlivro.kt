package com.main.todo_list

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBlivro(context: Context) : SQLiteOpenHelper(context, "livro.db", null, 1) {

    val sql = "CREATE TABLE livro(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, autor TEXT)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tarefa")
        onCreate(db)
    }



    fun livrosMostrarTodos():  ArrayList<Livro>{
        val db = readableDatabase
        val sql = db.rawQuery("SELECT titulo, autor FROM tarefa", null)
        val listaLivros : ArrayList<Livro> = ArrayList()
        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id"))
                val titulo = sql.getString(sql.getColumnIndex("titulo"))
                val autor = sql.getString(sql.getColumnIndex("autor"))
                listaLivros.add(Livro(id, titulo, autor))
            }while (sql.moveToNext())
        }
        return listaLivros
    }

    fun livrosPorAutor(autor: String) : ArrayList<Livro>{
        val db = readableDatabase
        val sql = db.rawQuery("SELECT titulo, autor FROM tarefa WHERE autor LIKE ?", arrayOf(autor))
        val listaLivros : ArrayList<Livro> = ArrayList()
        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id"))
                val titulo = sql.getString(sql.getColumnIndex("titulo"))
                val autor = sql.getString(sql.getColumnIndex("autor"))
                listaLivros.add(Livro(id, titulo, autor))
            }while (sql.moveToNext())
        }
        return listaLivros
    }

    fun livrosPorTitulo(titulo: String) : ArrayList<Livro> {
        val db = readableDatabase
        val sql = db.rawQuery("SELECT titulo, autor FROM tarefa WHERE titulo LIKE ?", arrayOf(id.toString()))
        val listaLivros : ArrayList<Livro> = ArrayList()
        if (sql.moveToFirst()) {
            do {
                val id = sql.getInt(sql.getColumnIndex("id"))
                val titulo = sql.getString(sql.getColumnIndex("titulo"))
                val autor = sql.getString(sql.getColumnIndex("autor"))
                listaLivros.add(Livro(id, titulo, autor))
            }while (sql.moveToNext())
        }
         return listaLivros
        }
        return livro
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
        val resultado = db.delete("livro", "titulo = ?", arrayOf(titulo))
        db.close()
        return resultado
    }
}