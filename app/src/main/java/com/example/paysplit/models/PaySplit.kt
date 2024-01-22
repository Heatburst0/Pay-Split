package com.example.paysplit.models

import android.os.Parcel
import android.os.Parcelable

data class PaySplit(
    val id : String="",
    val createdBy : String="",
    val createdOn : String="",
    val amount : String="",
    val createdByImg : String="",
    val creatorUPIID : String =""
): Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(createdBy)
        writeString(createdOn)
        writeString(amount)
        writeString(createdByImg)
        writeString(creatorUPIID)
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

