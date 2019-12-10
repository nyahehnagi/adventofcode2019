package aocday10

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat

//https://adventofcode.com/2019/day/1

class AdventofcodeTest {

    @Test
    fun `Should test width and height of a map is correct`() {
        val rawAsteroidData = listOf<String>(".#..#",".....","#####","....#","...##")
        val asteroidMap = AsteroidMap(rawAsteroidData)

        assertThat(asteroidMap.mapHeight, equalTo(5))
        assertThat(asteroidMap.mapWidth, equalTo(5))
    }

    @Test
    fun `Should test that the calculation of the greatest common divisor`(){
        val rawAsteroidData = listOf<String>(".#..#",".....","#####","....#","...##")
        val asteroidMap = AsteroidMap(rawAsteroidData)

        val coordinateTwoFour = Coordinate(2,4)
        val coordinateThreeNine = Coordinate(3,9)
        val coordinateFiveTen = Coordinate(5,10)

        assertThat(asteroidMap.greatestCommonDivisor(coordinateTwoFour), equalTo(2))
        assertThat(asteroidMap.greatestCommonDivisor(coordinateThreeNine), equalTo(3))
        assertThat(asteroidMap.greatestCommonDivisor(coordinateFiveTen), equalTo(5))
    }

}