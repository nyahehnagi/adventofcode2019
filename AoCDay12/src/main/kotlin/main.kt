package day12

import java.io.File
import kotlin.math.abs
import kotlin.math.absoluteValue

fun main() {

    val rawMoonData = getData("src/main/resources/day12input")
    //val rawMoonData = getData("src/main/resources/day12testdata1.txt")
    val systemX: System = System(rawMoonData)
    val systemY: System = System(rawMoonData)
    val systemZ: System = System(rawMoonData)

    //system.moveSystemDefinedTimeSteps(1)
    //println(system.calculateTotalEnergyForSystem())

    val x = systemX.calculateCommonXPeriodOfAllMoons()
    val y = systemY.calculateCommonYPeriodOfAllMoons()
    val z = systemZ.calculateCommonZPeriodOfAllMoons()

    println(x)
    println(y)
    println(z)


    println (leastCommonMultiple (x, leastCommonMultiple(y,z)))

}

fun leastCommonMultiple (x : Long, y : Long) : Long{
    return (x * y)/ greatestCommonDivisor(x,y)
}
fun greatestCommonDivisor(x : Long, y : Long): Long {
    return if (y != 0L)
        greatestCommonDivisor(y, x.rem(y))
    else x
}

typealias VelocityChangeXYZ = Triple<Int, Int, Int>

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

class System(rawMoonData: List<String>) {
    val moons: MutableList<Moon> = mutableListOf()
    val commonMoonPeriods: MutableList<Int> = mutableListOf()

    init {
        rawMoonData.forEach { it ->
            val map = it.replace("<", "")
                .replace(">", "").replace(" ", "").split(",")
                .map { it.replace("x", "").replace("y", "").replace("z", "").replace("=", "") }
            val pos = Position(map[0].toInt(), map[1].toInt(), map[2].toInt())
            val vel = Velocity(0, 0, 0)
            moons.add(Moon(pos, vel))
        }
    }

    fun calculateCommonXPeriodOfAllMoons() : Long {
        moveOneTimeStep()
        var xPeriod = 1L
        var ret = moons.map { it.doesInitialPosAnVelMatch(Axis.XAXIS) }.all { it == 1 }
        while (!ret) {
            moveOneTimeStep()
            xPeriod ++
            ret = moons.map { it.doesInitialPosAnVelMatch(Axis.XAXIS) }.all { it == 1 }
        }
        return xPeriod
    }

    fun calculateCommonYPeriodOfAllMoons() : Long{

        moveOneTimeStep()
        var yPeriod = 1L
        var ret = moons.map { it.doesInitialPosAnVelMatch(Axis.YAXIS) }.all { it == 1 }
        while (!ret) {
            moveOneTimeStep()
            yPeriod ++
            ret = moons.map { it.doesInitialPosAnVelMatch(Axis.YAXIS) }.all { it == 1 }
        }
        return yPeriod
    }

    fun calculateCommonZPeriodOfAllMoons() : Long{

        moveOneTimeStep()
        var zPeriod = 1L
        var ret = moons.map { it.doesInitialPosAnVelMatch(Axis.ZAXIS) }.all { it == 1 }
        while (!ret) {
            moveOneTimeStep()
            zPeriod ++
            ret = moons.map { it.doesInitialPosAnVelMatch(Axis.ZAXIS) }.all { it == 1 }
        }
        return zPeriod
    }

    fun moveSystemDefinedTimeSteps(numberOfTimeSteps: Int) {
        for (i in 1..numberOfTimeSteps) {
            moveOneTimeStep()
        }
    }

    fun moveOneTimeStep() {
        val velocityChanges = mutableListOf<VelocityChangeXYZ>()
        moons.forEach {
            velocityChanges.add(it.calculateVelocityChange(moons))
        }
        moons.forEachIndexed { index, moon ->
            moon.applyVelocityChanges(velocityChanges[index])
        }
    }

    fun calculateTotalEnergyForSystem(): Int {
        return moons.map { it.calculateTotalEnergyForMoon() }.sum()
    }
}


class Moon(
    val position: Position,
    val velocity: Velocity
) {
    val initialPosition: Position
    val initialVelocity: Velocity

    init {
        initialPosition = Position(position.xAxis, position.yAxis, position.zAxis)
        initialVelocity = Velocity(velocity.xVelocity, velocity.yVelocity, velocity.zVelocity)
    }

    fun doesInitialPosAnVelMatch(axis: Axis): Int {
        return when (axis) {
            Axis.XAXIS -> if(position.xAxis == initialPosition.xAxis && velocity.xVelocity == initialVelocity.xVelocity) 1 else 0
            Axis.YAXIS -> if (position.yAxis == initialPosition.yAxis && velocity.yVelocity == initialVelocity.yVelocity) 1 else 0
            Axis.ZAXIS -> if (position.zAxis == initialPosition.zAxis && velocity.zVelocity == initialVelocity.zVelocity) 1 else 0
        }
    }

    fun calculateTotalEnergyForMoon(): Int {
        val potentialEnergy = position.xAxis.absoluteValue + position.yAxis.absoluteValue + position.zAxis.absoluteValue
        val kineticEnergy =
            velocity.xVelocity.absoluteValue + velocity.yVelocity.absoluteValue + velocity.zVelocity.absoluteValue

        return potentialEnergy * kineticEnergy
    }

    fun calculateVelocityChange(moons: List<Moon>): VelocityChangeXYZ {
        var xVelChange = 0
        var yVelChange = 0
        var zVelChange = 0

        moons.forEach {
            if (position.xAxis < it.position.xAxis) xVelChange++
            if (position.xAxis > it.position.xAxis) xVelChange--
            if (position.yAxis < it.position.yAxis) yVelChange++
            if (position.yAxis > it.position.yAxis) yVelChange--
            if (position.zAxis < it.position.zAxis) zVelChange++
            if (position.zAxis > it.position.zAxis) zVelChange--
        }

        return VelocityChangeXYZ(xVelChange, yVelChange, zVelChange)
    }

    fun applyVelocityChanges(velocityChangeXYZ: VelocityChangeXYZ) {
        applyToVelocity(velocityChangeXYZ)
        applyVelocityToPosition()
    }

    fun applyToVelocity(velocityChangeXYZ: VelocityChangeXYZ) {
        velocity.xVelocity += velocityChangeXYZ.first
        velocity.yVelocity += velocityChangeXYZ.second
        velocity.zVelocity += velocityChangeXYZ.third
    }

    fun applyVelocityToPosition() {
        position.xAxis += velocity.xVelocity
        position.yAxis += velocity.yVelocity
        position.zAxis += velocity.zVelocity
    }
}

enum class Axis {
    XAXIS,
    YAXIS,
    ZAXIS
}

data class Position(
    var xAxis: Int,
    var yAxis: Int,
    var zAxis: Int
)

data class Velocity(
    var xVelocity: Int,
    var yVelocity: Int,
    var zVelocity: Int
)