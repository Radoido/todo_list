package com.main.todo_list

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.main.todo_list.databinding.ActivityLivroDetalhesBinding
import java.io.File
import java.io.FileOutputStream

class LivroDetalhes : AppCompatActivity() {

    private var imgUri: Uri? = null
    private var onImageSelected: ((Uri?) -> Unit)? = null
    private lateinit var binding: ActivityLivroDetalhesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLivroDetalhesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.livroDetalhes)

        val livroId = intent.getIntExtra("id", -1)
        if (livroId != -1) {
            val db = DAO(this)
            val livro = db.buscarLivroPorId(livroId)

            livro?.let {
                val titulo = intent.getStringExtra("titulo")
                val autor = intent.getStringExtra("autor")
                val imgUri = intent.getStringExtra("imgUri")

                // Atualiza a interface com os dados do livro
                binding.txtTitulo.text = titulo
                binding.txtAutor.text = autor
                if (!imgUri.isNullOrEmpty()) {
                    binding.imgLivro.setImageURI(Uri.parse(imgUri))
                } else {
                    binding.imgLivro.setImageResource(R.drawable.placeholder) // Placeholder caso não haja imagem
                }


/*
                binding.imgLivro.setOnClickListener {
                    checkMediaPermission { selectedUri ->
                        if (selectedUri != null) {
                            db.atualizarImagemLivro(livroId, selectedUri.toString())
                            binding.imgLivro.setImageURI(selectedUri)
                            println("Imagem salva em: $selectedUri")
                            db.atualizarImagemLivro(livroId, selectedUri.toString())
                        } else {
                            println("Nenhuma imagem foi selecionada.")
                        }
                    }
                }
*/


                binding.btnCancelar.setOnClickListener {
                    val intent = Intent(this, ListaAluguel::class.java)
                    startActivity(intent)
                    finish()
                }

            }
        }
    }



    //Função abrir galeria
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