package aocday7

import java.io.File
import java.lang.Exception

fun main() {

    //val intCode1 = listOf("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0")
    //val intCode1 = listOf("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0")
    //val intCode1 = listOf("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0")
    //println(processor.crackTheIntCodePart2("3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"))
    //println(processor.crackTheIntCodePart2("3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10"))

    println(crackTheIntCodePart1(getData("src/main/resources/day6input.csv")[0]))
    println(crackTheIntCodePart2(getData("src/main/resources/day6input.csv")[0]))

}

fun crackTheIntCodePart1(intCodeString: String): Int {

    var maxThrusterSignal = 0
    var endOutput = 0
    val processor = Processor()

    for (a in 0..4) {
        for (b in 0..4) {
            for (c in 0..4) {
                for (d in 0..4) {
                    for (e in 0..4) {
                        if (listOf(a, b, c, d, e).distinct().count() == 5) {
                            endOutput = processor.singleLoop(listOf(a, b, c, d, e), intCodeString)
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
}
fun crackTheIntCodePart2(intCodeString: String): Int {

    var maxThrusterSignal = 0
    var endOutput = 0
    val processor = Processor()

    for (a in 5..9) {
        for (b in 5..9) {
            for (c in 5..9) {
                for (d in 5..9) {
                    for (e in 5..9) {
                        if (listOf(a, b, c, d, e).distinct().count() == 5) {
                            endOutput = processor.feedbackLoop(listOf(a, b, c, d, e), intCodeString)
                            if (endOutput >= maxThrusterSignal) {
                                maxThrusterSignal = endOutput
                            }
                        }
                    }
                }
            }
        }
    }

    //return feedbackLoop(listOf(9, 7, 8, 5, 6), intCodeString)
    //return feedbackLoop(listOf(9, 8, 7, 6, 5), intCodeString)
    return maxThrusterSignal
}


fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

class Processor {

    val inputA: MutableList<Int> = mutableListOf()
    val inputB: MutableList<Int> = mutableListOf()
    val inputC: MutableList<Int> = mutableListOf()
    val inputD: MutableList<Int> = mutableListOf()
    val inputE: MutableList<Int> = mutableListOf()

    fun clearInputOutput() {
        inputA.clear()
        inputB.clear()
        inputC.clear()
        inputD.clear()
        inputE.clear()
    }


    fun singleLoop(phaseList: List<Int>, intCodeString: String): Int {

        clearInputOutput()

        inputA.add(phaseList[0])
        inputA.add(0)
        inputB.add(phaseList[1])
        inputC.add(phaseList[2])
        inputD.add(phaseList[3])
        inputE.add(phaseList[4])

        val instructionA = InstructionProcessor(0, intCodeString.split(",").toMutableList())
        val instructionB = InstructionProcessor(0, intCodeString.split(",").toMutableList())
        val instructionC = InstructionProcessor(0, intCodeString.split(",").toMutableList())
        val instructionD = InstructionProcessor(0, intCodeString.split(",").toMutableList())
        val instructionE = InstructionProcessor(0, intCodeString.split(",").toMutableList())

        instructionA.readIntCode(inputA, inputB)
        instructionB.readIntCode(inputB, inputC)
        instructionC.readIntCode(inputC, inputD)
        instructionD.readIntCode(inputD, inputE)
        instructionE.readIntCode(inputE, inputA)

        return instructionE.internalOutputList.last()
    }

    fun feedbackLoop(phaseList: List<Int>, intCodeString: String): Int {

        clearInputOutput()

        inputA.add(phaseList[0])
        inputA.add(0)
        inputB.add(phaseList[1])
        inputC.add(phaseList[2])
        inputD.add(phaseList[3])
        inputE.add(phaseList[4])

        val instructionA = InstructionProcessor(0, intCodeString.split(",").toMutableList())
        val instructionB = InstructionProcessor(0, intCodeString.split(",").toMutableList())
        val instructionC = InstructionProcessor(0, intCodeString.split(",").toMutableList())
        val instructionD = InstructionProcessor(0, intCodeString.split(",").toMutableList())
        val instructionE = InstructionProcessor(0, intCodeString.split(",").toMutableList())

        do {
            if (!instructionA.haltProgam) {
                instructionA.readIntCode(inputA, inputB)
            }

            if (!instructionB.haltProgam) {
                instructionB.readIntCode(inputB, inputC)
            }

            if (!instructionC.haltProgam) {
                instructionC.readIntCode(inputC, inputD)
            }

            if (!instructionD.haltProgam) {
                instructionD.readIntCode(inputD, inputE)
            }

            if (!instructionE.haltProgam) {
                instructionE.readIntCode(inputE, inputA)
            }

        } while (!instructionE.haltProgam)

        return instructionE.internalOutputList.last()

    }

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

class InstructionProcessor(index: Int, intCodeList: MutableList<String>) {

    val internalOutputList: MutableList<Int> = mutableListOf()
    val currentIntCode: MutableList<String> = mutableListOf()
    var haltProgam: Boolean = false
    var waiting: Boolean = false
    var currentInstructionPointer = 0

    init {
        currentInstructionPointer = index
        currentIntCode.addAll(intCodeList)
    }

    fun readIntCode(
        inputList: MutableList<Int>,
        outputList: MutableList<Int>
    ): Int {

        while (currentInstructionPointer <= currentIntCode.lastIndex) {

            val currentOpCode = getOpCode(currentInstructionPointer, currentIntCode)
            val parameterModes = getParameterModes(currentInstructionPointer, currentIntCode)

            processOpCode(inputList, outputList, currentOpCode, parameterModes)

            if (waiting) {
                return 2
            }
            if (haltProgam) {
                return 0
            }
        }

        return 1
    }

    fun processOpCode(
        inputList: MutableList<Int>,
        outputList: MutableList<Int>,
        opCode: OpCode,
        parameterModes: InstructionParameters
    ) {

        var firstValue: Int
        var secondValue: Int
        var thirdValue: Int
        var outputValue = 0

        when (opCode) {
            OpCode.OPCODE1 -> {
                firstValue =
                    if (parameterModes.param1) currentIntCode[currentInstructionPointer + 1].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 1].toInt()].toInt()
                secondValue =
                    if (parameterModes.param2) currentIntCode[currentInstructionPointer + 2].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 2].toInt()].toInt()
                thirdValue = currentIntCode[currentInstructionPointer + 3].toInt()
                currentIntCode[thirdValue] = (firstValue + secondValue).toString()
                currentInstructionPointer += 4

            }
            OpCode.OPCODE2 -> {
                firstValue =
                    if (parameterModes.param1) currentIntCode[currentInstructionPointer + 1].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 1].toInt()].toInt()
                secondValue =
                    if (parameterModes.param2) currentIntCode[currentInstructionPointer + 2].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 2].toInt()].toInt()
                thirdValue = currentIntCode[currentInstructionPointer + 3].toInt()
                currentIntCode[thirdValue] = (firstValue * secondValue).toString()
                currentInstructionPointer += 4
            }
            OpCode.OPCODE3 -> {
                val indexToAssignOpsCode3 = currentIntCode[currentInstructionPointer + 1].toInt()

                if (inputList.size > 0) {
                    currentIntCode[indexToAssignOpsCode3] = inputList[0].toString()
                    inputList.removeAt(0)
                    currentInstructionPointer += 2
                    waiting = false
                } else {
                    waiting = true
                }

            }
            OpCode.OPCODE4 -> {
                val indexToExtractOutput = currentIntCode[currentInstructionPointer + 1].toInt()
                outputValue = currentIntCode[indexToExtractOutput].toInt()
                outputList.add(outputValue)
                internalOutputList.add(outputValue)
                currentInstructionPointer += 2
            }
            OpCode.OPCODE5 -> {
                firstValue =
                    if (parameterModes.param1) currentIntCode[currentInstructionPointer + 1].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 1].toInt()].toInt()
                secondValue =
                    if (parameterModes.param2) currentIntCode[currentInstructionPointer + 2].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 2].toInt()].toInt()
                if (firstValue != 0) currentInstructionPointer = secondValue else currentInstructionPointer += 3
            }
            OpCode.OPCODE6 -> {
                firstValue =
                    if (parameterModes.param1) currentIntCode[currentInstructionPointer + 1].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 1].toInt()].toInt()
                secondValue =
                    if (parameterModes.param2) currentIntCode[currentInstructionPointer + 2].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 2].toInt()].toInt()
                if (firstValue == 0) currentInstructionPointer = secondValue else currentInstructionPointer += 3

            }
            OpCode.OPCODE7 -> {
                firstValue =
                    if (parameterModes.param1) currentIntCode[currentInstructionPointer + 1].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 1].toInt()].toInt()
                secondValue =
                    if (parameterModes.param2) currentIntCode[currentInstructionPointer + 2].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 2].toInt()].toInt()
                thirdValue = currentIntCode[currentInstructionPointer + 3].toInt()
                if (firstValue < secondValue) currentIntCode[thirdValue] = "1" else currentIntCode[thirdValue] = "0"
                currentInstructionPointer += 4
            }
            OpCode.OPCODE8 -> {
                firstValue =
                    if (parameterModes.param1) currentIntCode[currentInstructionPointer + 1].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 1].toInt()].toInt()
                secondValue =
                    if (parameterModes.param2) currentIntCode[currentInstructionPointer + 2].toInt() else currentIntCode[currentIntCode[currentInstructionPointer + 2].toInt()].toInt()
                thirdValue = currentIntCode[currentInstructionPointer + 3].toInt()
                if (firstValue == secondValue) currentIntCode[thirdValue] = "1" else currentIntCode[thirdValue] = "0"
                currentInstructionPointer += 4
            }

            OpCode.OPCODE99 -> {
                haltProgam = true
            }
            OpCode.OPERROR -> throw Exception("All gone tits up")
        }
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
}