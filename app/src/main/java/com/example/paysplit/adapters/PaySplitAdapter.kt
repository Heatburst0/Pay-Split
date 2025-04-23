package com.example.paysplit.adapters

import android.animation.ValueAnimator
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paysplit.R
import com.example.paysplit.fragments.HomeFragment
import com.example.paysplit.models.PaySplit
import com.example.paysplit.models.User


open class PaySplitAdapter(
    private val context: Context,
    private val frag: HomeFragment,
    private val list: ArrayList<PaySplit>,
    private val loggedInUser: User,
) :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_paysplit,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private var onClickListener: OnClickListener? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ps = list[position]
        if(holder is MyViewHolder){
            val time= DateUtils.getRelativeTimeSpanString(ps.createdOn.toLong()).toString()
            holder.itemView.findViewById<TextView>(R.id.paysplit_when).text = time
            holder.itemView.findViewById<TextView>(R.id.item_paysplit_title).text = ps.title
            val user = ps.createdBy
            if(user.image.isNotEmpty()){
                Glide
                    .with(context)
                    .load(user.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.findViewById(R.id.payplit_img))
            }
            holder.itemView.findViewById<TextView>(R.id.createdby).text = "Created by: ${user.name}"
            holder.itemView.findViewById<TextView>(R.id.paysplit_amount).text = "Rs. ${ps.amountMembers[loggedInUser.id]}"
            val pH = holder.itemView.height
            holder.itemView.setOnClickListener {
                val originalHeight = holder.itemView.height
                val paybtn = holder.itemView.findViewById<AppCompatButton>(R.id.btn_pay)
                val viewPaybtn = holder.itemView.findViewById<AppCompatButton>(R.id.btn_viewPaySplit)
                val valueAnimator : ValueAnimator
                if(paybtn.isVisible){
//                    paybtn.visibility = View.GONE
                     valueAnimator = ValueAnimator.ofInt(
                        originalHeight,
                        originalHeight-(originalHeight*0.58333).toInt()
                    )
                    val a: Animation = AlphaAnimation(1.00f, 0.00f) // Fade out
                    a.setDuration(100)
                    // Set a listener to the animation and configure onAnimationEnd
                    a.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            paybtn.visibility = View.GONE
                            paybtn.isEnabled = false
                            viewPaybtn.visibility = View.GONE
                            viewPaybtn.isEnabled = false
                        }

                        override fun onAnimationRepeat(animation: Animation?) {}
                    })

                    paybtn.startAnimation(a)
                }else{

                    viewPaybtn.visibility = View.VISIBLE
                    viewPaybtn.isEnabled = true
                    paybtn.visibility = View.VISIBLE
                    paybtn.isEnabled = true
//                        mIsViewExpanded = true
                        valueAnimator = ValueAnimator.ofInt(
                            originalHeight,
                            originalHeight + (originalHeight*1.4).toInt()
                        )

                }
                valueAnimator.setDuration(100)
                valueAnimator.interpolator = AccelerateDecelerateInterpolator()
                valueAnimator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    holder.itemView.getLayoutParams().height = value
                    holder.itemView.requestLayout()
                }


                valueAnimator.start()

            }
            holder.itemView.findViewById<AppCompatButton>(R.id.btn_pay).setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onPayButton(ps.createdBy,ps.amountMembers)
                }
            }
            holder.itemView.findViewById<AppCompatButton>(R.id.btn_viewPaySplit).setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onViewPaySplitButton(ps.createdBy,ps.amountMembers)
                }
            }

        }

    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun onPayButton(createdby : User,amount : HashMap<String,Double>)
        fun onViewPaySplitButton(createdby : User,amount : HashMap<String,Double>)
    }
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}