package utils.Certificate

import utils.Bluetooth.ManualTool
import java.sql.Timestamp

class CalibrationRecord {

    private var userInfo : UserInformation? = null
        get() = userInfo

    private var timeStamp : Timestamp? = null
        get() = timeStamp

    private var toolInformation : ManualTool? = null
        get() = toolInformation

    private var calbriData : CalibrationData? = null
        get() = calbriData


    fun setUserInfo(user: UserInformation){
        this.userInfo = user
    }

    fun createTimeStamp(){
        this.timeStamp = Timestamp(System.currentTimeMillis())
    }

    fun setToolInformation(tool : ManualTool){
        toolInformation = tool
    }

    fun setCalibrationData(calibrationData: CalibrationData){
        calbriData = calibrationData
    }

    class CalibrationData( val Results : DoubleArray,
                                   val Pass : Boolean,
                                   val Constants : FloatArray,
                                   val PercentDiff : DoubleArray )


    class UserInformation ( val firstName : String,
                            val lastName  : String,
                            val department: String ){

        override fun toString(): String {
            return super.toString()
        }
    }

}