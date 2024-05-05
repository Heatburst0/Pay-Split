package com.example.paysplit.models

data class PaySplitMember(
    val id : String ="",
    val username : String="",
    val email : String="",
    var amount : Double=0.0,
    val img : String="",
    val pricePerMeal : HashMap<String,Double> = HashMap()
)
