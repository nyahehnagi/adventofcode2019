package aocday12

import java.io.File
import java.lang.Exception
import java.lang.*
import java.lang.Integer.max

fun main() {
    day12(getData("src/main/resources/day12input.csv")[0])
}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

fun day12(intCodeString: String) {
    val game = Game()

    game.startGame(intCodeString, 2)
    println(game.getTileCount(TileType.BLOCK))
    playGame(game)
}

fun playGame(game: Game) {

    while (game.gameComputer.returnCode != ReturnCode.END) {
        val xBall = game.getBallPosition().xAxis
        val xPaddle = game.getPaddlePosition().xAxis
        val paddleDirection: JoyStick
        if (xPaddle < xBall) {
            paddleDirection = JoyStick.RIGHT
        } else if (xPaddle > xBall) {
            paddleDirection = JoyStick.LEFT
        } else
            paddleDirection = JoyStick.NEUT

        game.moveJoystick(paddleDirection.value)

    }

    println(game.screen.getScreen())
    println(game.score)
}

class Game{

    val screen: GameScreen = GameScreen()
    val gameComputer: Computer = Computer()
    var score: Long = 0L

    fun moveJoystick(input: Int) {

        gameComputer.input.add(input.toLong())
        gameComputer.resumeProgram()
        processOutput()
    }

    fun startGame(intCodeString: String, inputCode: Long) {

        try {
            gameComputer.clearInputOutput()
            gameComputer.runProgram(null, intCodeString)
            processOutput()

        } catch (e: Exception) {
            println("here")
        }
    }

    fun processOutput() {
        while (gameComputer.output.size > 1) {
            val coord = Coordinate(gameComputer.output[0], gameComputer.output[1])
            val thirdOutputValue = gameComputer.output[2]

            for (i in 1..3) {
                gameComputer.output.removeAt(0)
            }

            if (coord.xAxis == -1L && coord.yAxis == 0L) {
                //it's a score
                score = thirdOutputValue
            } else {
                val tileType = getTileType(thirdOutputValue)
                if (screen.gameTiles.containsKey(coord)) {
                    screen.gameTiles[coord] = tileType
                }
                screen.gameTiles.putIfAbsent(coord, tileType)
            }
        }
    }

    fun getPaddlePosition(): Coordinate {
        return screen.gameTiles.filter { it.value == TileType.PADDLE }.toList()[0].first
    }

    fun getBallPosition(): Coordinate {
        return screen.gameTiles.filter { it.value == TileType.BALL }.toList()[0].first
    }

    fun getTileCount(tiletype: TileType): Int {
        return screen.gameTiles.filter { it.value == tiletype }.count()

    }

    fun getTileType(tileID: Long): TileType {

        return when (tileID) {
            0L -> TileType.EMPTY
            1L -> TileType.WALL
            2L -> TileType.BLOCK
            3L -> TileType.PADDLE
            4L -> TileType.BALL
            else -> TileType.UNKNOWN
        }
    }

}

class GameScreen {
    val gameTiles: MutableMap<Coordinate, TileType> = mutableMapOf()

    fun getScreen(): String {
        val coords = gameTiles.keys.toList()
        var xMax = 0L
        var yMax = 0L
        coords.forEach {
            if (xMax < it.xAxis) xMax = it.xAxis
            if (yMax < it.yAxis) yMax = it.yAxis
        }

        var screenString: String = ""
        for (y in 0..yMax) {
            for (x in 0..xMax) {
                if (gameTiles.containsKey(Coordinate(x, y)))
                    screenString += tileToString(gameTiles[Coordinate(x, y)]!!)

            }
            screenString += "\n"
        }

        return screenString
    }

    fun tileToString(tiletype: TileType): String {
        return when (tiletype) {
            TileType.BALL -> "O"
            TileType.WALL -> "#"
            TileType.BLOCK -> "X"
            TileType.PADDLE -> "3"
            TileType.EMPTY -> " "
            TileType.UNKNOWN -> " "
        }
    }
}

data class Coordinate(
    val xAxis: Long,
    val yAxis: Long
)

enum class TileType {
    EMPTY,
    WALL,
    BLOCK,
    PADDLE,
    BALL,
    UNKNOWN
}

enum class JoyStick(val value: Int) {
    NEUT(0),
    LEFT(-1),
    RIGHT(1)
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

    fun runProgram(inputCode: Long?, intCodeString: String) {
        instruction = InstructionProcessor(0L, intCodeString.split(",").toMutableList())

        if (inputCode != null) input.add(inputCode)
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
