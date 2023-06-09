package com.dicoding.storyapp.ui.story

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityStoryBinding
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.ui.maps.PickLocationActivity
import com.dicoding.storyapp.utils.Helper
import com.dicoding.storyapp.utils.Helper.uriToFile
import com.dicoding.storyapp.utils.preferences.UserPreference
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var userPreference: UserPreference
    private var usingLocation : Boolean? = false
    private val storyViewModel: StoryViewModel by viewModels()
    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                Helper.rotateFile(file, isBackCamera)
                getFile = file
                binding.ivStory.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile =
                    uriToFile(uri, this@StoryActivity)
                getFile = myFile
                binding.ivStory.setImageURI(uri)
            }
        }
    }
    private val launcerIntentLocation = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.let { result ->
                usingLocation = result.getBooleanExtra("USINGLOCATION", false)
                storyViewModel.apply {
                    isUsingLocation.postValue(usingLocation)
                    val tempLatitude = result.getDoubleExtra("LATITUDE", 0.0)
                    val tempLongitude = result.getDoubleExtra("LONGITUDE", 0.0)
                    latitude.postValue(tempLatitude)
                    longitude.postValue(tempLongitude)
                    setupAddress(tempLatitude, tempLongitude)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreference = UserPreference(this)
        setupAction()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

    }

    private fun setupAddress(latitude: Double, longitude: Double){
        storyViewModel.isUsingLocation.observe(this,{
            binding.btnAddLocation.text = Helper.parseAddress(this@StoryActivity, latitude, longitude)
        })
    }

    private fun setupAction() {
        binding.apply {
            btnCamera.setOnClickListener {
                startCameraX()
            }
            btnGallery.setOnClickListener {
                startGallery()
            }
            btnUpload.setOnClickListener {
                if (binding.etDescription.text.toString().isEmpty()) {
                    binding.etDescription.error =
                        resources.getString(R.string.column_isEmpty_message)
                } else uploadImage()
            }
            btnAddLocation.setOnClickListener {
                getMyLocation()
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun getMyLocation() {
        val intent = Intent(this, PickLocationActivity::class.java)
        launcerIntentLocation.launch(intent)
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = Helper.reduceFileImage(getFile as File)
            val description =
                binding.etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val latitude = storyViewModel.latitude.value.toString()
            val longitude = storyViewModel.longitude.value.toString()
            val positionLat = latitude.toRequestBody("text/plain".toMediaType())
            val positionLon = longitude.toRequestBody("text/plain".toMediaType())

            storyViewModel.apply {
                isLoading.observe(this@StoryActivity, {
                    showLoading(it)
                })
                if (userPreference.isLogin()) {
                    if (isUsingLocation.value == true) {
                        postStory(this@StoryActivity, imageMultipart, description, true, positionLat, positionLon)
                    } else {
                        postStory(this@StoryActivity, imageMultipart, description)
                    }
                }
                isLogin.observe(this@StoryActivity, { state ->
                    if (state) showDialog(resources.getString(R.string.upload_success),
                        resources.getString(R.string.upload_success_msg),
                        resources.getString(R.string.next),
                        state)
                    else showDialog(resources.getString(R.string.upload_failure),
                        resources.getString(R.string.upload_failure_msg),
                        resources.getString(R.string.retry),
                        false)
                })
            }
        }

    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showDialog(title: String, message: String, instruction: String, state: Boolean) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(instruction) { _, _ ->
                if (state) {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    //do nothing
                }
            }
            create()
            show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}