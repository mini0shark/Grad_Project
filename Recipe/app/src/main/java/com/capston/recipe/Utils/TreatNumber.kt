package com.capston.recipe.Utils

object TreatNumber {
    fun convertBigNumber(number:Int):String{
        when(number){
            in 0 until 1000 ->{
                return number.toString()
            }
            in 1000 until 1000000 ->{
                return "${number/1000}K"
            }
            else ->{
                return return "${number/1000000}M"
            }
        }
    }
}