package com.example.fcode.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fcode.R
import com.example.fcode.databinding.ActivityHomeBinding
import com.example.fcode.view.ui.home.HomeFragment
import com.example.fcode.view.ui.information.informationFragment
import com.example.fcode.view.ui.note.noteFragment
import com.example.fcode.view.ui.setting.settingFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView : NavigationView = binding.navView

        val toggle = ActionBarDrawerToggle(this,drawerLayout,
            binding.appBarHome.toolbar,R.string.open,R.string.close)
        toggle.syncState()

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_home,HomeFragment())
                .commit()
            navView.setCheckedItem(R.id.nav_home)
        }
        navView.setNavigationItemSelectedListener(this)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home ->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_home,HomeFragment())
                    .commit()
            }
            R.id.nav_information ->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_home,informationFragment())
                    .commit()
            }
            R.id.nav_note ->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_home,noteFragment())
                    .commit()
            }
            R.id.nav_setting ->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_home,settingFragment())
                    .commit()
            }
            R.id.nav_logout ->{
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Thông báo")
                dialog.setMessage("Bạn có Chắc chắn muốn đăng xuất")
                dialog.setPositiveButton("oke"){ dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
                    googleSignInClient.signOut();
                    finish()
                }
                dialog.setNegativeButton("cancel"){dialog,which ->
                    dialog.cancel()
                }
                dialog.show()
            }
        }

        return true
    }
}