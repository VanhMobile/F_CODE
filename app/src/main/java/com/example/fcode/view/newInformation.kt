package com.example.fcode.view

import android.Manifest
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.fcode.R
import com.example.fcode.databinding.ActivityNewInformationBinding
import com.example.fcode.databinding.DialogBottomSheetBinding
import com.example.fcode.view.model.User
import com.example.fcode.view.model.UserInformation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.util.Calendar


class newInformation : AppCompatActivity() {

    private lateinit var newInformationBinding: ActivityNewInformationBinding
    private lateinit var dialog: BottomSheetDialog
    private lateinit var imgUri: Uri
    private var selectedDate = "18/05/2003"
    val someActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imgUri = result?.data?.data!!
                newInformationBinding.imgAvt.setImageURI(imgUri)
                dialog.dismiss()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newInformationBinding = ActivityNewInformationBinding.inflate(layoutInflater)
        setContentView(newInformationBinding.root)
        initView()
    }

    private fun initView() {
        val bundle = intent.extras
        val userName = bundle?.getString("UserName")
        val password = bundle?.getString("Password")
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val moth = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        newInformationBinding.apply {

            btnShowGallery.setOnClickListener {
                createDialog()
            }
            tvBirthDay.setOnClickListener {
                val datePickerDialog = DatePickerDialog(this@newInformation,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        selectedDate = "$dayOfMonth/${month + 1}/$year"
                        tvBirthDay.text = selectedDate
                    },
                    year,
                    moth,
                    day
                )
                datePickerDialog.setTitle("Ngày sinh")
                datePickerDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                datePickerDialog.show()
            }
            btnSaveInformation.setOnClickListener {
                val progressDialog = ProgressDialog(this@newInformation)
                progressDialog.setMessage("Loading")
                progressDialog.setCancelable(false)
                progressDialog.show()
                val fullName = edtName.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val numberPhone = edtPhone.text.toString().trim()
                var sex = ""
                if (rbMale.isChecked) {
                    sex = "Nam"
                } else {
                    sex = "Nữ"
                }
                if (userName != null && password != null) {
                    try {
                        val user = User(
                            userName,
                            password,
                            UserInformation(
                                fullName,
                                userName,
                                selectedDate.toString(),
                                email,
                                numberPhone,
                                sex
                            )
                        )
                        val storage = FirebaseStorage.getInstance().getReference("$userName")
                        storage.putFile(imgUri)
                        val db = FirebaseDatabase.getInstance()
                        db.reference.child("Users").child(userName).setValue(user)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@newInformation,
                                    "Đăng ký thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (progressDialog.isShowing) progressDialog.dismiss()
                                startActivity(Intent(this@newInformation, Login::class.java))
                            }
                    }catch (e :Exception){
                        Toast.makeText(
                            this@newInformation,
                            "Bạn chưa chọn ảnh đại diện",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
                }
            }
        }

    }


    private fun createDialog() {
        val dialogView = DialogBottomSheetBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(this, R.style.BottomSheetDialogThem)
        dialog.setContentView(dialogView.root)
        dialogView.tvFolderImg.setOnClickListener {
            checkPermissionGallry()
        }
        dialog.show()
    }

    private fun checkPermissionGallry() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(
                object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        gallery()
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@newInformation,
                            "bạn chưa cấp quyền",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        permissionRationaleShouldBeShown()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        permissionRationaleShouldBeShown()
                    }

                }
            ).onSameThread().check()
    }

    private fun permissionRationaleShouldBeShown() {
        AlertDialog.Builder(this)
            .setMessage("Bạn chưa cấp quyền cho máy ảnh hãy đến cài đặt")
            .setPositiveButton("đến cài đặt") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            .setNegativeButton("thoát") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        someActivityForResult.launch(intent)
    }


}

