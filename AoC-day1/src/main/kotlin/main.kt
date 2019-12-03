package adventofcode

import java.io.File
import kotlin.math.floor

//https://adventofcode.com/2019/day/2

fun main() {
    //println(calculateTotalFuel(getData("src/main/resources/inputdata.txt")))
    //println(findNounVerbToProduceGivenOutput(getData("src/main/resources/day2input.csv")[0],19690720))

}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

fun findNounVerbToProduceGivenOutput (intCodeString : String, output : Int) : Pair<Int,Int>{
    for (nounCount in 0..99){
        for (verbCount in 0..99){
            if (findMatchForOutput (nounCount, verbCount,output, intCodeString))
                return Pair(nounCount,verbCount)
        }
    }
    return Pair(0,0)
}

fun findMatchForOutput(noun : Int, verb : Int, output : Int, intCode: String): Boolean {
    val codeList = intCode.split(",").toMutableList()
    //Initialise noun and verb
    codeList[1] = noun.toString()
    codeList[2] = verb.toString()

    try {
        for (i in 0..codeList.lastIndex step 4) {
            if (codeList[i] != "99") {
                var firstValue = codeList[codeList[i + 1].toInt()].toInt()
                var secondValue = codeList[codeList[i + 2].toInt()].toInt()
                var thirdValue = codeList[i + 3].toInt()
                when {
                    codeList[i].toInt() == 1 ->
                        codeList[thirdValue] = (firstValue + secondValue).toString()
                    codeList[i].toInt() == 2 ->
                        codeList[thirdValue] = (firstValue * secondValue).toString()
                    else -> println("Errr")
                }
            }
        }
    }
    catch(e : IndexOutOfBoundsException){
        return codeList[0].toInt() == output
    }
    return codeList[0].toInt() == output
}

fun calculateTotalFuel(data: List<String>): Int {
    var total = 0
    data.forEach() {
        total += calculateFuel(it.toInt())
    }
    return total
}

fun calculateFuel(mass: Int): Int {
    var totalFuelRequired = floor(mass / 3.toDouble()).toInt() - 2
    var additionalFuelNeeded = totalFuelRequired

    while ((floor(additionalFuelNeeded / 3.toDouble()).toInt() - 2) > 0) {
        additionalFuelNeeded = (floor(additionalFuelNeeded / 3.toDouble()).toInt() - 2)
        totalFuelRequired += additionalFuelNeeded
    }

    return totalFuelRequired
}