package com.dicoding.storyapp.ui.insert

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.source.remote.request.NewStoryRequest
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.databinding.ActivityInsertBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.camera.CameraActivity
import com.dicoding.storyapp.utils.reduceFileImage
import com.dicoding.storyapp.utils.rotateFile
import com.dicoding.storyapp.utils.uriToFile
import com.dicoding.storyapp.ui.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class InsertActivity : AppCompatActivity() {

    private var getFile: File? = null

    private lateinit var binding: ActivityInsertBinding
    private lateinit var viewModel: InsertViewModel

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra(
                "isBackCamera",
                true
            ) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.ivItemImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@InsertActivity)
                getFile = myFile
                binding.ivItemImage.setImageURI(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    R.string.don_t_have_permission_to_access_camera,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupViewModel()
        setupSupportActionBarTitle()
        setupAction()
    }

    private fun setupAction() {
        binding.btnCameraX.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnAdd.setOnClickListener { addNewStory() }
    }

    private fun setupSupportActionBarTitle() {
        viewModel.getLogin().observe(this) { user ->
            if (user.token.isNotBlank()) {
                supportActionBar?.setTitle(R.string.add_new_story)
            } else {
                supportActionBar?.setTitle(R.string.add_new_story_guest)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[InsertViewModel::class.java]
    }

    private fun startCameraX() {
        launcherIntentCamera.launch(Intent(this, CameraActivity::class.java))
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, R.string.choose_a_picture.toString())
        launcherIntentGallery.launch(chooser)
    }

    private fun addNewStory() {
        val edAddDescription = binding.edAddDescription.text.toString()

        when {
            getFile == null -> {
                Toast.makeText(
                    this@InsertActivity,
                    R.string.please_insert_the_image_first,
                    Toast.LENGTH_SHORT
                ).show()
            }
            edAddDescription.isEmpty() -> {
                Toast.makeText(
                    this@InsertActivity,
                    R.string.please_fill_the_description_first,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val file = reduceFileImage(getFile as File)
                val description = edAddDescription.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                showAddNewStoryDialog(description, imageMultipart)
            }
        }
    }

    private fun showAddNewStoryDialog(desc: RequestBody, photo: MultipartBody.Part) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.add_new_story)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.getLogin().observe(this) { user ->
                    executeAddNewStory(user.token, desc, photo)
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun executeAddNewStory(token: String, desc: RequestBody, photo: MultipartBody.Part) {
        viewModel.addNewStory(NewStoryRequest(token, desc, photo)).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnCameraX.isEnabled = false
                        binding.btnGallery.isEnabled = false
                        binding.btnAdd.isEnabled = false
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnCameraX.isEnabled = true
                        binding.btnGallery.isEnabled = true
                        binding.btnAdd.isEnabled = true

                        Toast.makeText(
                            this,
                            R.string.add_new_story_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    is Result.Error -> {
                        binding.btnCameraX.isEnabled = true
                        binding.btnGallery.isEnabled = true
                        binding.btnAdd.isEnabled = true

                        Toast.makeText(
                            this,
                            R.string.add_new_story_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        viewModel.getLogin().observe(this) { user ->
            if (user.token.isNotBlank()) menuInflater.inflate(R.menu.option_menu_3, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_sign_out -> {
                showLogoutDialog()
                true
            }
            else -> true
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sign_out)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.deleteLogin()
                directToMainActivity()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun directToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}