package adventofcode

import java.io.File
import kotlin.math.floor


fun main(){
    println (calculateTotalFuel (getData()))
}

fun calculateTotalFuel(data : List<String>): Int{
    var total = 0
    data.forEach(){
        total += calculateFuel(it.toInt())
    }
    return total
}

fun getData () : List<String>{
    //TODO Learn how to do this with JSON rather than CSV
    val data = File("src/main/resources/inputdata.txt")
    return data.readLines()
}
//fuel = mass/3 - round down and subtract 2

fun calculateFuel (mass : Int ) : Int =  floor(mass/3.toDouble()).toInt() - 2
