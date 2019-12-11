package aocday10

import java.io.File
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sign

data class Coordinate(
    val xAxis: Int,
    val yAxis: Int
)

data class Asteroid(
    val coordinate: Coordinate,
    val angleToSourceAsteroid: Double
)

fun main() {

    //val rawAsteroidData = listOf<String>(".#..#", ".....", "#####", "....#", "...##")

    val rawAsteroidData = getData("src/main/resources/day10inputdata.txt")
    //val rawAsteroidData = getData("src/main/resources/day10inputtestdata1.txt")
    //val rawAsteroidData = getData("src/main/resources/day10inputtestdata2.txt")
    //val rawAsteroidData = getData("src/main/resources/day10inputtestdata3.txt")
    //val rawAsteroidData = getData("src/main/resources/day10inputtestdata4.txt")
    val satelliteMap: AsteroidMap = AsteroidMap(rawAsteroidData)
    //println(satelliteMap.calculateAsteroidWithMostVisible())
    satelliteMap.vaporise200Asteroids()

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

    fun vaporise200Asteroids() {
        val listOfVisibleAsteroids = mutableListOf<Asteroid>()
        var AsteroidsToVaporise: Int = 200
        var laserSource: Coordinate = Coordinate(17, 23)
        var counter = 0
        var laserPosition: Double = -3.141592653589793

        //fill the list with asteroids in view
        asteroidMap.filter { it.value }.forEach {
            if (isVisible(laserSource, it.key)) {
                if (!listOfVisibleAsteroids.contains(Asteroid(it.key, calculateAngle(laserSource, it.key))))
                    listOfVisibleAsteroids.add(Asteroid(it.key, calculateAngle(laserSource, it.key)))
            }
        }
        // order by angle
        listOfVisibleAsteroids.sortBy { it.angleToSourceAsteroid }


        for (i in 1..200) {

            //blow up the next visible asteroid
            var match: Boolean = false
            var positionOfLaserWhenItVaporisedAsteroid: Double = 0.0
            var removeIndex: Int = 0
            listOfVisibleAsteroids.forEachIndexed { index, asteroid ->
                if (asteroid.angleToSourceAsteroid >= laserPosition && !match) {
                    positionOfLaserWhenItVaporisedAsteroid = asteroid.angleToSourceAsteroid
                    asteroidMap.remove(listOfVisibleAsteroids[index].coordinate)
                    removeIndex = index
                    match = true
                }
            }
            if (match) listOfVisibleAsteroids.removeAt(removeIndex)

            // add new visible asteroids
            asteroidMap.filter { it.value }.forEach {
                if (isVisible(laserSource, it.key)) {
                    if (!listOfVisibleAsteroids.contains(Asteroid(it.key, calculateAngle(laserSource, it.key))))
                        listOfVisibleAsteroids.add(Asteroid(it.key, calculateAngle(laserSource, it.key)))
                }
            }
            // order by angle
            listOfVisibleAsteroids.sortBy { it.angleToSourceAsteroid }

            // set laser position
            var resetLaserMatch = false
            listOfVisibleAsteroids.forEachIndexed { index, asteroid ->
                if (asteroid.angleToSourceAsteroid > positionOfLaserWhenItVaporisedAsteroid && !resetLaserMatch) {
                    laserPosition = asteroid.angleToSourceAsteroid
                    resetLaserMatch = true
                }
            }

            // we've gone round in a circle. Reset the laser to -PI
            if (!match) {
                laserPosition = -3.141592653589793
            }


        }
        println(counter)
        println("here)")
    }

    fun calculateAngle(laserSource: Coordinate, targetAsteroid: Coordinate): Double {
        return -Math.atan2(
            (targetAsteroid.xAxis - laserSource.xAxis).toDouble(),
            (targetAsteroid.yAxis - laserSource.yAxis).toDouble()
        )
    }

    fun calculateAsteroidWithMostVisible(): Pair<Coordinate, Int> {
        //check each asteroid
        var maxCount: Int = 0
        var currentAsteroid = Coordinate(0, 0)
        asteroidMap.filter { it.value }.forEach {
            val totalCount = getVisibleCount(it.key)
            if (totalCount > maxCount) {
                maxCount = totalCount
                currentAsteroid = it.key
            }
        }
        return Pair(currentAsteroid, maxCount)
    }

    fun getVisibleCount(coordinate: Coordinate): Int {
        // are there any intermediate points between
        var count = 0
        asteroidMap.filter { it.value && it.key != coordinate }.forEach {
            if (isVisible(coordinate, it.key)) count++
        }
        return count
    }

    fun isVisible(coordinateFrom: Coordinate, coordinateTo: Coordinate): Boolean {
        val dx: Int = (coordinateTo.xAxis - coordinateFrom.xAxis).absoluteValue
        val dy: Int = (coordinateTo.yAxis - coordinateFrom.yAxis).absoluteValue

        asteroidMap.filter { it.value && it.key != coordinateFrom && it.key != coordinateTo }.forEach {
            val dxCompareCoord = it.key.xAxis - coordinateFrom.xAxis
            val dyCompareCoord = it.key.yAxis - coordinateFrom.yAxis
            if (dxCompareCoord.absoluteValue <= dx && dyCompareCoord.absoluteValue <= dy && sameAngle(
                    coordinateFrom,
                    coordinateTo,
                    it.key
                )
            ) {
                return false;
            }
        }
        return true
    }

    fun sameAngle(coordinateFrom: Coordinate, coordinateTo: Coordinate, coordinateToCheck: Coordinate): Boolean {

        if (sign((coordinateTo.xAxis - coordinateFrom.xAxis).toDouble()) != sign((coordinateToCheck.xAxis - coordinateFrom.xAxis).toDouble())) return false
        if (sign((coordinateTo.yAxis - coordinateFrom.yAxis).toDouble()) != sign((coordinateToCheck.yAxis - coordinateFrom.yAxis).toDouble())) return false

        if (coordinateTo.xAxis == coordinateFrom.xAxis && coordinateToCheck.xAxis == coordinateFrom.xAxis) return true
        if (coordinateTo.yAxis == coordinateFrom.yAxis && coordinateToCheck.yAxis == coordinateFrom.yAxis) return true
        return ((coordinateTo.xAxis - coordinateFrom.xAxis) * (coordinateToCheck.yAxis - coordinateFrom.yAxis) == (coordinateToCheck.xAxis - coordinateFrom.xAxis) * (coordinateTo.yAxis - coordinateFrom.yAxis))
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


