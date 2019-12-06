package aocday6

import java.io.File


fun main() {
    val orbitData = getData("src/main/resources/day6inputdata.txt")
    //val orbitData = listOf<String>("COM)B", "B)C", "C)D", "D)E", "E)F", "B)G", "G)H", "D)I", "E)J", "J)K", "K)L")
    buildObjectMap(orbitData)


}

fun buildObjectMap(orbitData: List<String>) {

    val spaceObjectMap = SpaceObjectMap()
    spaceObjectMap.createObjectMap(orbitData)
    spaceObjectMap.countOrbits()
    println(spaceObjectMap.countOrbits())


}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

class SpaceObjectMap {
    var universalCentreOfMass = SpaceObject("COM")
    var allObjects : List<String> = listOf<String>()

    fun createObjectMap(orbitData: List<String>) {
        val tmpObject : MutableList<String> = mutableListOf()
        val tmpOrbitData = orbitData.toMutableList()

        universalCentreOfMass.buildObjectMap(tmpOrbitData)

        orbitData.forEach {
            val parts = it.split(")")
            val spaceObjectName = parts[0]
            val tmpSpaceObjectOrbitedBy = parts[1]
            tmpObject.add(spaceObjectName)
            tmpObject.add(tmpSpaceObjectOrbitedBy)


        }
        //terrible idea this.. need to get my head around recursing through later on
        allObjects = tmpObject.toList().distinctBy { it }

    }

    fun countOrbits():Int{
        //TODO think of a better way to go through the different objects rather than this foreach
        var counter : Int = 0
        allObjects.forEach {
            counter += universalCentreOfMass.countOrbits(it)

        }
        return counter - allObjects.count()
    }
}

data class SpaceObject(
    var spaceObjectName: String,
    val objectDirectOrbitedBy: MutableList<SpaceObject> = mutableListOf()
) {

    fun countOrbits(name: String) : Int{
        if (spaceObjectName == name){
            return 1
        }
        // Check direct Orbits
        if (objectDirectOrbitedBy.isNullOrEmpty()) {
            return 0
        } else {
            objectDirectOrbitedBy.forEach {
                val retval = it.countOrbits(name)
                if (retval != 0) return retval + 1
            }
        }
        return 0
    }

    fun buildObjectMap(orbitData: MutableList<String>) {
        val tmpOrbitData : MutableList<String> = orbitData.toMutableList()

        orbitData.forEach {
            val parts = it.split(")")
            val tmpSpaceObjectName = parts[0]
            val tmpSpaceObjectOrbitedBy = parts[1]

            if (spaceObjectName == tmpSpaceObjectName){
                objectDirectOrbitedBy.add(SpaceObject(tmpSpaceObjectOrbitedBy))
                tmpOrbitData.remove(it)
            }
        }

        objectDirectOrbitedBy.forEach {
            it.buildObjectMap(tmpOrbitData)
        }
    }

}



