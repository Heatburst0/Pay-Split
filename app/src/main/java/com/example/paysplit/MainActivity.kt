package com.example.paysplit

import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.paysplit.databinding.ActivityMainBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.fragments.HistoryFragment
import com.example.paysplit.fragments.HomeFragment
import com.example.paysplit.fragments.ProfileFragment
import com.example.paysplit.models.PaySplit
import com.example.paysplit.models.User
import com.example.paysplit.utils.Constants
import com.example.paysplit.viewpager.ViewPagerAdapter
import com.google.android.material.navigation.NavigationView
import com.google.common.net.MediaType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


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
        sendNotification()
    }



    //Notification code
    fun sendNotification(){
        try{
            val jsonobj : JSONObject = JSONObject()
            val notiObj : JSONObject = JSONObject()
            notiObj.put("title","You have been added to a Pay Split")
            notiObj.put("body","Krishna added you to a Pay Split")

//            notiObj.put("sound","default")

            val dataObj : JSONObject = JSONObject()
            dataObj.put("id",loggedinUser.id)
            jsonobj.put("notification",notiObj)
            jsonobj.put("data",dataObj)
            jsonobj.put("to",loggedinUser.fcmtoken)
            callApi(jsonobj)
        }catch (e : Exception){
            Log.e("Error Notify",e.message.toString())
            Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()

        }

    }
    fun callApi(jsonobj : JSONObject){
        val JSON : okhttp3.MediaType = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url ="https://fcm.googleapis.com/fcm/send"
        val body = RequestBody.create(JSON,jsonobj.toString())
        val request : Request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization","Bearer ${Constants.apiKey}")
            .build()
        Handler().postDelayed({
            client.newCall(request).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Toast.makeText(this@MainActivity,"Failed",Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call, response: Response) {

                }
            })
        },2000)

    }
}