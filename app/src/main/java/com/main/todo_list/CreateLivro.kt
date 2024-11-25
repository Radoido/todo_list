package com.main.todo_list

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.main.todo_list.databinding.ActivityCreateLivroBinding
import java.io.File
import java.io.FileOutputStream

class CreateLivro : AppCompatActivity() {
    private var imgUri: Uri? = null
    private var onImageSelected: ((Uri?) -> Unit)? = null

    private var p = -1

    private lateinit var binding: ActivityCreateLivroBinding
    private lateinit var adapterLivro: ArrayAdapter<Livro>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateLivroBinding.inflate(layoutInflater)
        setContentView(binding.createLivro)


        val db = DAO(this)
        val listaLivros = db.mostrarTodosLivros()


        adapterLivro = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLivros)
        binding.listLivro.adapter = adapterLivro

        binding.listLivro.setOnItemClickListener { _, _, position, _ ->
            binding.editTitulo.setText(listaLivros[position].titulo)
            binding.editAutor.setText(listaLivros[position].autor)
            binding.txtId.text = "ID: ${listaLivros[position].id}"
            binding.btnImagem.setImageURI(Uri.parse(listaLivros[position].imgUri))
            p = position
        }

        binding.btnSalvar.setOnClickListener {
            val titulo = binding.editTitulo.text.toString()
            val autor = binding.editAutor.text.toString()
            val uriImagem = imgUri?.toString() ?: "android.resource://${packageName}/${R.drawable.placeholder}"

            val resultado = db.livroInsert(titulo, autor, uriImagem)

            if (resultado > 0) {
                listaLivros.add(Livro(resultado.toInt(), titulo, autor))
                adapterLivro.notifyDataSetChanged()
                binding.editTitulo.setText("")
                binding.editAutor.setText("")
                binding.txtId.text = "ID: "
                Toast.makeText(this, "Livro inserido com sucesso!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Insira o titulo e autor do livro", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEditar.setOnClickListener() {
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val titulo = binding.editTitulo.text.toString()
            val autor = binding.editAutor.text.toString()
            val imgUri = binding.btnImagem.toString()


            if (id == null) {
                Toast.makeText(this, "Selecione um livro para editar", Toast.LENGTH_SHORT).show()
            } else if (titulo.isBlank() || autor.isBlank()) {
                Toast.makeText(this, "Título e autor não podem estar vazios", Toast.LENGTH_SHORT)
                    .show()
            } else if (titulo == listaLivros[p].titulo && autor == listaLivros[p].autor) {
                Toast.makeText(this, "Altere as informações que deseja atualizar!", Toast.LENGTH_SHORT).show()
            } else {
                // Chama a função de atualização passando os valores
                val resultado = db.livroUpdate(id, titulo, autor, imgUri)

                if (resultado > 0) {
                    listaLivros[p] = Livro(id, titulo, autor)
                    adapterLivro.notifyDataSetChanged()

                    Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao atualizar o livro", Toast.LENGTH_SHORT).show()
                }
            }
            listaLivros.clear()
            listaLivros.addAll(db.mostrarTodosLivros())
            adapterLivro.notifyDataSetChanged()
        }

        binding.btnExcluir.setOnClickListener{
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val titulo = binding.editTitulo.text.toString()
            db.livroDelete(id!!)
            Toast.makeText(this, "O livro $titulo foi excluido com sucesso!", Toast.LENGTH_SHORT).show()
            listaLivros.clear()
            listaLivros.addAll(db.mostrarTodosLivros())
            adapterLivro.notifyDataSetChanged()
        }

        binding.btnImagem.setOnClickListener {
            checkMediaPermission { selectedUri ->
                if (selectedUri != null) {
                    binding.btnImagem.setImageURI(selectedUri)
                    println("Imagem salva em: $selectedUri")
                } else {
                    println("Nenhuma imagem foi selecionada.")

                }
            }
        }


        binding.btnCancelar.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }


    }
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            handleImageUri(it)
            onImageSelected?.invoke(it)
        }  // Processa a URI da imagem selecionada
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openImagePicker()  // Permissão concedida, abre a galeria
        } else {
            Toast.makeText(this, "Permissão negada para acessar a galeria", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkMediaPermission(onImageReady: (Uri?) -> Unit)  {
        // Verifica se a permissão necessária foi concedida
        onImageSelected = onImageReady  // Armazena o callback

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()  // Permissão já concedida, abre a galeria
        } else {
            permissionLauncher.launch(permission)  // Solicita a permissão
        }
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")  // Abre a galeria para selecionar imagens
    }

    private fun handleImageUri(uri: Uri) {
        val appContext = applicationContext
        val folder = File(appContext.filesDir, "images")  // Pasta interna para salvar imagens

        if (!folder.exists()) {
            folder.mkdirs()  // Cria a pasta se não existir
        }

        val fileName = "livro_${System.currentTimeMillis()}.jpg"  // Nome único para o arquivo
        val newFile = File(folder, fileName)

        try {
            // Copia o conteúdo da imagem para a nova localização
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(newFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Toast.makeText(this, "Imagem salva com sucesso!", Toast.LENGTH_SHORT).show()
            imgUri = Uri.fromFile(newFile)  // Salva a URI da imagem para referência futura

        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao salvar a imagem: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

}
