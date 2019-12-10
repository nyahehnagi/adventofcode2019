package aocday10

import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sign

data class Coordinate(
    val xAxis: Int,
    val yAxis: Int
)

data class Fraction(
    val numerator: Int,
    val denominator: Int
)

fun main() {

    //val rawAsteroidData = listOf<String>(".#..#", ".....", "#####", "....#", "...##")

    val rawAsteroidData = getData("src/main/resources/day10inputdata.txt")
    //val rawAsteroidData = getData("src/main/resources/day10inputtestdata1.txt")
    //val rawAsteroidData = getData("src/main/resources/day10inputtestdata2.txt")
    //val rawAsteroidData = getData("src/main/resources/day10inputtestdata3.txt")
    //val rawAsteroidData = getData("src/main/resources/day10inputtestdata4.txt")
    val satelliteMap: AsteroidMap = AsteroidMap(rawAsteroidData)
    println(satelliteMap.calculateAsteroidWithMostVisible())

}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

class AsteroidMap(rawSatelliteData: List<String>) {

    val mapWidth = rawSatelliteData[0].count()
    val mapHeight = rawSatelliteData.count()
    val asteroidMap: MutableMap<Coordinate, Boolean> = mutableMapOf()


    init {
        generateCoordinates(rawSatelliteData)
        //println(asteroidMap)
    }

    fun generateCoordinates(rawSatelliteData: List<String>) {

        rawSatelliteData.forEachIndexed { yAxis, row ->
            row.forEachIndexed { xAxis, column ->
                asteroidMap[Coordinate(xAxis, yAxis)] =
                    hasAsteroid(column)
            }
        }
    }
    //       val newList = asteroidMap.toList().filter { it.second.second }.distinctBy { it.second.first }

    fun calculateAsteroidWithMostVisible(): Pair<Coordinate, Int> {
        //check each asteroid
        var maxCount: Int = 0
        var currentAsteroid: Coordinate = Coordinate(0, 0)
        asteroidMap.filter { it.value }.forEach {
            val totalCount = getVisibleCount(it.key)
            if (totalCount > maxCount) {
                maxCount = totalCount
                currentAsteroid = it.key
            }
        }
        return Pair(currentAsteroid, maxCount)
    }

    fun getVisibleCount(coordinate: Coordinate) : Int {
        // are there any intermediate points between
        var count = 0
        asteroidMap.filter { it.value && it.key != coordinate}.forEach {
            if (isVisible(coordinate,it.key)) count ++
        }
        return count
    }

    fun isVisible (coordinateFrom: Coordinate, coordinateTo: Coordinate): Boolean{
        val dx : Int = (coordinateTo.xAxis - coordinateFrom.xAxis).absoluteValue
        val dy : Int = (coordinateTo.yAxis - coordinateFrom.yAxis).absoluteValue

        asteroidMap.filter { it.value && it.key != coordinateFrom && it.key != coordinateTo }.forEach{
                val dxCompareCoord = it.key.xAxis - coordinateFrom.xAxis
                val dyCompareCoord = it.key.yAxis - coordinateFrom.yAxis
                if (dxCompareCoord.absoluteValue <= dx && dyCompareCoord.absoluteValue <= dy && sameAngle(coordinateFrom, coordinateTo, it.key)) {
                    return false;
                }
            }
        return true
    }

fun sameAngle(coordinateFrom: Coordinate, coordinateTo: Coordinate, coordinateToCheck : Coordinate) : Boolean {

    if (sign((coordinateTo.xAxis-coordinateFrom.xAxis).toDouble()) != sign((coordinateToCheck.xAxis-coordinateFrom.xAxis).toDouble())) return false
    if (sign((coordinateTo.yAxis-coordinateFrom.yAxis).toDouble()) != sign((coordinateToCheck.yAxis-coordinateFrom.yAxis).toDouble())) return false

    if (coordinateTo.xAxis == coordinateFrom.xAxis && coordinateToCheck.xAxis == coordinateFrom.xAxis) return true
    if (coordinateTo.yAxis == coordinateFrom.yAxis && coordinateToCheck.yAxis == coordinateFrom.yAxis) return true
    return ((coordinateTo.xAxis-coordinateFrom.xAxis)*(coordinateToCheck.yAxis-coordinateFrom.yAxis) == (coordinateToCheck.xAxis-coordinateFrom.xAxis)*(coordinateTo.yAxis-coordinateFrom.yAxis))
}


    fun calculateSimplifiedFraction(coordinate: Coordinate): Fraction {
        return if (coordinate.xAxis == 0 || coordinate.yAxis == 0)
            Fraction(coordinate.xAxis, coordinate.yAxis)
        else {
            val gcd = greatestCommonDivisor(coordinate)
            Fraction(coordinate.xAxis / gcd, coordinate.yAxis / gcd)
        }
    }

    fun hasAsteroid(char: Char): Boolean {
        return char == '#'
    }

    fun greatestCommonDivisor(coordinate: Coordinate): Int {
        return if (coordinate.yAxis != 0)
            greatestCommonDivisor(Coordinate(coordinate.yAxis, coordinate.xAxis.rem(coordinate.yAxis)))
        else coordinate.xAxis
    }

}


