package aocday6

import java.io.File


fun main() {
    val orbitData = getData("src/main/resources/day6inputdata.txt")
    //val orbitData = listOf<String>("COM)B", "B)C", "C)D", "D)E", "E)F", "B)G", "G)H", "D)I", "E)J", "J)K", "K)L", "K)YOU", "I)SAN")
    buildObjectMap(orbitData)
}

fun buildObjectMap(orbitData: List<String>) {

    val spaceObjectMap = SpaceObjectMap()
    spaceObjectMap.createObjectMap(orbitData)
    spaceObjectMap.countOrbits()
    println(spaceObjectMap.countOrbits())
    println(spaceObjectMap.getShortestOrbitalTransfers("YOU", "SAN"))
}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

class SpaceObjectMap {

    var universalCentreOfMass = SpaceObject("COM")
    var allObjects: List<String> = listOf<String>()

    fun createObjectMap(orbitData: List<String>) {
        val tmpObject: MutableList<String> = mutableListOf()
        val tmpOrbitData = orbitData.toMutableList()

        universalCentreOfMass.buildObjectMap(tmpOrbitData)

        orbitData.forEach {
            val parts = it.split(")")
            val spaceObjectName = parts[0]
            val tmpSpaceObjectOrbitedBy = parts[1]
            tmpObject.add(spaceObjectName)
            tmpObject.add(tmpSpaceObjectOrbitedBy)


        }
        //terrible idea this.. need to get my head around recursing
        allObjects = tmpObject.toList().distinctBy { it }

    }

    fun countOrbits(): Int {
        //TODO think of a better way to go through the different objects rather than this foreach aka traverse the tree properly
        var counter: Int = 0
        allObjects.forEach {
            counter += universalCentreOfMass.countOrbits(it)

        }
        return counter - allObjects.count() // this is because I have a bug in my recursion and I could all the objects
    }

    fun getShortestOrbitalTransfers(start: String, end: String): Int {
        val startTotalOrbitsToCOM = universalCentreOfMass.getTotalOrbitsForObject(start)
        val endTotalOrbitsToCOM = universalCentreOfMass.getTotalOrbitsForObject(end)

        val tmpList: MutableList<String> = mutableListOf()
        // There's probably a way to use maps and what not here. Need to learn more in this area
        startTotalOrbitsToCOM.forEach {
            if (!endTotalOrbitsToCOM.contains(it)) {
                tmpList.add(it)
            }
        }
        endTotalOrbitsToCOM.forEach {
            if (!startTotalOrbitsToCOM.contains(it)) {
                tmpList.add(it)
            }
        }

        tmpList.remove(start) // I actually have 2 of these in the list.. something wrong with my recursion again
        tmpList.remove(end) // I actually have 2 of these in the list.. something wrong with my recursion again

        return tmpList.count() - 2 // minus 2 because I removed the intersecting node - horrible heh
    }
}

data class SpaceObject(
    var spaceObjectName: String,
    val objectDirectOrbitedBy: MutableList<SpaceObject> = mutableListOf()
) {

    fun getTotalOrbitsForObject(name: String): List<String> {
        if (spaceObjectName == name) {
            return listOf(name)
        }
        // Check direct Orbits
        if (objectDirectOrbitedBy.isNullOrEmpty()) {
            return emptyList()
        } else {
            objectDirectOrbitedBy.forEach {
                val retval = it.getTotalOrbitsForObject(name)
                if (retval.isNotEmpty()) {
                    return retval + listOf(it.spaceObjectName)
                }
            }
        }
        return emptyList()
    }

    fun countOrbits(name: String): Int {
        if (spaceObjectName == name) {
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
        val tmpOrbitData: MutableList<String> = orbitData.toMutableList()

        orbitData.forEach {
            val parts = it.split(")")
            val tmpSpaceObjectName = parts[0]
            val tmpSpaceObjectOrbitedBy = parts[1]

            if (spaceObjectName == tmpSpaceObjectName) {
                objectDirectOrbitedBy.add(SpaceObject(tmpSpaceObjectOrbitedBy))
                tmpOrbitData.remove(it)
            }
        }

        objectDirectOrbitedBy.forEach {
            it.buildObjectMap(tmpOrbitData)
        }
    }


}



