package aocday17

import java.io.File
import java.lang.Exception
import java.lang.*
import java.lang.Integer.max

fun main() {

    day17(getData("src/main/resources/day17input.txt")[0])

}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

fun day17(intCodeString: String) {
    val asciiInterface  = ASCIIInterface()

    asciiInterface.start(intCodeString)
    println(asciiInterface.getPrintableString())
    println(asciiInterface.calculateAlignmentParameters())
}


class ASCIIInterface {

    val asciiComputer: Computer = Computer()
    val scaffoldMap: MutableMap<Coordinate, Char> = mutableMapOf()
    val intersectingScaffold : MutableList<Coordinate> = mutableListOf()
    var maxXaxis : Int = 0
    var maxYAxis : Int = 0

    fun start(intCodeString: String) {

        asciiComputer.clearInputOutput()
        asciiComputer.runProgram(intCodeString)
        processOutput()
    }

    fun calculateAlignmentParameters() : Int{
        findScaffoldIntersections()
        return intersectingScaffold.fold(0,{ total, element -> total + (element.xAxis * element.yAxis)})
    }

    fun findScaffoldIntersections (){

        scaffoldMap.forEach {
            if (isScaffoldOrRobot(it.key)) {
                if (isIntersectingScaffold(it.key)) {
                    intersectingScaffold.add(it.key)
                }
            }
        }
    }

    fun isIntersectingScaffold (coordinate: Coordinate): Boolean{

        var scaffoldAbove = Coordinate(coordinate.xAxis,coordinate.yAxis -1)
        var scaffoldBelow = Coordinate(coordinate.xAxis,coordinate.yAxis +1)
        var scaffoldLeft = Coordinate(coordinate.xAxis -1,coordinate.yAxis)
        var scaffoldRight = Coordinate(coordinate.xAxis + 1,coordinate.yAxis)

        if (scaffoldAbove.yAxis < 0 || scaffoldBelow.yAxis > maxYAxis || scaffoldLeft.xAxis < 0 || scaffoldRight.xAxis > maxXaxis) return false

        return isScaffoldOrRobot(scaffoldAbove) && isScaffoldOrRobot(scaffoldBelow)&& isScaffoldOrRobot(scaffoldLeft)&& isScaffoldOrRobot(scaffoldRight)

    }

    fun isScaffoldOrRobot(coordinate: Coordinate) :Boolean{
        val scaffoldValue = scaffoldMap[coordinate]!!
        val validValues = "v<>^#"
        return validValues.contains(scaffoldValue)
    }


    fun processOutput() {

        var xAxisCounter : Int = 0
        var yAxisCounter : Int = 0

        while (asciiComputer.output.size > 1) {
            if (asciiComputer.output.isEmpty()){
                break
            }

            var currentOutputValue = asciiComputer.output[0]

            when (currentOutputValue){
                35L -> {
                    scaffoldMap.put(Coordinate(xAxisCounter,yAxisCounter),'#')
                    if (xAxisCounter > maxXaxis) maxXaxis = xAxisCounter
                    if (yAxisCounter > maxYAxis) maxYAxis = yAxisCounter
                    xAxisCounter ++
                }
                46L -> {
                    scaffoldMap.put(Coordinate(xAxisCounter,yAxisCounter),'.')
                    if (xAxisCounter > maxXaxis) maxXaxis = xAxisCounter
                    if (yAxisCounter > maxYAxis) maxYAxis = yAxisCounter
                    xAxisCounter ++
                }
                10L -> { //new line
                    xAxisCounter = 0
                    yAxisCounter ++
                }
                94L -> {
                    scaffoldMap.put(Coordinate(xAxisCounter,yAxisCounter),'^')
                    if (xAxisCounter > maxXaxis) maxXaxis = xAxisCounter
                    if (yAxisCounter > maxYAxis) maxYAxis = yAxisCounter
                    xAxisCounter ++
                }
                60L -> {
                    scaffoldMap.put(Coordinate(xAxisCounter,yAxisCounter),'<')
                    if (xAxisCounter > maxXaxis) maxXaxis = xAxisCounter
                    if (yAxisCounter > maxYAxis) maxYAxis = yAxisCounter
                    xAxisCounter ++
                }
                62L -> {
                    scaffoldMap.put(Coordinate(xAxisCounter,yAxisCounter),'>')
                    if (xAxisCounter > maxXaxis) maxXaxis = xAxisCounter
                    if (yAxisCounter > maxYAxis) maxYAxis = yAxisCounter
                    xAxisCounter ++
                }
                118L -> {
                    scaffoldMap.put(Coordinate(xAxisCounter,yAxisCounter),'v')
                    if (xAxisCounter > maxXaxis) maxXaxis = xAxisCounter
                    if (yAxisCounter > maxYAxis) maxYAxis = yAxisCounter
                    xAxisCounter ++
                }

                88L -> {
                    scaffoldMap.put(Coordinate(xAxisCounter,yAxisCounter),'X')
                    if (xAxisCounter > maxXaxis) maxXaxis = xAxisCounter
                    if (yAxisCounter > maxYAxis) maxYAxis = yAxisCounter
                    xAxisCounter ++
                }
                else -> println("error")
            }
            asciiComputer.output.removeAt(0)
        }
    }

    fun getPrintableString () : String {
        var stringToPrint = ""

        for (yAxis in 0..maxYAxis){
            for (xAxis in 0..maxXaxis) {
                stringToPrint += scaffoldMap[Coordinate(xAxis, yAxis)]
            }
            stringToPrint += "\n"
        }

        return stringToPrint
    }
}

data class Coordinate(
    val xAxis : Int,
    val yAxis : Int
)


class Computer {

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

    fun runProgram( intCodeString: String) {
        instruction = InstructionProcessor(0L, intCodeString.split(",").toMutableList())

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
