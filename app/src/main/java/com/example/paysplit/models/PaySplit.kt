package com.example.paysplit.models

import android.os.Parcel
import android.os.Parcelable

data class PaySplit(
    var id : String="",
    val title : String="",
    val createdBy : User=User(),
    val createdOn : String="",
    val totalamount : Double=0.0,
    val assignedTo : ArrayList<String> = ArrayList(),
    val amountMembers : HashMap<String,Double> = HashMap()
)


