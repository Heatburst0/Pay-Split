package com.example.paysplit.models

data class Meal(
    var name : String="",
    var quantity : Int=1,
    var price : Double=0.0,
    var shared : Boolean =false,
    var mealPricePerMember: HashMap<String,Double>
)
