package aocday5

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat

//https://adventofcode.com/2019/day/1

class AdventofcodeTest {

    @Test
    fun `Should test a correct opCode is returned`() {
        val intCode1 = listOf("1","0","0","0","99")
        val index1 = 0
        val intCode2 = listOf("2","3","0","3","99")
        val index2 = 4
        val intCode3 = listOf("2","3","0","5","99")
        val index3 = 3
        val intCode4 = listOf("02","3","0","5","99")
        val index4 = 0

        assertThat(getOpCode(index1, intCode1), equalTo(OpCode.OPCODE1))
        assertThat(getOpCode(index2, intCode2), equalTo(OpCode.OPCODE99))
        assertThat(getOpCode(index3, intCode3), equalTo(OpCode.OPERROR))
        assertThat(getOpCode(index4, intCode4), equalTo(OpCode.OPCODE2))

    }

    @Test
    fun `Should test that paramater modes are returned`() {
        val intCode1 = listOf("1","0","0","0","99")
        val index1 = 0
        val intCode2 = listOf("2","3","0","3","99")
        val index2 = 4
        val intCode3 = listOf("11102","3","0","5","99")
        val index3 = 0
        val intCode4 = listOf("1102","3","0","5","99")
        val index4 = 0

        assertThat(getParameterModes(index1, intCode1), equalTo(InstructionParameters(false,false,false)))
        assertThat(getParameterModes(index2, intCode2), equalTo(InstructionParameters(false,false,false)))
        assertThat(getParameterModes(index3, intCode3), equalTo(InstructionParameters(true,true,true)))
        assertThat(getParameterModes(index4, intCode4), equalTo(InstructionParameters(true, true, false)))

    }

    @Test
    fun `Should test that an Ops code 2 works `() {
        val intCode1 = mutableListOf("1002","4","3","4","33")
        val opCode = OpCode.OPCODE2
        val params = InstructionParameters(param2 = true)
        val index1 = 0

        "3","12","6","12","15","1","13","14","13","4","13","99","-1","0","1","9"

        assertThat(processOpCode(0,opCode,params,index1,intCode1).first, equalTo(mutableListOf("1002","4","3","4","99")))

    }

}