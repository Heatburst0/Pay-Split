package com.example.paysplit.models

import android.os.Parcel
import android.os.Parcelable

data class PaySplit(
    val id : String="",
    val createdBy : String="",
    val createdOn : String="",
    val totalamount : String="",
    val createdByImg : String="",
    val creatorUPIID : String ="",
    val assignedTo : ArrayList<String> = ArrayList(),
    val amountMembers : HashMap<String,Int> = HashMap()
): Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.createStringArrayList()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(createdBy)
        writeString(createdOn)
        writeString(totalamount)
        writeString(createdByImg)
        writeString(creatorUPIID)
        writeStringList(assignedTo)
        writeMap(amountMembers)
//        writeBoolean(Selected)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PaySplit> = object : Parcelable.Creator<PaySplit> {
            override fun createFromParcel(source: Parcel): PaySplit = PaySplit(source)
            override fun newArray(size: Int): Array<PaySplit?> = arrayOfNulls(size)
        }
    }
}

