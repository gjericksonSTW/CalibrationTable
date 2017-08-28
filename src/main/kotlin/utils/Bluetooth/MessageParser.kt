package utils.Bluetooth

object MessageParser {


    var subStrings : List<String> = List(3, {""})

    fun splitString(msg : String) : Double{
        subStrings = msg.split(",")
        if(subStrings.size > 2) {
            return subStrings[2].toDouble()
        }
        return 0.0
    }

    fun Format(data : Double) : String{

        var formattedData : String

        if(data >= 100.0){
            formattedData = "${(data * 10).toInt()}"
            return formattedData
        }
        else if(data >= 10.0){
            formattedData = "0${(data * 10).toInt()}"
            return formattedData
        }
        else{
            formattedData = "00${(data * 10).toInt()}"
            return formattedData
        }
    }

}