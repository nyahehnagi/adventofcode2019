package aocday7

import java.io.File
import java.lang.Exception

fun main() {

    //val intCode1 = listOf("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0")
    //val intCode1 = listOf("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0")
    //val intCode1 = listOf("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0")
    println(crackTheIntCode(getData("src/main/resources/day6input.csv")[0]))
    //println(crackTheIntCode(intCode1[0]))

}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}


fun crackTheIntCode(intCodeString: String): Int {

    var maxThrusterSignal = 0
    var ampBInput = 0
    var ampCInput = 0
    var ampDInput = 0
    var ampEInput = 0
    var endOutput = 0

    //val inputString = "3,8,1001,8,10,8,105,1,0,0,21,34,47,72,81,102,183,264,345,426,99999,3,9,102,5,9,9,1001,9,3,9,4,9,99,3,9,101,4,9,9,1002,9,3,9,4,9,99,3,9,102,3,9,9,101,2,9,9,102,5,9,9,1001,9,3,9,1002,9,4,9,4,9,99,3,9,101,5,9,9,4,9,99,3,9,101,3,9,9,1002,9,5,9,101,4,9,9,102,2,9,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,99,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,2,9,9,4,9,99,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,99,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,99,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,99"
    //readIntCode(listOf(4, 0), intCodeString)
/*
    ampBInput = readIntCode(listOf(2, 0), inputString)
    ampCInput = readIntCode(listOf(2, ampBInput), inputString)
    ampDInput = readIntCode(listOf(2, ampCInput), inputString)
    ampEInput = readIntCode(listOf(2, ampDInput), inputString)
    endOutput = readIntCode(listOf(2, ampEInput), inputString)

    return endOutput
*/


    for (a in 0..4) {
        for (b in 0..4) {
            for (c in 0..4) {
                for (d in 0..4) {
                    for (e in 0..4) {
                        if ( listOf(a,b,c,d,e).distinct().count() == 5) {
                            ampBInput = readIntCode(listOf(a, 0), intCodeString)
                            ampCInput = readIntCode(listOf(b, ampBInput), intCodeString)
                            ampDInput = readIntCode(listOf(c, ampCInput), intCodeString)
                            ampEInput = readIntCode(listOf(d, ampDInput), intCodeString)
                            endOutput = readIntCode(listOf(e, ampEInput), intCodeString)
                            if (endOutput >= maxThrusterSignal) {
                                maxThrusterSignal = endOutput
                            }
                        }
                    }
                }
            }
        }

    }

    return maxThrusterSignal
//685342372 is WRONG!

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

fun readIntCode(inputList: List<Int>, intCode: String): Int {
    var intCodeList = intCode.split(",").toMutableList()

    var currentInstructionPointer = 0 //track where we are in the code
    var inputCounterIndex = 0
    var nextInputValue = inputList[0]
    var outputValue: Int = 0

    while (currentInstructionPointer <= intCodeList.lastIndex) {

        val currentOpCode = getOpCode(currentInstructionPointer, intCodeList)
        val parameterModes = getParameterModes(currentInstructionPointer, intCodeList)

        val retval =
            processOpCode(nextInputValue, currentOpCode, parameterModes, currentInstructionPointer, intCodeList)

        intCodeList = retval.currentIntCode
        currentInstructionPointer = retval.nextInstructionIndex
        if (retval.nextInput) {
            inputCounterIndex += 1
            if (inputCounterIndex <= inputList.lastIndex) {
                nextInputValue = inputList[inputCounterIndex]
            }
        }

        if (retval.isOutput) {
            outputValue = retval.outputValue
        }

        if (retval.stopTheProgram) {
            break
        }
    }

    return outputValue
}


fun processOpCode(
    opCode3InputValue: Int,
    opCode: OpCode,
    parameterModes: InstructionParameters,
    index: Int,
    intCodeList: MutableList<String>
): IntCodeResponse {

    var firstValue: Int
    var secondValue: Int
    var thirdValue: Int
    var newInstructionIndex: Int = 0
    var nextInput = false
    var stopProgram = false
    var isOutput = false
    var outputValue = 0

    when (opCode) {
        OpCode.OPCODE1 -> {
            firstValue =
                if (parameterModes.param1) intCodeList[index + 1].toInt() else intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue =
                if (parameterModes.param2) intCodeList[index + 2].toInt() else intCodeList[intCodeList[index + 2].toInt()].toInt()
            thirdValue = intCodeList[index + 3].toInt()
            intCodeList[thirdValue] = (firstValue + secondValue).toString()
            newInstructionIndex = index + 4

        }
        OpCode.OPCODE2 -> {
            firstValue =
                if (parameterModes.param1) intCodeList[index + 1].toInt() else intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue =
                if (parameterModes.param2) intCodeList[index + 2].toInt() else intCodeList[intCodeList[index + 2].toInt()].toInt()
            thirdValue = intCodeList[index + 3].toInt()
            intCodeList[thirdValue] = (firstValue * secondValue).toString()
            newInstructionIndex = index + 4
        }
        OpCode.OPCODE3 -> {
            val indexToAssignOpsCode3 = intCodeList[index + 1].toInt()
            intCodeList[indexToAssignOpsCode3] = opCode3InputValue.toString()
            newInstructionIndex = index + 2
            nextInput = true
        }
        OpCode.OPCODE4 -> {
            val indexToExtractOutput = intCodeList[index + 1].toInt()
            outputValue = intCodeList[indexToExtractOutput].toInt()
            isOutput = true
            //println(intCodeList[indexToExtractOutput])
            newInstructionIndex = index + 2
        }
        OpCode.OPCODE5 -> {
            firstValue =
                if (parameterModes.param1) intCodeList[index + 1].toInt() else intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue =
                if (parameterModes.param2) intCodeList[index + 2].toInt() else intCodeList[intCodeList[index + 2].toInt()].toInt()
            if (firstValue != 0) newInstructionIndex = secondValue else newInstructionIndex = index + 3
        }
        OpCode.OPCODE6 -> {
            firstValue =
                if (parameterModes.param1) intCodeList[index + 1].toInt() else intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue =
                if (parameterModes.param2) intCodeList[index + 2].toInt() else intCodeList[intCodeList[index + 2].toInt()].toInt()
            if (firstValue == 0) newInstructionIndex = secondValue else newInstructionIndex = index + 3

        }
        OpCode.OPCODE7 -> {
            firstValue =
                if (parameterModes.param1) intCodeList[index + 1].toInt() else intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue =
                if (parameterModes.param2) intCodeList[index + 2].toInt() else intCodeList[intCodeList[index + 2].toInt()].toInt()
            thirdValue = intCodeList[index + 3].toInt()
            if (firstValue < secondValue) intCodeList[thirdValue] = "1" else intCodeList[thirdValue] = "0"
            newInstructionIndex = index + 4
        }
        OpCode.OPCODE8 -> {
            firstValue =
                if (parameterModes.param1) intCodeList[index + 1].toInt() else intCodeList[intCodeList[index + 1].toInt()].toInt()
            secondValue =
                if (parameterModes.param2) intCodeList[index + 2].toInt() else intCodeList[intCodeList[index + 2].toInt()].toInt()
            thirdValue = intCodeList[index + 3].toInt()
            if (firstValue == secondValue) intCodeList[thirdValue] = "1" else intCodeList[thirdValue] = "0"
            newInstructionIndex = index + 4
        }

        //tidy this up and work out how to do this properly - it's a fucking mess
        OpCode.OPCODE99 -> {
            stopProgram = true
            //throw Exception("Stop the program")
        }
        OpCode.OPERROR -> throw Exception("All gone tits up")
    }

    return IntCodeResponse(intCodeList, newInstructionIndex, nextInput, isOutput, stopProgram, outputValue)
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

data class IntCodeResponse(
    val currentIntCode: MutableList<String> = mutableListOf(),
    val nextInstructionIndex: Int = 0,
    val nextInput: Boolean = false,
    val isOutput: Boolean = false,
    val stopTheProgram: Boolean = false,
    val outputValue: Int = 0
)
