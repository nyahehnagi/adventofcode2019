package adventofcode

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat

//https://adventofcode.com/2019/day/1

class AdventofcodeTest {

    @Test
    fun `Should test the shortest manhatten distance for 2 wires that cross`() {
        val wire1 = "R75,D30,R83,U83,L12,D49,R71,U7,L72"
        val wire2 = "U62,R66,U55,R34,D71,R55,D58,R83"
        val wire3 = "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51"
        val wire4 = "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"

        assertThat(calculateManhattenDistance(wire1,wire2), equalTo(159))
        assertThat(calculateManhattenDistance(wire3,wire4), equalTo(135))
    }


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

        //assertThat(calculateIntCodeFinalState(intCode1), equalTo("2,0,0,0,99"))
        //assertThat(calculateIntCodeFinalState(intCode2), equalTo("2,3,0,6,99"))
        //assertThat(calculateIntCodeFinalState(intCode3), equalTo("2,4,4,5,99,9801"))
        //assertThat(calculateIntCodeFinalState(intCode4), equalTo("30,1,1,4,2,5,6,0,99"))
    }

}