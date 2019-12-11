package adventofcode

import java.io.File
import kotlin.math.abs
import kotlin.math.floor

//https://adventofcode.com/2019/day/2
typealias Point = Pair<Int, Int>

fun main() {
    //println(calculateTotalFuel(getData("src/main/resources/inputdata.txt")))
    //println(findNounVerbToProduceGivenOutput(getData("src/main/resources/day2input.csv")[0],19690720))
    println(
        calculateManhattanDistance(
            getData("src/main/resources/day3input.csv")[0],
            getData("src/main/resources/day3input.csv")[1]
        )
    )

}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

fun calculateManhattanDistance(firstWire: String, secondWire: String): Int {
    val wire1Points = createListOfPoints(firstWire)
    val wire2Points = createListOfPoints(secondWire)

    return getShortestManhattanDistance(getIntersectingPoints(wire1Points, wire2Points))

}

fun getShortestManhattanDistance(intersectingPoints: List<Triple<Point, Int, Int>>): Int {

    var shortestMoves = 0
    intersectingPoints.forEach {
        if ( shortestMoves == 0 || it.second + it.third < shortestMoves) {
            //abs(it.first.first) + abs(it.first.second) < shortestDistance
            shortestMoves = it.second + it.third
        }
    }

    return shortestMoves
}

fun getIntersectingPoints(wire1Points: List<Point>, wire2Points: List<Point>): List<Triple<Point, Int, Int>> {
    val intersectingPointsList: MutableList<Triple<Point, Int, Int>> = mutableListOf()

    wire1Points.forEachIndexed { index, point ->
        if (wire2Points.contains(point)) {
            intersectingPointsList.add(Triple(point, index, wire2Points.indexOf(point)))
        }
    }
    return intersectingPointsList
}

fun createListOfPoints(wireInstructions: String): List<Point> {
    val instructionsList = wireInstructions.split(",").toList()
    val pointList: MutableList<Point> = mutableListOf(Pair(0, 0))

    instructionsList.forEach {
        pointList.addAll(calculateNextCoordinates(pointList.last(), it))
    }

    return pointList
}

fun calculateNextCoordinates(currentCoordinate: Point, move: String): List<Point> {
    val moveDirection = move.first()
    val moveAmount = move.takeLast(move.count() - 1).toInt()

    val coordinatesVisited: MutableList<Point> = mutableListOf()

    when (moveDirection) {
        Direction.RIGHT.direction -> {
            for (i in 1..moveAmount) {
                coordinatesVisited.add(Point(currentCoordinate.first + i, currentCoordinate.second))
            }
        }

        Direction.LEFT.direction -> {
            for (i in 1..moveAmount) {
                coordinatesVisited.add(Point(currentCoordinate.first - i, currentCoordinate.second))
            }
        }
        Direction.UP.direction -> {
            for (i in 1..moveAmount) {
                coordinatesVisited.add(Point(currentCoordinate.first, currentCoordinate.second + i))
            }
        }

        Direction.DOWN.direction -> {
            for (i in 1..moveAmount) {
                coordinatesVisited.add(Point(currentCoordinate.first, currentCoordinate.second - i))
            }
        }
        else -> throw Exception("wtf")
    }

    return coordinatesVisited
}

enum class Direction(val direction: Char) {
    RIGHT('R'),
    LEFT('L'),
    DOWN('D'),
    UP('U')
}

fun findNounVerbToProduceGivenOutput(intCodeString: String, output: Int): Point {
    for (nounCount in 0..99) {
        for (verbCount in 0..99) {
            if (findMatchForOutput(nounCount, verbCount, output, intCodeString))
                return Pair(nounCount, verbCount)
        }
    }
    return Pair(0, 0)
}

fun findMatchForOutput(noun: Int, verb: Int, output: Int, intCode: String): Boolean {
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
    } catch (e: IndexOutOfBoundsException) {
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