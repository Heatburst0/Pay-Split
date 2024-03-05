package com.example.paysplit

import android.app.Dialog
import android.health.connect.datatypes.units.Length
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paysplit.adapters.PaySplitMemberAdapter
import com.example.paysplit.databinding.ActivityCreateBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.PaySplitMember
import com.example.paysplit.models.User
import com.google.android.material.textfield.TextInputLayout

class CreateActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateBinding
    var members : ArrayList<PaySplitMember> = ArrayList()
    private var membersVis : HashSet<String> = HashSet()
    lateinit var adapterPro : PaySplitMemberAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        binding.addMemberBtn.setOnClickListener {
            searchDialog()
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCreate)
        binding.toolbarCreate.setNavigationIcon(R.drawable.ic_white_color_back_24dp)

        binding.toolbarCreate.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun searchDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.search_user_dialog)
        dialog.findViewById<AppCompatButton>(R.id.btn_addmember).setOnClickListener {
            val email  = dialog.findViewById<AppCompatEditText>(R.id.et_email_member).text.toString()
            if(email.trim().isEmpty()){
                dialog.findViewById<AppCompatEditText>(R.id.et_email_member).setError("Please fill this")
            }else{
                if(membersVis.contains(email)) Toast.makeText(this@CreateActivity,"user already added",Toast.LENGTH_SHORT).show()
                else FirestoreClass().getMemberDetails(this,email)
                dialog.cancel()
            }
        }
        dialog.setCancelable(true)
        dialog.show()
    }
    fun foundmember(user : User?){
        if(membersVis.contains(user!!.email)){
            Toast.makeText(this,"user already added",Toast.LENGTH_SHORT).show()
        }
        else {Toast.makeText(this,"user found",Toast.LENGTH_SHORT).show()
        val member = PaySplitMember(user!!.name, user.email,"0",user.image)
        membersVis.add(user.email)
        members.add(member)
        populateMembers()}

    }
    private fun populateMembers(){
        binding.rvMembers.visibility = View.VISIBLE
        binding.rvMembers.layoutManager = LinearLayoutManager(this)
        binding.rvMembers.isNestedScrollingEnabled = false
        val adapter = PaySplitMemberAdapter(this,members)
        binding.rvMembers.adapter = adapter
        adapterPro=adapter
        adapter.setOnClickListener(object : PaySplitMemberAdapter.OnClickListener{
            override fun removeUser(position: Int, user: PaySplitMember,list : ArrayList<PaySplitMember>) {
                Toast.makeText(this@CreateActivity,"Remove user",Toast.LENGTH_SHORT).show()
                members.removeAt(position)
                membersVis.remove(user.email)
                list.removeAt(position)
                adapter.notifyItemRemoved(position)
//                populateMembers()
            }


        })
    }

}