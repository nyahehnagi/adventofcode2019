package aocday7

import java.io.File
import java.lang.Exception
import kotlinx.coroutines.*

fun main() {

    val processor = Processor()

    //val intCode1 = listOf("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0")
    //val intCode1 = listOf("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0")
    //val intCode1 = listOf("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0")
    println(processor.crackTheIntCodePart2(getData("src/main/resources/day6input.csv")[0]))
    //println(processor.crackTheIntCodePart2("3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"))
    //println(processor.crackTheIntCodePart2("3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10"))

    //println(crackTheIntCode(intCode1[0]))

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

    /*
        fun singleLoop(phaseList: List<Int>, intCodeString: String): Int {

            clearInputOutput()

            inputA.add(phaseList[0])
            inputA.add(0)
            inputB.add(phaseList[1])
            inputC.add(phaseList[2])
            inputD.add(phaseList[3])
            inputE.add(phaseList[4])

            runBlocking {
                launch() {
                    inputA.addAll(outputE)
                    readIntCode(inputA, outputA, intCodeString)
                    inputB.addAll(outputA)
                    readIntCode(inputB, outputB, intCodeString)
                    inputC.addAll(outputB)
                    readIntCode(inputC, outputC, intCodeString)
                    inputD.addAll(outputC)
                    readIntCode(inputD, outputD, intCodeString)
                    inputE.addAll(outputD)
                    readIntCode(inputE, outputE, intCodeString)
                }
            }
            return outputE[0]
        }
    */
    fun crackTheIntCodePart2(intCodeString: String): Int {

        var maxThrusterSignal = 0
        var endOutput = 0


        for (a in 5..9) {
            for (b in 5..9) {
                for (c in 5..9) {
                    for (d in 5..9) {
                        for (e in 5..9) {
                            if (listOf(a, b, c, d, e).distinct().count() == 5) {
                                endOutput = feedbackLoop(listOf(a, b, c, d, e), intCodeString)
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


    fun feedbackLoop(phaseList: List<Int>, intCodeString: String): Int {

        clearInputOutput()

        inputA.add(phaseList[0])
        inputA.add(0)
        inputB.add(phaseList[1])
        inputC.add(phaseList[2])
        inputD.add(phaseList[3])
        inputE.add(phaseList[4])



        val instructionA = Instruction()
        val instructionB = Instruction()
        val instructionC = Instruction()
        val instructionD = Instruction()
        val instructionE = Instruction()


        var intCodeList1 = intCodeString.split(",").toMutableList()
        var intCodeList2 = intCodeString.split(",").toMutableList()
        var intCodeList3 = intCodeString.split(",").toMutableList()
        var intCodeList4 = intCodeString.split(",").toMutableList()
        var intCodeList5 = intCodeString.split(",").toMutableList()


        var lastOutputValue: Int = 0

        var response1 = IntCodeResponse(intCodeList1, 0, false, false)
        var response2 = IntCodeResponse(intCodeList2, 0, false, false)
        var response3 = IntCodeResponse(intCodeList3, 0, false, false)
        var response4 = IntCodeResponse(intCodeList4, 0, false, false)
        var response5 = IntCodeResponse(intCodeList5, 0, false, false)

        do {
            if (!instructionA.haltProgam) {
                response1 =
                    instructionA.readIntCode(
                        inputA,
                        inputB,
                        response1.currentIntCode,
                        response1.nextInstructionIndex
                    )
                println("A" + instructionA.internalOutputList)
            }


            if (!instructionB.haltProgam) {
                response2 =
                    instructionB.readIntCode(inputB, inputC, response2.currentIntCode, response2.nextInstructionIndex)
                println("B" + instructionB.internalOutputList)
            }


            if (!instructionC.haltProgam) {
                response3 =
                    instructionC.readIntCode(inputC, inputD, response3.currentIntCode, response3.nextInstructionIndex)
                println("C" + instructionC.internalOutputList)

            }

            if (!instructionD.haltProgam) {
                response4 =
                    instructionD.readIntCode(inputD, inputE, response4.currentIntCode, response4.nextInstructionIndex)
                println("D" + instructionD.internalOutputList)
            }

            if (!instructionE.haltProgam) {
                response5 =
                    instructionE.readIntCode(inputE, inputA, response5.currentIntCode, response5.nextInstructionIndex)
                println("E" + instructionE.internalOutputList)
            }

        } while (!instructionE.haltProgam)
println("Completed loop")
        return instructionE.internalOutputList.last()

    }

}


/*
    fun crackTheIntCodePart1(intCodeString: String): Int {

        var maxThrusterSignal = 0
        var endOutput = 0

        for (a in 0..4) {
            for (b in 0..4) {
                for (c in 0..4) {
                    for (d in 0..4) {
                        for (e in 0..4) {
                            if (listOf(a, b, c, d, e).distinct().count() == 5) {
                                endOutput = singleLoop(listOf(a, b, c, d, e), intCodeString)
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
*/






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


data class IntCodeResponse(
    val currentIntCode: MutableList<String> = mutableListOf(),
    val nextInstructionIndex: Int = 0,
    val stopTheProgram: Boolean = false,
    val waitingForInput: Boolean = false
)

class Instruction {

    val internalOutputList: MutableList<Int> = mutableListOf()
    var haltProgam: Boolean = false
    var waiting: Boolean = false

    fun readIntCode(
        inputList: MutableList<Int>,
        outputList: MutableList<Int>,
        intCodeList: MutableList<String>,
        index: Int
    ): IntCodeResponse {

        var currentInstructionPointer = index //track where we are in the code

        while (currentInstructionPointer <= intCodeList.lastIndex) {

            val currentOpCode = getOpCode(currentInstructionPointer, intCodeList)
            val parameterModes = getParameterModes(currentInstructionPointer, intCodeList)

            val retval =
                processOpCode(
                    inputList,
                    outputList,
                    currentOpCode,
                    parameterModes,
                    currentInstructionPointer,
                    intCodeList
                )

            //intCodeList = retval.currentIntCode
            currentInstructionPointer = retval.nextInstructionIndex

            if (retval.waitingForInput) {
                return retval
            }

            if (retval.stopTheProgram) {
                return retval
            }
        }
        return IntCodeResponse()
    }


    fun processOpCode(
        inputList: MutableList<Int>,
        outputList: MutableList<Int>,
        opCode: OpCode,
        parameterModes: InstructionParameters,
        index: Int,
        intCodeList: MutableList<String>
    ): IntCodeResponse {

        var firstValue: Int
        var secondValue: Int
        var thirdValue: Int
        var newInstructionIndex: Int = 0
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

                if (inputList.size > 0) {
                    intCodeList[indexToAssignOpsCode3] = inputList[0].toString()
                    inputList.removeAt(0)
                    newInstructionIndex = index + 2
                    waiting = false
                } else {
                    waiting = true
                    newInstructionIndex = index
                }

            }
            OpCode.OPCODE4 -> {
                val indexToExtractOutput = intCodeList[index + 1].toInt()
                outputValue = intCodeList[indexToExtractOutput].toInt()
                outputList.add(outputValue)
                internalOutputList.add(outputValue)
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

            OpCode.OPCODE99 -> {
                haltProgam = true
            }
            OpCode.OPERROR -> throw Exception("All gone tits up")
        }

        return IntCodeResponse(intCodeList, newInstructionIndex, haltProgam, waiting)
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