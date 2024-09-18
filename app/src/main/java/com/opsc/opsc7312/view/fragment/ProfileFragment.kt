package com.opsc.opsc7312.view.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentProfileBinding
import com.opsc.opsc7312.databinding.FragmentSettingsBinding
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.api.controllers.UserController
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.TimeOutDialog
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var userViewModel: UserController

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickPictureLauncher: ActivityResultLauncher<Intent>

    private lateinit var timeOutDialog: TimeOutDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE)


        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        userViewModel = ViewModelProvider(this).get(UserController::class.java)

        timeOutDialog = TimeOutDialog()

        // Load profile data
        loadProfileData()

        // Set up listeners
        binding.btnEditProfileImage.setOnClickListener { showImagePickerDialog() }
        binding.btnSave.setOnClickListener {
            saveProfileData()
        }

        binding.ChangePassword.setOnClickListener{
            showChangePasswordDialog()
        }

        // Initialize activity result launchers
        initializeActivityResultLaunchers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Profile")
    }

    private fun initializeActivityResultLaunchers() {
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    setProfileImage(it)
                    saveProfileImageToPreferences(it)
                }
            }
        }

        pickPictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    val inputStream = requireActivity().contentResolver.openInputStream(it)
                    val imageBitmap = BitmapFactory.decodeStream(inputStream)
                    setProfileImage(imageBitmap)
                    saveProfileImageToPreferences(imageBitmap)
                }
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImagePickerDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Select Profile Picture")
            .setItems(arrayOf("Take Photo", "Choose from Gallery")) { _, which ->
                when (which) {
                    0 -> checkAndRequestPermission(Manifest.permission.CAMERA) {
                        dispatchTakePictureIntent()
                    }
                    1 -> checkAndRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                        dispatchPickPictureIntent()
                    }
                }
            }
            .show()
    }

    private fun checkAndRequestPermission(permission: String, onGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 1)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    private fun dispatchPickPictureIntent() {
        Log.d("ProfileFragment", "Attempting to open gallery...")
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoIntent.type = "image/*"
        pickPictureLauncher.launch(pickPhotoIntent)
    }

    private fun loadProfileData() {
        val user = userManager.getUser()

        val username = user.username
        val email = user.email
        val profileImageBase64 = sharedPreferences.getString("profileImage", "")

        binding.username.setText(username)
        binding.email.setText(email)
        //passwordEditText.setText(password)

        // Load profile image if available
        if (!profileImageBase64.isNullOrEmpty()) {
            val imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            setProfileImage(bitmap)
        }
    }

    private fun saveProfileData() {
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        val user = userManager.getUser()
        val token = tokenManager.getToken()
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()
        //val password = passwordEditText.text.toString()

        val updatedUser = User(id = user.id, username = username, email = email)

        userViewModel.status.observe(viewLifecycleOwner){
            status ->

            if (status) {
                userManager.saveUser(updatedUser)
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Profile update successful!", hideProgressBar = true, )

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                }, 2000)

                //Toast.makeText(requireContext(), "Category creation successful", Toast.LENGTH_LONG).show()
            } else {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Profile update failed!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()


                }, 2000)
            }
        }

        if (token != null) {
            userViewModel.updateEmailAndUsername(token, user.id, updatedUser)
        }
    }

    private fun saveProfileImageToPreferences(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()
        val imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        with(sharedPreferences.edit()) {
            putString("profileImage", imageBase64)
            apply()
        }
    }

    private fun setProfileImage(bitmap: Bitmap) {
        //profileImageView.setImageBitmap(bitmap)
    }

    private fun showChangePasswordDialog(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.change_password_dialog, null)

        val closeBtn: ImageView = dialogView.findViewById(R.id.closeView)

        val newPassword: EditText = dialogView.findViewById(R.id.newPassword)

        val confirmPassword: EditText = dialogView.findViewById(R.id.confirmPassword)

        val saveBtn: Button = dialogView.findViewById(R.id.btnSave)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener{
            saveNewPassword(newPassword.text.toString(), confirmPassword.text.toString())
        }

        dialog.show()
    }

    private fun saveNewPassword(newPassword: String, confirmPassword: String) {
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        val user = userManager.getUser()
        val token = tokenManager.getToken()

        if(newPassword != confirmPassword){
            timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Confirm new password does not match", hideProgressBar = true)

            Handler(Looper.getMainLooper()).postDelayed({
                // Dismiss the dialog after the delay
                progressDialog.dismiss()

            }, 2000)

            return
        }

        //val updatedUser = User(id = user.id, username = username, email = email)

        userViewModel.status.observe(viewLifecycleOwner){
                status ->

            if (status) {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Password update successful!", hideProgressBar = true, )

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                }, 2000)

            } else {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Password update failed!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()


                }, 2000)
            }
        }

        if (token != null) {
            userViewModel.updatePassword(token, user.id, newPassword)
        }
    }
}
