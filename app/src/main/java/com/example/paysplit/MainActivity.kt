package com.example.paysplit

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.paysplit.databinding.ActivityMainBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.User
import com.example.paysplit.viewpager.ViewPagerAdapter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private var prevMenuItem : MenuItem?=null
    lateinit var loggedinUser : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        FirestoreClass().loadUserData(this)
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
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            11
        )
        if(!isOnline(this)){
            Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            toggleDrawer()
        }else super.onBackPressed()
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
            R.id.create_btn_drawer ->{
                val intent = Intent(this, CreateActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun setToken(){
        if(loggedinUser.fcmtoken.isEmpty()){
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if(it.isSuccessful){
                    val token = it.getResult()
                    val hm : HashMap<String,Any> = HashMap()
                    hm["fcmtoken"] =token
                    FirestoreClass().updateUserProfileData(this,hm)
                }
            }

        }

    }
    fun setUserdata(user : User){
        val hv = binding.navView.getHeaderView(0)
        loggedinUser = user
        setToken()
        hv.findViewById<TextView>(R.id.username_header).text = user.name
        hv.findViewById<TextView>(R.id.upiid_header).text = "UPI id - ${user.upiid}"
        if(user.image.isNotEmpty()){
            Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(hv.findViewById<ImageView>(R.id.iv_header_img))
        }

    }
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }



}