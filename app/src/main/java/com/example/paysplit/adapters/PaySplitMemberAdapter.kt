package com.example.paysplit.adapters

import android.app.Dialog
import android.content.Context
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
import com.example.paysplit.CreateActivity
import com.example.paysplit.R
import com.example.paysplit.models.PaySplitMember
import com.google.android.material.textfield.TextInputLayout

open class PaySplitMemberAdapter(
    private val context : Context,
    private val list : ArrayList<PaySplitMember>
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
            holder.itemView.findViewById<TextView>(R.id.amount_to_pay).text = "₹ ${member.amount}"
            holder.itemView.findViewById<ImageView>(R.id.btn_removeMember).setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.removeUser(position,member,list)
                }
            }
            holder.itemView.setOnClickListener {

                openEditAmountDialog(position)
            }
        }

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun removeUser(position: Int, user: PaySplitMember,list: ArrayList<PaySplitMember>)

    }
    private fun openEditAmountDialog(pos: Int){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.search_user_dialog)

        dialog.findViewById<TextInputLayout>(R.id.tl_et).setHint("Amount")
        val amountEt = dialog.findViewById<AppCompatEditText>(R.id.et_email_member)
        amountEt.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL


        dialog.findViewById<AppCompatButton>(R.id.btn_addmember).setText("Done")
        dialog.findViewById<AppCompatButton>(R.id.btn_addmember).setOnClickListener {
            val amount = amountEt.text.toString()
            if(amount.isEmpty()){
                amountEt.setError("Please fill this")
            }else{
                (context as CreateActivity).members.get(pos).amount=amount
                list[pos].amount=amount
                notifyItemChanged(pos)
                dialog.dismiss()
            }
        }
        dialog.show()

    }
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}