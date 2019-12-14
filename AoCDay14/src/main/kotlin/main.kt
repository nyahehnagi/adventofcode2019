package aocday14

import java.io.File
import java.lang.Math.floor
import kotlin.math.absoluteValue

fun main() {

    val elementMap = createDataSet(getData("src/main/resources/day14input.txt"))
    //val elementMap = createDataSet(getData("src/main/resources/test1.txt"))
    //val elementMap = createDataSet(getData("src/main/resources/test2.txt"))
    //val elementMap = createDataSet(getData("src/main/resources/test3.txt"))
    //val elementMap = createDataSet(getData("src/main/resources/test4.txt"))

    val totalOre = elementMap.calculateOreToMakeNumberOfElement("FUEL", 1)
    val ore: Long = 1000000000000
    //val fuel = elementMap.totalFuelFromOre(ore)
    println(totalOre)
    //println(fuel)
}


fun extractData(dataString: String): MutableList<Pair<String, Int>> {

    val instructionsList: MutableList<Pair<String, Int>> = mutableListOf()

    val parts = dataString.replace(",", "").replace("=>", "").split(" ")
    //get created element and amount created
    instructionsList.add(Pair(parts[parts.lastIndex], parts[parts.lastIndex - 1].toInt()))
    for (i in 0..(parts.size - 4) step 2) {
        instructionsList.add(Pair(parts[i + 1], parts[i].toInt()))
    }
    return instructionsList
}

fun createDataSet(inputData: List<String>): ElementMap {

    val elementMap = ElementMap()

    inputData.forEach {
        val dataList = extractData(it)
        val newElement = Element(dataList[0].first, dataList[0].second, dataList.subList(1, dataList.size))
        elementMap.elementMap.put(newElement.name, newElement)
    }

    return elementMap
}

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

class ElementMap {

    val elementMap: MutableMap<String, Element> = mutableMapOf()
    val surplus: MutableMap<String, Int> = mutableMapOf()

    fun totalFuelFromOre(totalOre: Long): Int {
        var totalFuel = 0
        var currentOreUsage: Long = 0

        while (currentOreUsage <= totalOre) {
            currentOreUsage += calculateOreToMakeNumberOfElement("FUEL", 1)
            totalFuel++
            println(currentOreUsage)
        }
        return totalFuel - 1
    }

    fun calculateOreToMakeNumberOfElement(name: String, amount: Int): Int {

        var currentTotalOre = 0

        if (name == "ORE") {
            currentTotalOre = amount
        } else {
            val currentElement: Element = elementMap[name]!!
            val createdByList: List<Pair<ElementName, NeedsAmount>> = currentElement.createdBy
            createdByList.forEach {
                val nextElement = elementMap[it.first]
                val nextAmount: Int
                val nextSurplus: Int

                val currentSurplus = if (surplus.containsKey(it.first)) surplus[it.first]!! else 0

                var amountOfThisNeeded = (it.second * amount) - currentSurplus
                if (amountOfThisNeeded <= 0) {
                    surplus[it.first] = amountOfThisNeeded.absoluteValue
                    amountOfThisNeeded = 0
                } else
                    surplus[it.first] = 0


                if (nextElement != null) {

                    if ((amountOfThisNeeded).rem(nextElement.amountCreated) > 0) {

                        nextAmount = floor(((amountOfThisNeeded) / nextElement!!.amountCreated).toDouble()).toInt() + 1
                        nextSurplus = nextAmount * nextElement.amountCreated - amountOfThisNeeded
                        surplus[it.first] = surplus[it.first]!! + nextSurplus

                    } else {
                        nextAmount = amountOfThisNeeded / nextElement.amountCreated
                        nextSurplus = nextAmount * nextElement.amountCreated - amountOfThisNeeded
                        surplus[it.first] = surplus[it.first]!! + nextSurplus

                    }
                } else {
                    // then next recursive call will be for an ORE.
                    nextAmount = amountOfThisNeeded

                }

                if (amountOfThisNeeded == 0) {
                    currentTotalOre += calculateOreToMakeNumberOfElement(it.first, 0)
                } else {
                    currentTotalOre += calculateOreToMakeNumberOfElement(it.first, nextAmount)
                }
            }
        }
        return currentTotalOre
    }

}

typealias ElementName = String
typealias NeedsAmount = Int

class Element(_name: String, _amountCreated: Int, _createdBy: List<Pair<String, Int>>) {

    val name: String
    val amountCreated: Int
    val createdBy: List<Pair<ElementName, NeedsAmount>>

    init {
        name = _name
        amountCreated = _amountCreated
        createdBy = _createdBy
    }

}