package com.main.todo_list

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.main.todo_list.databinding.ActivityCreateLivroBinding
import java.io.File
import java.io.FileOutputStream

class CreateLivro : AppCompatActivity() {
    private var imgUri: Uri? = null
    private var onImageSelected: ((Uri?) -> Unit)? = null


    private lateinit var binding: ActivityCreateLivroBinding
    private lateinit var adapter: ArrayAdapter<Livro>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateLivroBinding.inflate(layoutInflater)
        setContentView(binding.createLivro)


        val db = DAO(this)
        val listaLivros = db.mostrarTodosLivros()


        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLivros)
        binding.listLivro.adapter = adapter

        binding.listLivro.setOnItemClickListener { _, _, position, _ ->
            binding.editTitulo.setText(listaLivros[position].titulo)
            binding.editAutor.setText(listaLivros[position].autor)
            binding.txtId.text = "ID: ${listaLivros[position].id}"
        }

        binding.btnSalvar.setOnClickListener {
            val titulo = binding.editTitulo.text.toString()
            val autor = binding.editAutor.text.toString()
            val uriImagem = imgUri?.toString() ?: "android.resource://${packageName}/${R.drawable.placeholder}"

            val resultado = db.livroInsert(titulo, autor, uriImagem)

            if (resultado > 0) {
                listaLivros.add(Livro(resultado.toInt(), titulo, autor))
                adapter.notifyDataSetChanged()
                binding.editTitulo.setText("")
                binding.editAutor.setText("")
                binding.txtId.text = "ID: "

            } else {
                Toast.makeText(this, "Insira o titulo e autor do livro", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEditar.setOnClickListener() {
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val titulo = binding.editTitulo.text.toString()
            val autor = binding.editAutor.text.toString()

            if (id == null) {
                Toast.makeText(this, "Selecione um livro para editar", Toast.LENGTH_SHORT).show()
            } else if (titulo.isBlank() || autor.isBlank()) {
                Toast.makeText(this, "Título e autor não podem estar vazios", Toast.LENGTH_SHORT)
                    .show()
            } else if (titulo == listaLivros[id].titulo && autor == listaLivros[id].autor) {
                Toast.makeText(this, "Altere as informações que deseja atualizar!", Toast.LENGTH_SHORT).show()
            } else {
                // Chama a função de atualização passando os valores
                val resultado = db.livroUpdate(id, titulo, autor)

                if (resultado > 0) {
                    listaLivros[id] = Livro(id, titulo, autor)
                    adapter.notifyDataSetChanged()

                    Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao atualizar o livro", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnExcluir.setOnClickListener{
            val idString = binding.txtId.text.toString()
            val id = idString.substringAfter("ID: ").toIntOrNull()
            val titulo = binding.editTitulo.text.toString()
            db.livroDelete(id!!)
            Toast.makeText(this, "O livro $titulo foi excluido com sucesso!", Toast.LENGTH_SHORT).show()
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
            binding.editTitulo.setText("")
            binding.editAutor.setText("")
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
    /*
       FUNÇÕES PARA A CAMERA


    private fun checkMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_MEDIA_PERMISSION
                )
            } else {
                com.main.todo_list.openImagePicker()
            }
        } else { // For Android versions below Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_MEDIA_PERMISSION
                )
            } else {
                com.main.todo_list.openImagePicker()
            }
        }
    }
/*
    private fun openImagePicker() {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST)
    }
*/
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            handleImageUri(it)  // Processa a URI da imagem
        }
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")  // Filtra apenas imagens
    }

    // Handle the result of image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            // Get the image URI and display it
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                com.main.todo_list.handleImageUri(uri)
                binding.btnImg.setImageURI(uri)

            }
        }
    }
    private fun handleImageUri(uri: Uri) {
        // Get the app's internal storage directory (private folder)
        val appContext = applicationContext
        val folder = File(appContext.filesDir, "images")  // Create a folder called "images" inside internal storage

        if (!folder.exists()) {
            folder.mkdirs()  // Make sure the folder exists
        }

        // Generate a new file in the internal storage
        val fileName = "uploadedimage${System.currentTimeMillis()}.jpg"
        val newFile = File(folder, fileName)

        try {
            // Open input stream to read the image
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(newFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    // Copy the content of the image to the new file
                    input.copyTo(output)
                }
            }

            // Successfully saved the image to internal storage
            Toast.makeText(this, "Image saved to app's internal storage", Toast.LENGTH_SHORT).show()

            val savedImageUri = Uri.fromFile(newFile)

            imgUri = savedImageUri

        } catch (e: Exception) {
            // Handle the exception if something goes wrong
            Toast.makeText(this, "Failed to save image: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    // Handle the result of the media permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the image picker
                openImagePicker()
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Permission denied to read your images", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

     */