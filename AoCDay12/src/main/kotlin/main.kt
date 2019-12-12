package day12

import java.io.File
import kotlin.math.abs
import kotlin.math.absoluteValue

fun main() {

    val rawMoonData = getData("src/main/resources/day12input")
    //val rawMoonData = getData("src/main/resources/day12testdata1.txt")
    val system: System = System(rawMoonData)

    system.moveSystemDefinedTimeSteps(1000)
    println(system.calculateTotalEnergyForSystem())
}

typealias VelocityChangeXYZ = Triple<Int, Int, Int>

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

class System(rawMoonData: List<String>) {
    val moons: MutableList<Moon> = mutableListOf()

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

    fun moveSystemDefinedTimeSteps(numberOfTimeSteps : Int) {
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

    fun calculateTotalEnergyForSystem() : Int {
        return moons.map { it.calculateTotalEnergyForMoon() }.sum()
    }
}

data class Moon(
    val position: Position,
    val velocity: Velocity
) {
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