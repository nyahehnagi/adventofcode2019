package aocday11

import java.io.File
import java.lang.Exception
import java.lang.*
import java.lang.Integer.max

fun main() {
    //val intCodeDay9Test1 = "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99"
    //val intCodeDay9Test1 = "1102,34915192,34915192,7,4,7,99,0"
    //val intCodeDay9Test1 = "104,1125899906842624,99"
    day11(getData("src/main/resources/day11inputdata.csv")[0])


}

fun day11(intCodeString: String) {
    val paintingRobot = PaintingRobot()

    paintingRobot.paintSquares(intCodeString)
    println (paintingRobot.hullGrid.filter { it.value.timesPainted >0 }.count())

    val Coords = paintingRobot.hullGrid.keys.toList()
    var xMax : Int = 0
    var yMax : Int = 0
    Coords.forEach {
        if (xMax < it.xAxis) xMax = it.xAxis
        if (yMax < it.yAxis) yMax = it.yAxis
    }
    var registration : String = ""
    for (x in 0..xMax){
        for (y in 0..yMax){
            if (paintingRobot.hullGrid.containsKey(Coordinate(x,y)))
                registration = registration + paintingRobot.hullGrid[Coordinate(x,y)]!!.colour
            else
                registration = registration + "0"
        }
        registration += "\n"
    }

    println(registration.replace("0"," ").replace("1", "X"))
}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

data class Coordinate(
    val xAxis: Int,
    val yAxis: Int
)

data class RobotPosition(
    val coordinate: Coordinate,
    val directionFacing: Direction
)

data class Paint(
    var colour: Long,
    var timesPainted: Int
)

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

class PaintingRobot {

    var hullGrid: MutableMap<Coordinate, Paint> = mutableMapOf()
    val computer: Computer = Computer()
    var currentPosition = RobotPosition(Coordinate(0, 0), Direction.UP)

    fun paintSquares(intCodeString: String) {
        try {

            hullGrid.putIfAbsent(currentPosition.coordinate, Paint(1,0))
            computer.clearInputOutput()
            computer.input.add(0)
            computer.runProgram(0, intCodeString)

            while (computer.returnCode != ReturnCode.END && computer.returnCode != ReturnCode.UNKNOWN) {
                // need to convert longs

                if (computer.output.size > 1) {
                    val paintColour = computer.output[computer.output.lastIndex - 1]
                    val move = computer.output.last()

                    paintCurrentSquare(currentPosition, paintColour)
                    currentPosition = moveRobot(currentPosition, move)
                }
                computer.clearInputOutput()
                computer.input.add(hullGrid[currentPosition.coordinate]!!.colour)
                computer.resumeProgram()
            }
        } catch (e: Exception) {
            println("here")
        }

    }

    fun paintCurrentSquare(position: RobotPosition, colour: Long) {
        try {
            hullGrid[position.coordinate]!!.timesPainted = hullGrid[position.coordinate]!!.timesPainted + 1
            hullGrid[position.coordinate]!!.colour = colour
        } catch (e: Exception) {
            println("here")
        }
    }

