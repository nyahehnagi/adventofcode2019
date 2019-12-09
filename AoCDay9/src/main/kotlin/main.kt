package aocday9

import java.io.File
import java.lang.Exception

fun main() {
    //val intCodeDay9Test1 = "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99"
    //val intCodeDay9Test1 = "1102,34915192,34915192,7,4,7,99,0"
    //val intCodeDay9Test1 = "104,1125899906842624,99"
    day9(getData("src/main/resources/day9inputdata.txt")[0])
}

fun day9 (intCodeString: String) {
    val computer = Computer()

    computer.singleCall(1,intCodeString)
    println(computer.output)
    computer.clearInputOutput()
    computer.singleCall(2,intCodeString)
    println(computer.output)
}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

class Computer {

    val input: MutableList<Long> = mutableListOf()
    val output: MutableList<Long> = mutableListOf()

    fun clearInputOutput() {
        input.clear()
        output.clear()
    }

    fun singleCall(inputCode : Long, intCodeString: String) {
        val instruction = InstructionProcessor(0L, intCodeString.split(",").toMutableList())
        input.add(inputCode)
        instruction.processIntCode(input,output )
    }
}

class InstructionProcessor(index: Long, intCodeList: MutableList<String>) {

    val internalOutputList: MutableList<Long> = mutableListOf()
    val instructionMemory: MutableMap<Long, String> = mutableMapOf()
    var haltProgam: Boolean = false
    var waiting: Boolean = false
    var instructionPointer: Long = 0L
    var relativeBase: Long = 0

    init {
        instructionPointer = index
        intCodeList.toMutableList().forEachIndexed() { index, singleInstruction ->
            instructionMemory[index.toLong()] =
                singleInstruction
        }
    }

    fun processIntCode(
        inputList: MutableList<Long>,
        outputList: MutableList<Long>
    ): ReturnCode {

        do  {
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

    fun processOpCode(
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
                instructionPointer += 4

            }
            OpCode.OPCODE2 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                thirdValue = getThirdInstructionValue(parameterModes)
                assignValueToMemory(thirdValue, (firstValue * secondValue).toString())
                instructionPointer += 4
            }
            OpCode.OPCODE3 -> {

                var indexToAssignOpsCode3 = instructionMemory[instructionPointer + 1]!!.toLong()

                if (parameterModes.param1 == ParameterType.RELATIVE) {
                    indexToAssignOpsCode3 = indexToAssignOpsCode3 + relativeBase
                }

                if (inputList.size > 0) {
                    assignValueToMemory(indexToAssignOpsCode3, inputList[0].toString())
                    inputList.removeAt(0)
                    instructionPointer += 2
                    waiting = false
                } else {
                    waiting = true
                }

            }
            OpCode.OPCODE4 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                outputValue = firstValue
                outputList.add(outputValue)
                internalOutputList.add(outputValue)
                instructionPointer += 2
            }
            OpCode.OPCODE5 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                if (firstValue != 0L) instructionPointer = secondValue else instructionPointer += 3
            }
            OpCode.OPCODE6 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                if (firstValue == 0L) instructionPointer = secondValue else instructionPointer += 3

            }
            OpCode.OPCODE7 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                thirdValue = getThirdInstructionValue(parameterModes)
                if (firstValue < secondValue) assignValueToMemory(thirdValue, "1") else assignValueToMemory(thirdValue, "0")
                instructionPointer += 4
            }
            OpCode.OPCODE8 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                secondValue = getSecondInstructionValue(parameterModes)
                thirdValue = getThirdInstructionValue(parameterModes)
                if (firstValue == secondValue) assignValueToMemory(thirdValue, "1") else assignValueToMemory(thirdValue, "0")
                instructionPointer += 4
            }
            OpCode.OPCODE9 -> {
                firstValue = getfirstInstructionValue(parameterModes)
                relativeBase += firstValue
                instructionPointer += 2
            }

            OpCode.OPCODE99 -> {
                haltProgam = true
            }
            OpCode.OPERROR -> throw Exception("All gone tits up")
        }
    }

    fun assignValueToMemory(key: Long, value: String) {
        if (instructionMemory.containsKey(key)) instructionMemory[key] = value
        else instructionMemory[key] = value
    }

    //Checks to see if a key(memory address exists), if not, it creates one with value zero
    fun doesKeyExistIfNotCreate(key: Long)  {
        if (instructionMemory.containsKey(key)) //Do nothing
        else
            instructionMemory[key] = "0"
    }

    fun getfirstInstructionValue(parameterModes: InstructionParameters): Long {

        val firstParameterPointer = instructionPointer + 1
        doesKeyExistIfNotCreate(firstParameterPointer)

        return when (parameterModes.param1) {
            ParameterType.IMMEDIATE -> instructionMemory[firstParameterPointer]!!.toLong()
            ParameterType.POSITION -> {
                doesKeyExistIfNotCreate (instructionMemory[firstParameterPointer]!!.toLong())
                instructionMemory[instructionMemory[firstParameterPointer]!!.toLong()]!!.toLong()
            }
            ParameterType.RELATIVE -> {
                instructionMemory[instructionMemory[firstParameterPointer]!!.toLong() + relativeBase]!!.toLong()
            }
        }
    }

    fun getSecondInstructionValue(parameterModes: InstructionParameters): Long {

        val secondParameterPointer = instructionPointer + 2
        doesKeyExistIfNotCreate(secondParameterPointer)

        return when (parameterModes.param2) {
            ParameterType.IMMEDIATE -> instructionMemory[secondParameterPointer]!!.toLong()
            ParameterType.POSITION -> {
                doesKeyExistIfNotCreate (instructionMemory[secondParameterPointer]!!.toLong())
                instructionMemory[instructionMemory[secondParameterPointer]!!.toLong()]!!.toLong()
            }
            ParameterType.RELATIVE -> {
                doesKeyExistIfNotCreate (instructionMemory[secondParameterPointer]!!.toLong())
                instructionMemory[instructionMemory[secondParameterPointer]!!.toLong() + relativeBase]!!.toLong()
            }
        }
    }

    fun getThirdInstructionValue(parameterModes: InstructionParameters): Long {

        val thirdParameterPointer = instructionPointer + 3
        doesKeyExistIfNotCreate(thirdParameterPointer)

        return when (parameterModes.param3) {
            ParameterType.RELATIVE -> {
                instructionMemory[thirdParameterPointer]!!.toLong() + relativeBase
            }
            else -> instructionMemory[thirdParameterPointer]!!.toLong() //Can only otherwise be in Immediate as per spec
        }
    }

    // so need to refactor this
    fun getParameterModes(index: Long): InstructionParameters {

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

    fun getOpCode(index: Long): OpCode {

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
    OPCODE9,
    OPCODE99,
    OPERROR
}

enum class ReturnCode {
    UNKNOWN,
    WAITING,
    END
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