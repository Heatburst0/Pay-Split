package com.example.paysplit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paysplit.R
import com.example.paysplit.models.Meal

open class MealsAdapter(
    private val context : Context,
    private val list : ArrayList<Meal>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_meal,
                parent,
                false
            ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val meal = list[position]

        holder.itemView.findViewById<TextView>(R.id.meal_title).text = meal.name
        holder.itemView.findViewById<TextView>(R.id.meal_quantity).text ="Quantity- ${meal.quantity}"
        holder.itemView.findViewById<TextView>(R.id.meal_price).text = "Rs. ${meal.price}"
        holder.itemView.setOnClickListener {
            if(onClickListener!=null){
                onClickListener!!.onEdit(position)
            }
        }

    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    interface OnClickListener {
        fun onEdit(pos : Int)
    }
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}