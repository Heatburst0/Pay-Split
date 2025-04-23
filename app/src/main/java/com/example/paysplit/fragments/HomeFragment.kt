package com.example.paysplit.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.paysplit.CreateActivity
import com.example.paysplit.MainActivity
import com.example.paysplit.R
import com.example.paysplit.adapters.PaySplitAdapter
import com.example.paysplit.databinding.FragmentHomeBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.PaySplit
import com.example.paysplit.models.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.PaymentApp
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import dev.shreyaspatil.easyupipayment.model.TransactionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    companion object{
        const val create_code=23
    }
    private lateinit var binding : FragmentHomeBinding
    private lateinit var loggedinUser : User
    private var payeeUPIid : String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = FragmentHomeBinding.inflate(layoutInflater)
        binding.swipeLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            FirestoreClass().getPaySplits(this, loggedinUser.email)
            Toast.makeText(activity,"Refreshed", Toast.LENGTH_SHORT).show()
            binding.swipeLayout.isRefreshing = false
        })
        FirestoreClass().loadDataHomeFragment(this,false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment


        binding.createBtn.setOnClickListener {
            val intent = Intent(activity,CreateActivity::class.java)
            intent.putExtra("user",(activity as MainActivity).loggedinUser)
            Toast.makeText(activity as MainActivity,"create btn in fragment",Toast.LENGTH_SHORT).show()
            startActivityForResult(intent,create_code)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==RESULT_OK && requestCode ==create_code){
            Toast.makeText(activity as MainActivity,"Working in fragment",Toast.LENGTH_SHORT).show()
            FirestoreClass().getPaySplits(this, loggedinUser.email)
        }
    }
    fun setPaySplits(list : ArrayList<PaySplit>){
        if(list.size>0){
            binding.revPaysplitsHome.visibility = View.VISIBLE
            val adapter = PaySplitAdapter(activity as MainActivity,this,list,loggedinUser)
            (activity as MainActivity).homeFrag = this
            binding.revPaysplitsHome.layoutManager = LinearLayoutManager(activity as MainActivity)
            binding.revPaysplitsHome.setHasFixedSize(true)
            binding.revPaysplitsHome.adapter = adapter
            adapter.setOnClickListener(object : PaySplitAdapter.OnClickListener{
                override fun onPayButton(createdby : User, amount: HashMap<String, Double>) {

                    try {
                        makePayment(createdby.name,amount[loggedinUser.id].toString())
                    }catch (e: Exception){
                        Toast.makeText(activity as MainActivity,e.message,Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onViewPaySplitButton(createdby: User, amount: HashMap<String, Double>) {
                    val dialog = BottomSheetDialog(activity as MainActivity)
                    dialog.setContentView(R.layout.view_paysplit)
                    dialog.setCancelable(true)
                    val username = dialog.findViewById<TextView>(R.id.username_member_simple)
                    username?.text = createdby.name
                    val email = dialog.findViewById<TextView>(R.id.email_member_simple)
                    email?.text = createdby.email
                    if(createdby.image.isNotEmpty()){
                        Glide
                            .with(context as MainActivity)
                            .load(createdby.image)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(dialog.findViewById<ImageView>(R.id.iv_member_user_image_simple)!!)
                    }
                    dialog.show()
                }

            })
        }
    }
    private fun makePayment(name : String,amount : String){
        val vib = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vib.vibrate(200)
        }
        val transcId = System.currentTimeMillis().toString()
        val desc = "Easy UPI payment by Pay Split"
        lifecycleScope.launch {
            if(payeeUPIid.isNullOrEmpty()) FirestoreClass().loadDataHomeFragment(this@HomeFragment,true)
            (activity as MainActivity).showProgressDialog("Please wait")
            delay(500)
            (activity as MainActivity).cancelDialog()
            try{
                val easyUpiPayment = EasyUpiPayment(activity as MainActivity) {
                    this.paymentApp = PaymentApp.ALL
                    this.payeeVpa = payeeUPIid
                    this.payeeName = name
                    this.transactionId = transcId
                    this.transactionRefId = transcId
                    this.payeeMerchantCode = transcId
                    this.description = desc
                    this.amount = amount
                }
                // END INITIALIZATION

                // Register Listener for Events
                easyUpiPayment.setPaymentStatusListener(object : PaymentStatusListener{
                    override fun onTransactionCancelled() {

                    }

                    override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
                        if(transactionDetails.transactionStatus==TransactionStatus.SUBMITTED){
                            Toast.makeText(activity,"Payment success",Toast.LENGTH_SHORT).show()
                        }
                    }

                })

                // Start payment / transaction
                easyUpiPayment.startPayment()
            }catch(e: Exception){
                Log.e("Payment error",e.message.toString())
            }
        }


    }
    fun setUser(user : User,upi : Boolean){
        if(!upi){
            loggedinUser = user
            FirestoreClass().getPaySplits(this, loggedinUser.email)
        }else payeeUPIid = user.upiid
    }
}