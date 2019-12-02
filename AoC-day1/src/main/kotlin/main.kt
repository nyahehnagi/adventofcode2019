package adventofcode

import java.io.File
import kotlin.math.floor

fun main() {
    println(calculateTotalFuel(getData()))
}

fun calculateTotalFuel(data: List<String>): Int {
    var total = 0
    data.forEach() {
        total += calculateFuel(it.toInt())
    }
    return total
}

fun getData(): List<String> {
    val data = File("src/main/resources/inputdata.txt")
    return data.readLines()
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