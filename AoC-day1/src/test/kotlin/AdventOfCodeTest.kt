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

        assertThat(calculateFuel(mass1), equalTo(654))
        assertThat(calculateFuel(mass2), equalTo(33583))
    }
}