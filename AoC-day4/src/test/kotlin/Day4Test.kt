package aocday4

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat

class AdventofcodeTest {

    @Test
    fun `Should test that a combination is valid or false`() {
        val number1 = 122345
        val number2 = 223450
        val number3 = 123789
        val number4 = 123444
        val number5 = 111122
        val number6 = 334666

        assertThat(isValidCombination(number1), equalTo(true))
        assertThat(isValidCombination(number2), equalTo(false))
        assertThat(isValidCombination(number3), equalTo(false))
        assertThat(isValidCombination(number4), equalTo(false))
        assertThat(isValidCombination(number5), equalTo(true))
        assertThat(isValidCombination(number6), equalTo(true))

    }
}
