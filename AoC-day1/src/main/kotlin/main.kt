package adventofcode

import java.io.File
import kotlin.math.floor

//https://adventofcode.com/2019/day/2

fun main() {
    println(calculateTotalFuel(getData("src/main/resources/inputdata.txt")))
    println(calculateIntCodeFinalState(getData("src/main/resources/day2input.csv")[0]))
}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

fun calculateIntCodeFinalState(intCode: String): String {
    val codeList = intCode.split(",").toMutableList()

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
        println(codeList.joinToString (","))
    }

    val retVal = codeList.joinToString (",")
    return retVal
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