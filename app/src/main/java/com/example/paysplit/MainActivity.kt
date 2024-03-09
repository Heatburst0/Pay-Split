package com.example.paysplit

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.paysplit.databinding.ActivityMainBinding
import com.example.paysplit.fragments.HistoryFragment
import com.example.paysplit.fragments.HomeFragment
import com.example.paysplit.fragments.ProfileFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val historyFragment = HistoryFragment()
        val profileFragment = ProfileFragment()
        setFragment(homeFragment)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home_btn -> {
                    setFragment(homeFragment)
                    binding.toolbarMainActivity.title = "Home"
                }
                R.id.history_btn ->{
                    setFragment(historyFragment)
                    binding.toolbarMainActivity.title
                }
                R.id.profile_btn -> setFragment(profileFragment)

            }
            true
        }
        binding.bottomNavigationView.itemActiveIndicatorColor = ColorStateList.valueOf(Color.WHITE)
        setupActionBar()
        binding.navView.setNavigationItemSelectedListener(this)


    }
    private fun setFragment(frag : Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,frag)
            commit()
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMainActivity)
        binding.toolbarMainActivity.setNavigationIcon(R.drawable.ic_more)

        binding.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signout_btn_drawer->{
                auth.signOut()
                val intent = Intent(this, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                Toast.makeText(this@MainActivity,"You have signed out",Toast.LENGTH_SHORT).show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}