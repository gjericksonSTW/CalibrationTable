package utils.Bluetooth

class ManualTool (val CalibrationXpry: String,
                  val Id: String,
                  val MaximumTorque: Double,
                  val Firmware: String
                  ) {

    fun getMaxTorque() : Double{
        return MaximumTorque
    }

    override fun toString(): String {
        return "Expiration:\t" + CalibrationXpry +
                "\r\nID:\t" + Id
                "\r\nMaximum Torque:\t" + MaximumTorque
                "\r\nFirmware:\t" + Firmware
    }

}