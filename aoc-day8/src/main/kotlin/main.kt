package aocday8

import java.io.File

fun getData(filename: String): List<String> {
    val data = File(filename)
    return data.readLines()
}

fun main(){

    val image  = Image(25,6, getData("src/main/resources/day8inputdata.txt")[0])
    //val image = Image(3,2, "123456789012")
    //val image = Image(2,2,"0222112222120000")
    println(image.getPart1())


    val output :String = image.getPart2()
    output.replace("1","X")
    var index = 0
    while (index < output.count()) {
        println(output.substring(index, index + (25)).replace("1","X").replace("0"," "))
        index +=  25
    }

}

class Layer(layerString: String){

    val layerData : MutableList<Int> = mutableListOf()

    init{
        layerString.forEach {
            layerData.add(it.toString().toInt())
        }
    }

    fun digitCount(digit : Int) : Int{
        return layerData.count { it == digit }
    }
}

class Image(_width : Int, _height : Int, imageString : String){

    val width : Int = _width
    val height : Int = _height

    val layerList : MutableList<Layer> = mutableListOf()

    init{
        createLayers(imageString)
    }

    fun createLayers (imageString : String){

        var index  = 0

        while (index < imageString.count()) {
            layerList.add (Layer(imageString.substring(index, index + (width * height))))
            index +=  width * height
        }
    }

    fun getLayerWithLeastNumberOfDigits(digit : Int) : Int {

        var minCount = layerList[0].digitCount(digit)
        var minLayer = 0
        layerList.forEachIndexed { index, layer ->
            val digitCount = layer.digitCount(digit)
            if ( digitCount < minCount){
                minCount = digitCount
                minLayer = index
            }
        }

        return minLayer
    }

    fun getPart2(): String{

        var visible : String = ""
        for (i in 0..layerList[0].layerData.lastIndex){
            visible = visible + getVisibleColour(0,i).toString()
        }

        return visible
    }


    fun getVisibleColour(layerNumber : Int, layerIndex : Int) : Int {
        if (layerList[layerNumber].layerData[layerIndex] == 0 || layerList[layerNumber].layerData[layerIndex] == 1)
            return layerList[layerNumber].layerData[layerIndex]
        else
            return getVisibleColour (layerNumber + 1, layerIndex)
    }

    fun getPart1 (): Int{
        val indexOfLayerWithFewestZeros = getLayerWithLeastNumberOfDigits(0)
        val oneCount = layerList[indexOfLayerWithFewestZeros].digitCount(1)
        val twoCount = layerList[indexOfLayerWithFewestZeros].digitCount(2)

        return oneCount * twoCount
    }
}