    fun moveRobot(currentRobotPosition: RobotPosition, move: Long): RobotPosition {
        // turn left 90
        val newRobotPosition: RobotPosition
        if (move == 0L) {
            when (currentRobotPosition.directionFacing) {
                Direction.UP -> newRobotPosition = RobotPosition(
                    Coordinate(
                        currentRobotPosition.coordinate.xAxis - 1,
                        currentRobotPosition.coordinate.yAxis
                    ), Direction.LEFT
                )
                Direction.LEFT -> newRobotPosition = RobotPosition(
                    Coordinate(
                        currentRobotPosition.coordinate.xAxis,
                        currentRobotPosition.coordinate.yAxis - 1
                    ), Direction.DOWN
                )
                Direction.RIGHT -> newRobotPosition = RobotPosition(
                    Coordinate(
                        currentRobotPosition.coordinate.xAxis,
                        currentRobotPosition.coordinate.yAxis + 1
                    ), Direction.UP
                )
                Direction.DOWN -> newRobotPosition = RobotPosition(
                    Coordinate(
                        currentRobotPosition.coordinate.xAxis + 1,
                        currentRobotPosition.coordinate.yAxis
                    ), Direction.RIGHT
                )
            }
        }

        //turn right 90
        else if (move == 1L) {
            when (currentRobotPosition.directionFacing) {
                Direction.UP -> newRobotPosition = RobotPosition(
                    Coordinate(
                        currentRobotPosition.coordinate.xAxis + 1,
                        currentRobotPosition.coordinate.yAxis
                    ), Direction.RIGHT
                )
                Direction.LEFT -> newRobotPosition = RobotPosition(
                    Coordinate(
                        currentRobotPosition.coordinate.xAxis,
                        currentRobotPosition.coordinate.yAxis + 1
                    ), Direction.UP
                )
                Direction.RIGHT -> newRobotPosition = RobotPosition(
                    Coordinate(
                        currentRobotPosition.coordinate.xAxis,
                        currentRobotPosition.coordinate.yAxis - 1
                    ), Direction.DOWN
                )
                Direction.DOWN -> newRobotPosition = RobotPosition(
                    Coordinate(
                        currentRobotPosition.coordinate.xAxis - 1,
                        currentRobotPosition.coordinate.yAxis
                    ), Direction.LEFT
                )
            }
        } else newRobotPosition = currentRobotPosition

        hullGrid.putIfAbsent(newRobotPosition.coordinate, Paint(0, 0))
        return newRobotPosition
    }

}

class Computer() {

    val input: MutableList<Long> = mutableListOf()
    val output: MutableList<Long> = mutableListOf()
    var returnCode = ReturnCode.UNKNOWN
    var instruction: InstructionProcessor

    init {
        instruction = InstructionProcessor(0, mutableListOf())
    }

    fun clearInputOutput() {
        input.clear()
        output.clear()
    }

    fun runProgram(inputCode: Long, intCodeString: String) {
        instruction = InstructionProcessor(0L, intCodeString.split(",").toMutableList())
        input.add(inputCode)
        returnCode = instruction.processIntCode(input, output)
    }

    fun resumeProgram() {
        returnCode = instruction.processIntCode(input, output)
    }
}

class InstructionProcessor(index: Long, intCodeList: MutableList<String>) {

    var haltProgam: Boolean = false
    var waiting: Boolean = false

    private val instructionMemory: MutableMap<Long, String> = mutableMapOf()
    private var instructionPointer: Long = 0L
    private var relativeBase: Long = 0

    init {
        instructionPointer = index
        intCodeList.toMutableList().forEachIndexed() { i, singleInstruction ->
            instructionMemory[i.toLong()] =
                singleInstruction
        }
    }

    fun processIntCode(
        inputList: MutableList<Long>,
        outputList: MutableList<Long>
    ): ReturnCode {

        do {
            //TODO Join these 2 up so we have one OpCode class which works everything out
            val currentOpCode = getOpCode(instructionPointer)
            val parameterModes = getParameterModes(instructionPointer)

            processOpCode(inputList, outputList, currentOpCode, parameterModes)
        } while ((!haltProgam) && (!waiting))

        if (waiting) {
            return ReturnCode.WAITING
        }
        if (haltProgam) {
            return ReturnCode.END
        }
        return ReturnCode.UNKNOWN
    }

