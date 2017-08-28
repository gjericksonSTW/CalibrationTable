package Bluetooth

import javax.bluetooth.RemoteDevice

class BlueDev(    val address: String,
                  val localName: String,
                  var status: Boolean,
                  val addressType: String,
                  val classicDeviceRef: RemoteDevice?  ){

    override fun toString(): String {
        return "Device: $localName \n" +
                "Type: Classic \n" +
                "Connectable: $status \n"
    }
}



