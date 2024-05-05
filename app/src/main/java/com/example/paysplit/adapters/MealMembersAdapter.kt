package com.example.paysplit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paysplit.R
import com.example.paysplit.models.PaySplitMember
import com.bumptech.glide.Glide
open class MealMembersAdapter(
    private val index : Int,
    private val context : Context,
    private val list : ArrayList<PaySplitMember>,
    private val shared : Boolean,
    private val mealPricePermember : HashMap<String,Double>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_meal_member,
                parent,
                false
            ))

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mem = list[position]
        if(mem.img.isNotEmpty()){
            Glide
                .with(context)
                .load(mem.img)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_member_user_image_meal))
        }

        holder.itemView.findViewById<TextView>(R.id.username_member_meal).text = mem.username
        if(!shared) {
            holder.itemView.findViewById<LinearLayout>(R.id.shared_view).visibility = View.GONE
            holder.itemView.findViewById<LinearLayout>(R.id.notSharedView).visibility=View.VISIBLE
            val counter = holder.itemView.findViewById<TextView>(R.id.meal_counter)
            if(mealPricePermember.containsKey(mem.id)){
                counter.text = mealPricePermember[mem.id]!!.toInt().toString()
            }
            holder.itemView.findViewById<ImageView>(R.id.increment).setOnClickListener {
                val prev: Int = counter.text.toString().toInt()
                counter.text = (prev + 1).toString()
                mealPricePermember[mem.id] = prev + 1.0
            }
            holder.itemView.findViewById<ImageView>(R.id.minus).setOnClickListener {
                val prev: Int = counter.text.toString().toInt()
                if (prev > 0) {
                    counter.text = (prev - 1).toString()
                    if (prev - 1 == 0) mealPricePermember.remove(mem.id)
                    else mealPricePermember[mem.id] = prev - 1.0
                }
            }
        }
        else{
            val checkbox = holder.itemView.findViewById<CheckBox>(R.id.checkbox_shared)
            holder.itemView.findViewById<LinearLayout>(R.id.shared_view).visibility = View.VISIBLE
            holder.itemView.findViewById<LinearLayout>(R.id.notSharedView).visibility=View.GONE
            if(mealPricePermember.containsKey(mem.id)){
                checkbox.isChecked=true
            }
            checkbox.setOnClickListener{
                if(mealPricePermember.containsKey(mem.id)){
                    mealPricePermember.remove(mem.id)
                    checkbox.isChecked=false
                }else{
                    mealPricePermember[mem.id]=1.0
                    checkbox.isChecked=true
                }
            }
        }
    }
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}