    private fun processOpCode(
        inputList: MutableList<Long>,
        outputList: MutableList<Long>,
        opCode: OpCode,
        parameterModes: InstructionParameters
    ) {

        val firstValue: Long
        val secondValue: Long
        val thirdValue: Long
        val outputValue: Long

        when (opCode) {
            OpCode.OPCODE1 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                thirdValue = getThirdInstructionValue(parameterModes)
                assignValueToMemory(thirdValue, (firstValue + secondValue).toString())
                instructionPointer += OpCode.OPCODE1.increment

            }
            OpCode.OPCODE2 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                thirdValue = getThirdInstructionValue(parameterModes)
                assignValueToMemory(thirdValue, (firstValue * secondValue).toString())
                instructionPointer += OpCode.OPCODE2.increment
            }
            OpCode.OPCODE3 -> {

                var indexToAssignOpsCode3 = instructionMemory[instructionPointer + 1]!!.toLong()

                if (parameterModes.param1 == ParameterType.RELATIVE) {
                    indexToAssignOpsCode3 = indexToAssignOpsCode3 + relativeBase
                }

                if (inputList.size > 0) {
                    assignValueToMemory(indexToAssignOpsCode3, inputList[0].toString())
                    inputList.removeAt(0)
                    instructionPointer += OpCode.OPCODE3.increment
                    waiting = false
                } else {
                    waiting = true
                }

            }
            OpCode.OPCODE4 -> {
                outputValue = getfirstInstructionValue(parameterModes)
                outputList.add(outputValue)
                instructionPointer += OpCode.OPCODE4.increment
            }
            OpCode.OPCODE5 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                if (firstValue != 0L) instructionPointer =
                    secondValue else instructionPointer += OpCode.OPCODE5.increment
            }
            OpCode.OPCODE6 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                if (firstValue == 0L) instructionPointer =
                    secondValue else instructionPointer += OpCode.OPCODE6.increment

            }
            OpCode.OPCODE7 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                thirdValue = getThirdInstructionValue(parameterModes)
                if (firstValue < secondValue) assignValueToMemory(thirdValue, "1") else assignValueToMemory(
                    thirdValue,
                    "0"
                )
                instructionPointer += OpCode.OPCODE7.increment
            }
            OpCode.OPCODE8 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                thirdValue = getThirdInstructionValue(parameterModes)
                if (firstValue == secondValue) assignValueToMemory(thirdValue, "1") else assignValueToMemory(
                    thirdValue,
                    "0"
                )
                instructionPointer += OpCode.OPCODE8.increment
            }
            OpCode.OPCODE9 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                relativeBase += firstValue
                instructionPointer += OpCode.OPCODE9.increment
            }

            OpCode.OPCODE99 -> {
                haltProgam = true
            }
            OpCode.OPERROR -> throw Exception("All gone tits up")
        }
    }

    private fun assignValueToMemory(key: Long, value: String) {
        if (instructionMemory.containsKey(key)) instructionMemory[key] = value
        else instructionMemory[key] = value
    }

    //Checks to see if a key(memory address exists), if not, it creates one with value zero
    private fun doesKeyExistIfNotCreate(key: Long) {
        if (instructionMemory.containsKey(key)) //Do nothing
        else
            instructionMemory[key] = "0"
    }

    private fun getfirstInstructionValue(parameterModes: InstructionParameters): Long {

        val firstParameterPointer = instructionPointer + 1
        doesKeyExistIfNotCreate(firstParameterPointer)

        return when (parameterModes.param1) {
            ParameterType.IMMEDIATE -> instructionMemory[firstParameterPointer]!!.toLong()
            ParameterType.POSITION -> {
                doesKeyExistIfNotCreate(instructionMemory[firstParameterPointer]!!.toLong())
                instructionMemory[instructionMemory[firstParameterPointer]!!.toLong()]!!.toLong()
            }
            ParameterType.RELATIVE -> {
                instructionMemory[instructionMemory[firstParameterPointer]!!.toLong() + relativeBase]!!.toLong()
            }
        }
    }

    private fun getSecondInstructionValue(parameterModes: InstructionParameters): Long {

        val secondParameterPointer = instructionPointer + 2
        doesKeyExistIfNotCreate(secondParameterPointer)

        return when (parameterModes.param2) {
            ParameterType.IMMEDIATE -> instructionMemory[secondParameterPointer]!!.toLong()
            ParameterType.POSITION -> {
                doesKeyExistIfNotCreate(instructionMemory[secondParameterPointer]!!.toLong())
                instructionMemory[instructionMemory[secondParameterPointer]!!.toLong()]!!.toLong()
            }
            ParameterType.RELATIVE -> {
                doesKeyExistIfNotCreate(instructionMemory[secondParameterPointer]!!.toLong())
                instructionMemory[instructionMemory[secondParameterPointer]!!.toLong() + relativeBase]!!.toLong()
            }
        }
    }

    private fun getThirdInstructionValue(parameterModes: InstructionParameters): Long {

        val thirdParameterPointer = instructionPointer + 3

        return when (parameterModes.param3) {
            ParameterType.RELATIVE -> {
                instructionMemory[thirdParameterPointer]!!.toLong() + relativeBase
            }
            else -> instructionMemory[thirdParameterPointer]!!.toLong() //otherwise is Immediate as per spec
        }
    }

    //TODO refactor this
    private fun getParameterModes(index: Long): InstructionParameters {

        val paramCode: String

        if (instructionMemory[index]!!.length > 2) {

            paramCode = instructionMemory[index]!!.take(instructionMemory[index]!!.length - 2)

            var param1 = ParameterType.POSITION
            var param2 = ParameterType.POSITION
            var param3 = ParameterType.POSITION

            when (paramCode.length) {
                1 -> {
                    if (paramCode[0] == '1') param1 = ParameterType.IMMEDIATE
                    if (paramCode[0] == '2') param1 = ParameterType.RELATIVE

                    return InstructionParameters(param1 = param1)
                }
                2 -> {
                    if (paramCode[1] == '1') param1 = ParameterType.IMMEDIATE
                    if (paramCode[1] == '2') param1 = ParameterType.RELATIVE

                    if (paramCode[0] == '1') param2 = ParameterType.IMMEDIATE
                    if (paramCode[0] == '2') param2 = ParameterType.RELATIVE

                    return InstructionParameters(param1, param2)
                }
                3 -> {
                    if (paramCode[2] == '1') param1 = ParameterType.IMMEDIATE
                    if (paramCode[2] == '2') param1 = ParameterType.RELATIVE

                    if (paramCode[1] == '1') param2 = ParameterType.IMMEDIATE
                    if (paramCode[1] == '2') param2 = ParameterType.RELATIVE

                    if (paramCode[0] == '1') param3 = ParameterType.IMMEDIATE
                    if (paramCode[0] == '2') param3 = ParameterType.RELATIVE

                    return InstructionParameters(param1, param2, param3)
                }
                else -> return InstructionParameters()
            }
        }
        return InstructionParameters()
    }

    private fun getOpCode(index: Long): OpCode {

        val opCodeValue = instructionMemory[index]!!.takeLast(2)
        return when (opCodeValue) {
            "01", "1" -> OpCode.OPCODE1
            "02", "2" -> OpCode.OPCODE2
            "03", "3" -> OpCode.OPCODE3
            "04", "4" -> OpCode.OPCODE4
            "05", "5" -> OpCode.OPCODE5
            "06", "6" -> OpCode.OPCODE6
            "07", "7" -> OpCode.OPCODE7
            "08", "8" -> OpCode.OPCODE8
            "09", "9" -> OpCode.OPCODE9
            "99" -> OpCode.OPCODE99
            else -> OpCode.OPERROR

        }
    }

    //nested class
    enum class OpCode(val increment: Int) {
        OPCODE1(4),
        OPCODE2(4),
        OPCODE3(2),
        OPCODE4(2),
        OPCODE5(3),
        OPCODE6(3),
        OPCODE7(4),
        OPCODE8(4),
        OPCODE9(2),
        OPCODE99(0),
        OPERROR(0)
    }

    enum class ParameterType {
        POSITION,
        IMMEDIATE,
        RELATIVE
    }

    data class InstructionParameters(
        val param1: ParameterType = ParameterType.POSITION,
        val param2: ParameterType = ParameterType.POSITION,
        val param3: ParameterType = ParameterType.POSITION
    )
}

enum class ReturnCode {
    UNKNOWN,
    WAITING,
    END
}
