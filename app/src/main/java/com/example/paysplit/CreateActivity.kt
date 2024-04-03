package com.example.paysplit

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paysplit.adapters.PaySplitMemberAdapter
import com.example.paysplit.databinding.ActivityCreateBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.PaySplit
import com.example.paysplit.models.PaySplitMember
import com.example.paysplit.models.User
import com.google.android.material.textfield.TextInputLayout

class CreateActivity : BaseActivity() {
    private lateinit var binding : ActivityCreateBinding
    var members : ArrayList<PaySplitMember> = ArrayList()
    private var membersVis : HashSet<String> = HashSet()
    private var totalAmount =0.0
    private lateinit var loggedinUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        if(intent.hasExtra("user")){
            loggedinUser = intent.getParcelableExtra("user")!!

        }
        binding.addMemberBtn.setOnClickListener {
            searchDialog()
        }
        binding.etTotalAmount.setOnClickListener {
            openDialogEditTotalAmount()
        }
        binding.createPayBtn.setOnClickListener {
            createPaySplit()
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
        val member = PaySplitMember(user.id,user.name, user.email,"0",user.image)
        membersVis.add(user.email)
        members.add(member)
        populateMembers()}

    }
    private fun populateMembers(){
        binding.rvMembers.visibility = View.VISIBLE
        binding.rvMembers.layoutManager = LinearLayoutManager(this)
        binding.rvMembers.isNestedScrollingEnabled = false
        binding.rvMembers.setHasFixedSize(true)
        val adapter = PaySplitMemberAdapter(this,members,totalAmount)
        binding.rvMembers.adapter = adapter
//        adapterPro=adapter
        adapter.setOnClickListener(object : PaySplitMemberAdapter.OnClickListener{
            override fun removeUser(position: Int, user: PaySplitMember,list : ArrayList<PaySplitMember>) {
                Toast.makeText(this@CreateActivity,"Remove user",Toast.LENGTH_SHORT).show()
                members.removeAt(position)
                membersVis.remove(user.email)
                totalAmount
                adapter.notifyItemRemoved(position)
                populateMembers()
            }

            override fun editamount(pos: Int, lis: ArrayList<PaySplitMember>,prevAmount: Double) {
                openDialogEditMemberAmount(pos,lis,adapter,prevAmount)
            }


        })
    }
    private fun createPaySplit(){
        val title = binding.titlePaysplit.text.toString()
        if(members.size>0 && title.isNotEmpty()){
            var totalSum=0.0
            val assignedTo = ArrayList<String>(membersVis)
            val amountMembers : HashMap<String,Double> = HashMap()
            for(i in members){
                var amount=0.0
                amount = if(i.amount.toDouble()==0.0) totalAmount/members.size
                else i.amount.toDouble()
                amountMembers[i.id]=amount
                totalSum+=amount
            }
            if(totalSum==0.0){
                Toast.makeText(this,"Please assign amount to member(s)",Toast.LENGTH_SHORT).show()
                return
            }
            val paysplit = PaySplit(title=title,createdBy=loggedinUser, createdOn = System.currentTimeMillis().toString(), totalamount = totalSum, assignedTo = assignedTo, amountMembers = amountMembers)
            showProgressDialog()
            FirestoreClass().addPaySplit(this,paysplit)
        }else{
            if(title.isEmpty()){
                binding.titlePaysplit.setError("Please add a title")
                }
            else Toast.makeText(this,"Please add member(s)",Toast.LENGTH_SHORT).show()

        }
    }

    fun addPaySplitSuccess(){
        cancelDialog()
        finish()
    }
    private fun openDialogEditMemberAmount(pos: Int, list : ArrayList<PaySplitMember>, adapter: PaySplitMemberAdapter,prevAmount : Double){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.search_user_dialog)
        dialog.findViewById<TextView>(R.id.dialog_header).setText("Add extra amount")
        dialog.findViewById<TextInputLayout>(R.id.tl_et).setHint("Amount")
        val amountEt = dialog.findViewById<AppCompatEditText>(R.id.et_email_member)
        amountEt.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL


        dialog.findViewById<AppCompatButton>(R.id.btn_addmember).setText("Done")
        dialog.findViewById<AppCompatButton>(R.id.btn_addmember).setOnClickListener {
            val amount = amountEt.text.toString()
            if(amount.isEmpty() || amount.toDouble()==0.0){
                amountEt.setError("Please fill this")
            }else{
//                totalAmount = totalAmount + amount.toDouble() - members.get(pos).amount.toDouble()

                members.get(pos).amount=(amount.toDouble()+prevAmount).toString()
                list[pos].amount=(amount.toDouble()+prevAmount).toString()

                adapter.notifyItemChanged(pos)
                dialog.dismiss()
            }
        }
        dialog.show()

    }
    private fun openDialogEditTotalAmount(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.search_user_dialog)
        dialog.findViewById<TextView>(R.id.dialog_header).setText("Set total amount")
        dialog.findViewById<TextInputLayout>(R.id.tl_et).setHint("Amount")
        val amountEt = dialog.findViewById<AppCompatEditText>(R.id.et_email_member)
        amountEt.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL


        dialog.findViewById<AppCompatButton>(R.id.btn_addmember).setText("Done")
        dialog.findViewById<AppCompatButton>(R.id.btn_addmember).setOnClickListener {
            val amount = amountEt.text.toString()
            if(amount.isEmpty()){
                amountEt.setError("Please fill this")
            }else{
                binding.etTotalAmount.setText("₹ $amount")
                totalAmount = amount.toDouble()
                if(membersVis.size>0) populateMembers()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

}