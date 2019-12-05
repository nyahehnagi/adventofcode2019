package aocday5

import java.io.File
import java.lang.Exception

fun main() {
    println(testIntCode(getData("src/main/resources/day5input.csv")[0]))

}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}


fun testIntCode(intCodeString: String) {
     readIntCode(5, intCodeString)
}

enum class OpCode {
    OPCODE1,
    OPCODE2,
    OPCODE3,
    OPCODE4,
    OPCODE5,
    OPCODE6,
    OPCODE7,
    OPCODE8,
    OPCODE99,
    OPERROR
}

data class InstructionParameters(
    val param1: Boolean = false,
    val param2: Boolean = false,
    val param3: Boolean = false
)

fun readIntCode(opCode3InputValue: Int, intCode: String) {
    var intCodeList = intCode.split(",").toMutableList()

    var currentInstructionPointer = 0 //track where we are in the code

    while (currentInstructionPointer <= intCodeList.lastIndex) {

        val currentOpCode = getOpCode(currentInstructionPointer, intCodeList)
        val parameterModes = getParameterModes(currentInstructionPointer,intCodeList)

        val retval = processOpCode(opCode3InputValue,currentOpCode,parameterModes,currentInstructionPointer,intCodeList)
        intCodeList = retval.first
        currentInstructionPointer = retval.second
    }

}

fun processOpCode(opCode3InputValue: Int, opCode: OpCode, parameterModes : InstructionParameters, index: Int, intCodeList: MutableList<String>): Pair<MutableList<String>, Int> {

    var firstValue : Int
    var secondValue : Int
    var thirdValue : Int
    var newInstructionIndex: Int

    when (opCode) {
        OpCode.OPCODE1 -> {
            firstValue = if (parameterModes.param1) intCodeList[index + 1].toInt() else  intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue = if (parameterModes.param2) intCodeList[index + 2].toInt() else  intCodeList[intCodeList[index + 2].toInt()].toInt()
            thirdValue = intCodeList[index + 3].toInt()
            intCodeList[thirdValue] = (firstValue + secondValue).toString()
            newInstructionIndex = index + 4

        }
        OpCode.OPCODE2 -> {
            firstValue = if (parameterModes.param1) intCodeList[index + 1].toInt() else  intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue = if (parameterModes.param2) intCodeList[index + 2].toInt() else  intCodeList[intCodeList[index + 2].toInt()].toInt()
            thirdValue = intCodeList[index + 3].toInt()
            intCodeList[thirdValue] = (firstValue * secondValue).toString()
            newInstructionIndex = index + 4
        }
        OpCode.OPCODE3 -> {
            val indexToAssignOpsCode3 = intCodeList[index+1].toInt()
            intCodeList[indexToAssignOpsCode3] = opCode3InputValue.toString()
            newInstructionIndex = index + 2
        }
        OpCode.OPCODE4 -> {
            val indexToExtractOutput = intCodeList[index+1].toInt()
            println(intCodeList[indexToExtractOutput])
            newInstructionIndex = index + 2
        }
        OpCode.OPCODE5 -> {
            firstValue = if (parameterModes.param1) intCodeList[index + 1].toInt() else  intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue = if (parameterModes.param2) intCodeList[index + 2].toInt() else  intCodeList[intCodeList[index + 2].toInt()].toInt()
            if (firstValue != 0) newInstructionIndex = secondValue else newInstructionIndex = index + 3
        }
        OpCode.OPCODE6 ->{
            firstValue = if (parameterModes.param1) intCodeList[index + 1].toInt() else  intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue = if (parameterModes.param2) intCodeList[index + 2].toInt() else  intCodeList[intCodeList[index + 2].toInt()].toInt()
            if (firstValue == 0) newInstructionIndex = secondValue else newInstructionIndex = index + 3

        }
        OpCode.OPCODE7 ->{
            firstValue = if (parameterModes.param1) intCodeList[index + 1].toInt() else  intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue = if (parameterModes.param2) intCodeList[index + 2].toInt() else  intCodeList[intCodeList[index + 2].toInt()].toInt()
            thirdValue = intCodeList[index + 3].toInt()
            if (firstValue < secondValue) intCodeList[thirdValue] = "1" else intCodeList[thirdValue] = "0"
            newInstructionIndex = index + 4
        }
        OpCode.OPCODE8 ->{
            firstValue = if (parameterModes.param1) intCodeList[index + 1].toInt() else  intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue = if (parameterModes.param2) intCodeList[index + 2].toInt() else  intCodeList[intCodeList[index + 2].toInt()].toInt()
            thirdValue = intCodeList[index + 3].toInt()
            if (firstValue == secondValue) intCodeList[thirdValue] = "1" else intCodeList[thirdValue] = "0"
            newInstructionIndex = index + 4
        }

        //tidy this up and work out how to do this properly - it's a fucking mess
        OpCode.OPCODE99 -> throw Exception("Stop the program")
        OpCode.OPERROR -> throw Exception("All gone tits up")
    }

    return Pair(intCodeList,newInstructionIndex)
}

// so need to refactor this
fun getParameterModes(index: Int, intCodeList: List<String>): InstructionParameters {

    var paramCode: String
    if (intCodeList[index].length > 2) {
        paramCode = intCodeList[index].take(intCodeList[index].length - 2)

        var param1 = false
        var param2 = false
        var param3 = false

        when (paramCode.length) {
            1 -> return InstructionParameters(param1 = true)
            2 -> {
                if (paramCode[1] == '1') {
                    param1 = true
                }
                if (paramCode[0] == '1') {
                    param2 = true
                }
                return InstructionParameters(param1, param2)
            }

            3 -> {
                if (paramCode[2] == '1') {
                    param1 = true
                }
                if (paramCode[1] == '1') {
                    param2 = true
                }
                if (paramCode[0] == '1') {
                    param3 = true
                }
                return InstructionParameters(param1, param2, param3)
            }
            else -> return InstructionParameters()
        }
    }
    return InstructionParameters()
}

fun getOpCode(index: Int, intCodeList: List<String>): OpCode {

    val opCodeValue = intCodeList[index].takeLast(2)
    return when (opCodeValue) {
        "01", "1" -> OpCode.OPCODE1
        "02", "2" -> OpCode.OPCODE2
        "03", "3" -> OpCode.OPCODE3
        "04", "4" -> OpCode.OPCODE4
        "05", "5" -> OpCode.OPCODE5
        "06", "6" -> OpCode.OPCODE6
        "07", "7" -> OpCode.OPCODE7
        "08", "8" -> OpCode.OPCODE8
        "99" -> OpCode.OPCODE99
        else -> OpCode.OPERROR

    }

}