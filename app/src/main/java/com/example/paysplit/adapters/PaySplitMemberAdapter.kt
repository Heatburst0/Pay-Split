package com.example.paysplit.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paysplit.CreateActivity
import com.example.paysplit.R
import com.example.paysplit.models.PaySplitMember
import com.google.android.material.textfield.TextInputLayout

open class PaySplitMemberAdapter(
    private val context : Context,
    private val list : ArrayList<PaySplitMember>,
    private val totalAmount : Double
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val member = list[position]
        if(holder is MyViewHolder){
            holder.itemView.findViewById<TextView>(R.id.username_member).text = member.username
            holder.itemView.findViewById<TextView>(R.id.email_member).text = member.email
            var toPay = "%.2f".format(member.amount).toDouble()
            holder.itemView.findViewById<TextView>(R.id.amount_to_pay).text = "₹ ${toPay}"
            holder.itemView.findViewById<ImageView>(R.id.btn_removeMember).setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.removeUser(position,member,list)
                }
            }
            holder.itemView.setOnClickListener {

                if(onClickListener!=null){
                    onClickListener!!.editamount(position,list,toPay)
                }
            }
            if(member.img.isNotEmpty()){
                Glide
                    .with(context)
                    .load(member.img)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.findViewById(R.id.iv_member_user_image))
            }
        }

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun removeUser(position: Int, user: PaySplitMember,list: ArrayList<PaySplitMember>)
        fun editamount(pos : Int,lis : ArrayList<PaySplitMember>,prevAmount : Double)

    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}