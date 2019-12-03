package adventofcode

import java.io.File
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

fun getShortestManhattanDistance(intersectingPoints: List<Point>): Int {
    var shortestDistance = 0
    intersectingPoints.forEach {
        if (shortestDistance == 0 || it.first + it.second < shortestDistance) {
            shortestDistance = it.first + it.second
        }
    }

    return shortestDistance
}

fun getIntersectingPoints(wire1Points: List<Point>, wire2Points: List<Point>): List<Point> {
    val intersectingPointsList: MutableList<Point> = mutableListOf()

    wire1Points.forEachIndexed { index1, point ->
        if (index1 < wire1Points.lastIndex) {

            var verticalWire1 = false
            var horizontalWire1 = false
            if (point.first == wire1Points[index1 + 1].first) {

                verticalWire1 = true
            }
            if (point.second == wire1Points[index1 + 1].second) {

                horizontalWire1 = true
            }

            wire2Points.forEachIndexed { index2, point2 ->
                if (index2 < wire2Points.lastIndex) {
                    if (point2.first == wire2Points[index2 + 1].first) { //vertical wire2
                        if (horizontalWire1){





                            if ((point2.first in point.first..wire1Points[index1 + 1].first) && (point.second in point2.second..wire2Points[index2 + 1].second)){
                                intersectingPointsList.add(Point(point2.first, point.second))
                            }
                        }
                    }
                    if (point2.second == wire2Points[index2 + 1].second) {//horizontal wire2
                        if (verticalWire1){
                            if (point2.second in point.second..wire1Points[index1 + 1].second && point.first in point2.first..wire2Points[index2 + 1].first){
                                intersectingPointsList.add(Point(point.first, point2.second))
                            }
                        }
                    }

                }
            }
        }


    }
    return intersectingPointsList
}

fun createListOfPoints(wireInstructions: String): List<Point> {
    val instructionsList = wireInstructions.split(",").toList()
    val pointList: MutableList<Point> = mutableListOf(Pair(0, 0))

    instructionsList.forEach {
        pointList.add(calculateNewCoordinate(pointList.last(), it))
    }

    return pointList
}

fun calculateNewCoordinate(currentCoordinate: Point, move: String): Point {
    val moveDirection = move.first()
    val moveAmount = move.takeLast(move.count() - 1).toInt()

    return when (moveDirection) {
        Direction.RIGHT.direction -> Pair(currentCoordinate.first + moveAmount, currentCoordinate.second)
        Direction.LEFT.direction -> Pair(currentCoordinate.first - moveAmount, currentCoordinate.second)
        Direction.UP.direction -> Pair(currentCoordinate.first, currentCoordinate.second + moveAmount)
        Direction.DOWN.direction -> Pair(currentCoordinate.first, currentCoordinate.second - moveAmount)
        else -> Pair(0, 0)
    }
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