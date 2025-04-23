package com.example.paysplit

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paysplit.adapters.MealMembersAdapter
import com.example.paysplit.adapters.MealsAdapter
import com.example.paysplit.adapters.PaySplitMemberAdapter
import com.example.paysplit.adapters.SearchMemberAdapter
import com.example.paysplit.databinding.ActivityCreateBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.Meal
import com.example.paysplit.models.PaySplit
import com.example.paysplit.models.PaySplitMember
import com.example.paysplit.models.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.auth.oauth2.GoogleCredentials
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class CreateActivity : BaseActivity() {
    private lateinit var binding : ActivityCreateBinding
    var members : ArrayList<PaySplitMember> = ArrayList()
    private var membersVis : HashMap<String,PaySplitMember> = HashMap()
    private var usersToken : ArrayList<String> = ArrayList()
    private var totalAmount = 0.0
    private lateinit var loggedinUser: User
    private lateinit var mealPricePerMember : HashMap<String,Double>
    private val mealsList : ArrayList<Meal> = ArrayList()
    private val searchUsers : ArrayList<User> = ArrayList()
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
        binding.additemBtn.setOnClickListener {
            if(members.size==0){
                Toast.makeText(this,"First add all the members",Toast.LENGTH_SHORT).show()
            }
            else openDialogCreateItem(-1)
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
        dialog.findViewById<AppCompatButton>(R.id.btn_addmember).visibility = View.GONE
        val editText = dialog.findViewById<AppCompatEditText>(R.id.et_email_member)

        liveSearchingUser(editText,dialog)
        dialog.setCancelable(true)
        dialog.setOnDismissListener {
            searchUsers.clear()
        }
        dialog.show()
    }
    private fun liveSearchingUser(editText: AppCompatEditText,dialog: Dialog){
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                searchUsers.clear()
                if(p0.toString().isNotEmpty()){
                    FirestoreClass().getMemberDetails(this@CreateActivity,p0.toString()){
                        val searchResultRV = dialog.findViewById<RecyclerView>(R.id.search_result_rv)
                        searchResultRV.layoutManager =
                            LinearLayoutManager(this@CreateActivity)
                        searchResultRV.setHasFixedSize(true)
                        val adapter = SearchMemberAdapter(this@CreateActivity, searchUsers)
                        searchResultRV.adapter = adapter
                        adapter.setOnClickListener(object : SearchMemberAdapter.OnClickListener{
                            override fun onClick(pos: Int) {
                                foundmember(searchUsers[pos])
                                dialog.dismiss()
                            }

                        })
                    }
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }
    fun foundmember(user : User?){
        searchUsers.clear()
        if(membersVis.contains(user!!.id)){
            Toast.makeText(this,"user already added",Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this,"user found",Toast.LENGTH_SHORT).show()
            usersToken.add(user.fcmtoken)
        val member = PaySplitMember(user.id,user.name, user.email,0.0,user.image)

        membersVis[user.id]=member
        members.add(member)
        populateMembers()}

    }
    fun foundmembers(list : ArrayList<User>){
        searchUsers.addAll(list)

    }

    private fun populateMembers(){
        binding.rvMembers.visibility = View.VISIBLE
        binding.rvMembers.layoutManager = LinearLayoutManager(this)
        binding.rvMembers.isNestedScrollingEnabled = false
        binding.rvMembers.setHasFixedSize(true)
        val adapter = PaySplitMemberAdapter(this,members,totalAmount)
        binding.rvMembers.adapter = adapter

        adapter.setOnClickListener(object : PaySplitMemberAdapter.OnClickListener{
            override fun removeUser(position: Int, user: PaySplitMember,list : ArrayList<PaySplitMember>) {
                Toast.makeText(this@CreateActivity,"Remove user",Toast.LENGTH_SHORT).show()
                members.removeAt(position)
                usersToken.removeAt(position)
                membersVis.remove(user.id)
                removeMember(user.id)

            }

            override fun editamount(pos: Int, lis: ArrayList<PaySplitMember>,prevAmount: Double) {
                openDialogEditMemberAmount(pos,lis,adapter,prevAmount)
            }


        })
    }
    private fun removeMember(id : String){
        for(i in mealsList){
            if(i.mealPricePerMember.containsKey(id) && i.shared){
                i.mealPricePerMember.remove(id)
                for(k in i.mealPricePerMember){
                    membersVis[k.key]!!.amount-= (i.price*i.quantity)/(i.mealPricePerMember.size+1)
                    membersVis[k.key]!!.amount+= (i.price*i.quantity)/i.mealPricePerMember.size
                }
            }
        }
        populateMembers()
    }
    private fun populateMeals(shared : Boolean,meal : Meal){

        binding.rvMeals.visibility = View.VISIBLE
        binding.rvMeals.layoutManager = LinearLayoutManager(this@CreateActivity)
        val adapter = MealsAdapter(this,mealsList)
        binding.rvMeals.adapter=adapter
        binding.rvMeals.setHasFixedSize(true)
        adapter.setOnClickListener(object : MealsAdapter.OnClickListener{
            override fun onEdit(pos: Int) {
                openDialogCreateItem(pos)
            }

        })
        for(i in meal.mealPricePerMember){
            if(shared)
                membersVis[i.key]!!.amount+=(meal.price*meal.quantity)/mealPricePerMember.size
            else
                membersVis[i.key]!!.amount+=meal.price*meal.quantity*i.value
        }
//
        populateMembers()


    }

    private fun populateMealMembers(shared : Boolean,rv : RecyclerView,hm : HashMap<String,Double>?){
        rv.layoutManager = LinearLayoutManager(this@CreateActivity)
        mealPricePerMember = if(hm!=null){
            HashMap(hm)
        }else HashMap()
        val adapter = MealMembersAdapter(-1,this@CreateActivity,members,shared,mealPricePerMember)
        rv.setHasFixedSize(true)
        rv.adapter = adapter
    }

    private fun createPaySplit(){
        val title = binding.titlePaysplit.text.toString()
        if(members.size>0 && title.isNotEmpty()){
            var totalSum=0.0
            val assignedTo = ArrayList<String>()
            for(i in membersVis.values){
                assignedTo.add(i.email)
            }
            val amountMembers : HashMap<String,Double> = HashMap()
            for(i in members){
                var amount=0.0
                amount = if(i.amount==0.0) totalAmount/members.size
                else i.amount
                amountMembers[i.id]=amount
                totalSum+=amount
            }
            if(totalSum==0.0){
                Toast.makeText(this,"Please assign amount to member(s)",Toast.LENGTH_SHORT).show()
                return
            }
            val paysplit = PaySplit(title=title,createdBy=loggedinUser, createdOn = System.currentTimeMillis().toString(), totalamount = totalSum, assignedTo = assignedTo, amountMembers = amountMembers)
            showProgressDialog("Creating Pay Split")
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
        for(i in usersToken){
            sendNotification(i)
        }
        setResult(RESULT_OK)
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

                members.get(pos).amount=(amount.toDouble()+prevAmount)
                list[pos].amount=(amount.toDouble()+prevAmount)

                adapter.notifyItemChanged(pos)
                dialog.dismiss()
            }
        }
        dialog.show()

    }

    private fun openDialogCreateItem(pos : Int){
        //Creating Dialog


        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.create_meal_layout)
        dialog.setOnShowListener{
            val dialog = it as BottomSheetDialog
            val bs = dialog.findViewById<View>(R.id.meal_layout)
            bs?.let{
                dialog.behavior.peekHeight = it.height
                it.parent.parent.requestLayout()
            }
        }

        //Populating members
        val rv = dialog.findViewById<RecyclerView>(R.id.rv_members_meal)
        val title = dialog.findViewById<TextView>(R.id.title_meal_set)
        val price = dialog.findViewById<TextView>(R.id.price_meal_set)
        val quan = dialog.findViewById<TextView>(R.id.quantity_meal)
        val checkbox = dialog.findViewById<CheckBox>(R.id.shared_meal)
        if(pos!=-1){
            title!!.text = mealsList[pos].name
            price!!.text = mealsList[pos].price.toString()
            quan!!.text = mealsList[pos].quantity.toString()
            checkbox!!.isChecked = mealsList[pos].shared
            populateMealMembers(checkbox.isChecked,rv!!,mealsList[pos].mealPricePerMember)
        }
        else populateMealMembers(
            checkbox!!.isChecked,
            rv!!,
            HashMap())
        dialog.findViewById<CheckBox>(R.id.shared_meal)!!.setOnClickListener {
            populateMealMembers(checkbox.isChecked,rv,null)
        }
        //Creating meal

        dialog.findViewById<AppCompatButton>(R.id.createMeal_btn)!!.setOnClickListener {
           if(title!!.text.toString().isEmpty()){
               title.setError("Please fill this")
           }else if(price!!.text.toString().isEmpty()){
               title.setError("Please fill this")
           }else if(quan!!.text.toString().isEmpty()){
               quan.setError("Please fill this")
           }else{
               val newMeal = Meal(
                   title.text.toString(),
                   quan.text.toString().toInt(),
                   price.text.toString().toDouble(),
                   checkbox.isChecked,
                   HashMap(mealPricePerMember)
               )
               if(pos==-1) {
                   mealsList.add(newMeal)
                   populateMeals(checkbox.isChecked, newMeal)
               }else{
                   val prevMeal = mealsList[pos]
                   editMeal(pos,prevMeal,newMeal)
               }
               dialog.dismiss()
           }
        }
        dialog.show()
    }

    private fun editMeal(pos : Int,prev : Meal,newMeal : Meal){
        Log.i("prev meal",prev.toString())
        Log.i("new meal",newMeal.toString())
        for(i in members){
            if(prev.mealPricePerMember.containsKey(i.id)){
                if(!prev.shared)
                    i.amount-=prev.mealPricePerMember[i.id]!!*prev.price*prev.quantity
                else
                    i.amount-=(prev.quantity*prev.price)/prev.mealPricePerMember.size
            }
            if(mealPricePerMember.containsKey(i.id)){
                if(!newMeal.shared)
                    i.amount+=mealPricePerMember[i.id]!!*newMeal.price*newMeal.quantity
                else i.amount+=(newMeal.price*newMeal.quantity)/mealPricePerMember.size
            }
        }
        mealsList.set(pos,newMeal)
        Toast.makeText(this,"Item updated",Toast.LENGTH_SHORT).show()
        populateMembers()
    }
    //Notification work
    fun sendNotification(fcmtoken : String){
        Toast.makeText(this@CreateActivity,"inside notify", Toast.LENGTH_SHORT).show()
        try{
            val jsonobj= JSONObject()
            val notiObj= JSONObject()
            notiObj.put("title","You have been added to a Pay Split")
            notiObj.put("body","${loggedinUser.name} added you to a Pay Split")

            val dataObj = JSONObject()
            dataObj.put("id",loggedinUser.id)
            val messageJSON = JSONObject()
            messageJSON.put("token",fcmtoken)
            messageJSON.put("notification",notiObj)
            messageJSON.put("data",dataObj)
            jsonobj.put("message",messageJSON)
            callApi(jsonobj)
        }catch (e : Exception){
            Log.e("Error Notify",e.message.toString())
            Toast.makeText(this@CreateActivity,"Error",Toast.LENGTH_SHORT).show()

        }

    }
    fun callApi(jsonobj : JSONObject){
        val JSON : okhttp3.MediaType = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url ="https://fcm.googleapis.com/v1/projects/pay-split-6f8c1/messages:send"
        val body = RequestBody.create(JSON,jsonobj.toString())
        val inputStream = assets.open("pay-split-6f8c1-firebase-adminsdk-ii4sw-cf0d2b6b7d.json")

        if (inputStream == null) {
            Log.e("FCM File", "Service account file not found!")
            return
        }else Log.i("File found",inputStream.toString())


        val credentials = GoogleCredentials
            .fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        credentials.refresh()
        val token = credentials.accessToken.tokenValue
        Log.i("OAuth2 Token", token ?: "Token is null")
        val request : Request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization","Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@CreateActivity,"Failed Notify",Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call, response: Response) {
            }
        })


    }
    //testing


}