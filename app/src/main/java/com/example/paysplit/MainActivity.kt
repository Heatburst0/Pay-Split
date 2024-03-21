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
import androidx.viewpager2.widget.ViewPager2
import com.example.paysplit.databinding.ActivityMainBinding
import com.example.paysplit.fragments.HistoryFragment
import com.example.paysplit.fragments.HomeFragment
import com.example.paysplit.fragments.ProfileFragment
import com.example.paysplit.viewpager.ViewPagerAdapter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private var prevMenuItem : MenuItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        val adapter = ViewPagerAdapter(this)
        binding.vPager.adapter = adapter
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home_btn -> {
                    binding.vPager.setCurrentItem(0)
                }
                R.id.profile_btn ->{
                    binding.vPager.setCurrentItem(1)
                }
                else->binding.vPager.setCurrentItem(2)

            }
            true
        }
        binding.bottomNavigationView.itemActiveIndicatorColor = ColorStateList.valueOf(Color.WHITE)
        binding.bottomNavigationView.menu.getItem(0).setChecked(true);
        binding.navView.setNavigationItemSelectedListener(this)
        binding.vPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(prevMenuItem!=null){
                    prevMenuItem!!.setChecked(false)
                }else{
                    binding.bottomNavigationView.menu.getItem(0).setChecked(false)
                }
                binding.bottomNavigationView.menu.getItem(position).setChecked(true)
                prevMenuItem=binding.bottomNavigationView.menu.getItem(position)
            }
        })

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