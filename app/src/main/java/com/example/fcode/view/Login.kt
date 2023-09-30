package com.example.fcode.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.fcode.R
import com.example.fcode.databinding.ActivityLoginBinding
import com.example.fcode.databinding.DialogNeedHelpBinding
import com.example.fcode.view.model.User
import com.example.fcode.view.model.UserInformation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Login : AppCompatActivity() {

    private lateinit var logInBinding: ActivityLoginBinding
    private lateinit var users: MutableList<User>
    private lateinit var dialogBinding: DialogNeedHelpBinding
    private var checkLogin: Boolean = false
    private var numberOfLogin: Int = 0
    private val RC_SIGN_IN = 123
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logInBinding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        setContentView(logInBinding.root)
        initView()
    }

    private fun initView() {
        users = mutableListOf()
        loadAccount()
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestProfile()
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        logInBinding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@Login, forgotPassword::class.java))
        }
        logInBinding.tvSignup.setOnClickListener {
            startActivity(Intent(this@Login, SignUp::class.java))
        }
        logInBinding.btnLogin.setOnClickListener {
            loginWithAccount()
        }
        logInBinding.btnLoginWithGoogle.setOnClickListener {
            loginWithGoogle()
            // sửa thêm
            // lần 2
            //lần 3
        }
    }

    private fun loginWithGoogle() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent,RC_SIGN_IN)
    }

    private fun loginWithAccount() {
        val userName = logInBinding.edtEmail.text.toString().trim()
        val password = logInBinding.edtPassword.text.toString().trim()
        if (userName.isEmpty()) {
            logInBinding.edtEmail.error = "User name rỗng"
            return
        }
        if (password.isEmpty()) {
            logInBinding.edtPassword.error = "Password rỗng"
            return
        }
        checkLogin(userName, password)
        if (checkLogin) {
            checkLogin = false
            startActivity(Intent(this@Login, Home::class.java))
            Toast.makeText(this@Login, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
        } else {
            if (numberOfLogin < 3) {
                Toast.makeText(
                    this@Login,
                    "Thông tin đăng nhập của bạn sai",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (numberOfLogin == 3) {
                createDialog()
            }
        }
    }

    private fun loadAccount() {
        val dataBase = FirebaseDatabase.getInstance().getReference("Users")
        dataBase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        users.add(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun createDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialogBinding = DialogNeedHelpBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnYes.setOnClickListener {
            startActivity(Intent(this@Login, forgotPassword::class.java))
        }
        dialog.show()
    }


    private fun checkLogin(userName: String, password: String) {
        for (user in users) {
            if (user.userName == userName && user.password == password) {
                checkLogin = true
            }
        }
        if (checkLogin) {
            numberOfLogin = 0
        } else {
            numberOfLogin += 1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException){
                Toast.makeText(this,"Đăng nhập thất bại",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Đăng nhập thành công, lấy thông tin người dùng
                    val user = auth.currentUser
                    val name = user?.displayName
                    val birthdate = user?.metadata?.creationTimestamp?.let { getBirthdateFromTimestamp(it) }
                    val gender = user?.metadata?.creationTimestamp?.let { getGenderFromTimestamp(it) }
                    val id = user?.uid
                    val photoUrl = user?.photoUrl!!
                    val numberPhone = user?.phoneNumber
                    val email = user?.email
                    // Lưu thông tin người dùng vào Firestore hoặc Realtime Database
                    val account = User(name.toString(),id.toString()
                        , UserInformation(name.toString(),name.toString(),birthdate.toString()
                            ,email.toString(),numberPhone.toString(),gender.toString())
                    )
                    val storage = FirebaseStorage.getInstance().getReference("$name")
                    storage.putFile(photoUrl)
                    val db = FirebaseDatabase.getInstance()
                    db.reference.child("Users").child(name.toString()).setValue(account)
                    //
                    startActivity(Intent(this@Login,Home::class.java))
                } else {
                    // Đăng nhập thất bại
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun getBirthdateFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun getGenderFromTimestamp(timestamp: Long): String {
        return if (timestamp % 2 == 0L) "Nữ" else "Nam"
    }
}