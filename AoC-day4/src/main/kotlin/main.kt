package aocday4

import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {

    println(calculatePossibleCombinations(245318, 765747))
}

fun calculatePossibleCombinations(startNumber: Int, endNumber: Int): Int {
    var validCombinationCount = 0
    for (i in startNumber..endNumber) {
        if (isValidCombination(i))
            validCombinationCount += 1
    }
    return validCombinationCount
}

fun isValidCombination(number: Int): Boolean {
    val arrayofNumbers = number.toString().toCharArray()

    // check incremental numbers
    arrayofNumbers.forEachIndexed { index, c ->
        if (index < arrayofNumbers.lastIndex) {
            if (c.toString().toInt() > arrayofNumbers[index + 1].toString().toInt()) {
                return false
            }
        }
    }
    //check contains two adjacent digits
    arrayofNumbers.forEachIndexed { index, c ->
        if (index < arrayofNumbers.lastIndex) {
            if (c.toString().toInt() == arrayofNumbers[index + 1].toString().toInt()) {
                // is it the last 2 digits and just double digits?
                if (index + 1 == arrayofNumbers.lastIndex && c.toString().toInt() != arrayofNumbers[index - 1].toString().toInt()) {
                    // must be valid
                    return true
                }

                // is it the first 2 digits and just double digits?
                if (index + 2 <= arrayofNumbers.lastIndex) {
                    if (c.toString().toInt() != arrayofNumbers[index + 2].toString().toInt() && index == 0)
                        return true
                }

                // is double digit only?
                if (index + 2 <= arrayofNumbers.lastIndex) {
                    if (c.toString().toInt() != arrayofNumbers[index + 2].toString().toInt() && c.toString().toInt() != arrayofNumbers[index - 1].toString().toInt())
                        return true
                }
            }
        }
    }

    return false

}