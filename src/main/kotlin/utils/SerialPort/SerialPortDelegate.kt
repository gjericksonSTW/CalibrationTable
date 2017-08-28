package SerialPort

import Control.CalibrationControl
import Control.CommunicationControl
import Control.StageControl
import jssc.*
import utils.Bluetooth.MessageParser

object SerialPortDelegate {

    private var PortNames : Array<String> = SerialPortList.getPortNames()
    private var Port : SerialPort = SerialPort("")
    private var sData = ""

    private val listener = SerialPortEventListener { event ->
        var eType : Int = event.eventType
        if (eType == SerialPortEvent.RXCHAR)
            try {
                sData = Port.readString(12)
                Port.readString(2)
                CalibrationControl.getControl().updateScene("SERIAL", MessageParser.splitString(sData))
            } catch (e: SerialPortException) {
                println(e)
            }
     }

    fun getCOMPorts(){
        PortNames.forEach{
            var comPort = COMPort(it)
            CommunicationControl.addPort(comPort)
        }
    }

    fun getPort(): SerialPort{
        return Port
    }

    fun closePort(){
        try {
            Port.closePort()
        }catch(e : Exception){
            println(e)
        }
        CommunicationControl.getControl().refreshView()
        StageControl.connectionChanged("COM")
    }

    fun openPort(port: String) {

        if (!Port.isOpened) {
            Port = SerialPort(port)
            try {
                Port.openPort()
                StageControl.connectionChanged("COM")
            } catch(e: SerialPortException) {
            println(e)  }
            try {
                Port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_7, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
            } catch (e: SerialPortException) {
                println(e)
            }
            try {
                Port.addEventListener(listener)
            } catch(e: SerialPortException) {
                println(e)
            }
            CommunicationControl.getControl().openedPort()
        }
    }
}