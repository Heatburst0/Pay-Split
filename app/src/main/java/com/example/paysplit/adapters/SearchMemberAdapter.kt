package com.example.paysplit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paysplit.R
import com.example.paysplit.models.User

class SearchMemberAdapter(
    private val context : Context,
    private val list : ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_meal_member,
                parent,
                false
            ))

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val mem = list[position]
        if(mem.image.isNotEmpty()){
            Glide
                .with(context)
                .load(mem.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_member_user_image_meal))
        }

        holder.itemView.findViewById<TextView>(R.id.username_member_meal).text = mem.name
        holder.itemView.findViewById<LinearLayout>(R.id.shared_view).visibility = View.GONE
        holder.itemView.setOnClickListener {
            if(onClickListener!=null){
                onClickListener!!.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun onClick(pos : Int)
    }
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}