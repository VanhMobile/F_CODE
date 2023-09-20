package com.example.fcode.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.fcode.R
import com.example.fcode.databinding.ActivitySignUpBinding
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

class SignUp : AppCompatActivity() {

    private lateinit var signUpBinding: ActivitySignUpBinding
    private val RC_SIGN_IN = 123
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var userName: String
    private lateinit var password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)

        initView()
    }

    private fun initView() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        signUpBinding.btnLoginWithGoogle.setOnClickListener {
            loginWithGoogle()
        }
        signUpBinding.btnSignUp.setOnClickListener {
            userName = signUpBinding.edtUsername.text.toString().trim()
            password = signUpBinding.edtPassword.text.toString().trim()
            if (userName.isEmpty()){
                signUpBinding.edtUsername.error = "User name rỗng"
                return@setOnClickListener
            }
            if (password.isEmpty()){
                signUpBinding.edtPassword.error = "Password rỗng"
                return@setOnClickListener
            }
            val intent = Intent(this@SignUp,newInformation::class.java)
            val bundle = Bundle()
            bundle.putString("UserName",userName)
            bundle.putString("Password",password)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun loginWithGoogle() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent,RC_SIGN_IN)
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
                        ,UserInformation(name.toString(),name.toString(),birthdate.toString()
                        ,numberPhone.toString(),email.toString(),gender.toString()))
                    val storage = FirebaseStorage.getInstance().getReference("$name")
                    storage.putFile(photoUrl)
                    db.reference.child("Users").child(name.toString()).setValue(account)
                    //
                    startActivity(Intent(this@SignUp,Home::class.java))
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