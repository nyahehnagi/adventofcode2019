package adventofcode

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat

//https://adventofcode.com/2019/day/1

class AdventofcodeTest {

    @Test
    fun `Should test that amount of fuel calculated is correct`() {
        val mass1 = 1969
        val mass2 = 100756

        assertThat(calculateFuel(mass1), equalTo(966))
        assertThat(calculateFuel(mass2), equalTo(50346))
    }


    @Test
    fun `Should test IntCode is correct after processing - Day 2`() {
        val intCode1 = "1,0,0,0,99"
        val intCode2 = "2,3,0,3,99"
        val intCode3 = "2,4,4,5,99,0"
        val intCode4 = "1,1,1,4,99,5,6,0,99"

        assertThat(calculateIntCodeFinalState(intCode1), equalTo("2,0,0,0,99"))
        assertThat(calculateIntCodeFinalState(intCode2), equalTo("2,3,0,6,99"))
        assertThat(calculateIntCodeFinalState(intCode3), equalTo("2,4,4,5,99,9801"))
        assertThat(calculateIntCodeFinalState(intCode4), equalTo("30,1,1,4,2,5,6,0,99"))
    }

}