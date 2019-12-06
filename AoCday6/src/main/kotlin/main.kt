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
    var allUniqueSpaceObjectsByName: List<String> = listOf()

    fun createObjectMap(orbitData: List<String>) {
        val tmpObject: MutableList<String> = mutableListOf()
        val tmpOrbitData = orbitData.toMutableList()

        universalCentreOfMass.buildObjectMap(tmpOrbitData)

        //Getting a list of all celestial bodies as will use this data later - not a great idea and no doubt there's a way to recurse through each SpaceObject
        orbitData.forEach {
            val parts = it.split(")")
            val spaceObjectName = parts[0]
            val tmpSpaceObjectOrbitedBy = parts[1]
            tmpObject.add(spaceObjectName)
            tmpObject.add(tmpSpaceObjectOrbitedBy)
        }
        allUniqueSpaceObjectsByName = tmpObject.toList().distinctBy { it }

    }

    fun countOrbits(): Int {
        //TODO think of a better way to go through the different objects rather than this foreach aka traverse the tree properly
        var counter: Int = 0
        allUniqueSpaceObjectsByName.forEach {
            counter += universalCentreOfMass.countNodes(it) - 1 // -1 because the node count is greater than the orbit count by 1 due to the counting of the the "COM" object
        }
        return counter
    }

    fun getShortestOrbitalTransfers(start: String, end: String): Int {
        val startTotalOrbitsToCOM = universalCentreOfMass.getTotalOrbitsForObject(start)
        val endTotalOrbitsToCOM = universalCentreOfMass.getTotalOrbitsForObject(end)

        val tmpList: MutableList<String> = mutableListOf()
        // There's probably a way to use maps and what not here. Need to learn more in this area
        // remove any matching SpaceObjects less the start and end objects and we have our shortest route
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

        tmpList.remove(start)
        tmpList.remove(end)

        return tmpList.count()
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
                    return if (retval[0] == it.spaceObjectName) retval else retval + listOf(it.spaceObjectName)
                }
            }
        }
        return emptyList()
    }

    fun countNodes(name: String): Int {
        if (spaceObjectName == name) {
            return 1
        }
        // Check direct Orbits
        if (objectDirectOrbitedBy.isNullOrEmpty()) {
            return 0
        } else {
            objectDirectOrbitedBy.forEach {
                val retval = it.countNodes(name)
                if (retval != 0) {
                    return retval + 1
                }
            }
            return 0
        }
    }

    fun buildObjectMap(orbitData: List<String>) {
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



