package Bluetooth
import Control.CalibrationControl
import Control.CommunicationControl
import Control.StageControl
import javafx.application.Platform
import utils.Bluetooth.MessageParser
import java.io.*
import java.util.*
import javax.bluetooth.*
import javax.bluetooth.UUID
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection
import javax.microedition.io.StreamConnectionNotifier
import kotlin.concurrent.timerTask
import kotlin.properties.Delegates

object ClassicBluetoothService {

    private var lDevice : LocalDevice =  LocalDevice.getLocalDevice()
    private val agent : DiscoveryAgent = lDevice.discoveryAgent

    val lock = java.lang.Object()

    private var msg_flag : Boolean = false
    private var connected = true
    private lateinit var t : Thread

    private var chosenDevice : RemoteDevice? = null
    private var stream : StreamConnectionNotifier? = null
    private var host : StreamConnection? = null
    private lateinit var client : StreamConnection
    private lateinit var m_os : DataOutputStream
    private lateinit var m_is : DataInputStream

    lateinit private var ep_url : String

    private val mUUID : Long = 0x0003
    private val ServiceUUID : UUID = UUID(mUUID)
    private var mURL : String = "btspp://localhost:" + ServiceUUID.toString() + ";name=Calibration;authenticate=false;encrypt=false"

    private var bluetoothMessage: Double by Delegates.observable(
            initialValue = 0.0,
            onChange = {
                prop, old, new -> CalibrationControl.getControl().updateScene("BLUETOOTH", new)
            }
    )

    private var mListener: DiscoveryListener = object: DiscoveryListener {

        override fun deviceDiscovered(bDevice: RemoteDevice, bClass: DeviceClass) {
            try {
                val fName = bDevice.getFriendlyName(false)

                if (fName == "") return
                val device =  BlueDev(bDevice.bluetoothAddress, fName, true, "", bDevice)

                println(device.toString())

                synchronized(lock) { CommunicationControl.pushDevice(device) }

            } catch(noName: IOException) {
            }
        }

        override fun inquiryCompleted(discType: Int) {

            try {
                println("Finished device discovery")
                CommunicationControl.getControl().enableButton()

                synchronized(lock) {
                    lock.notifyAll()
                }
            }
            catch (e : InterruptedException){
                println(e)
            }
        }

        override fun serviceSearchCompleted(p0: Int, p1: Int) {
            synchronized(lock){
                lock.notifyAll()
            }

            println("finished service discovery")

            t = startCommunication()

            if(ep_url != null) {
                openChannel()
            }
        }

        override fun servicesDiscovered(p0: Int, services: Array<out ServiceRecord>?) {
            println("Discovered a service from: " + chosenDevice?.getFriendlyName(true))
            println("Number of services: " + services?.size.toString())
            services?.forEach {
                var spp_url: String = it.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, true)
                if (spp_url.startsWith("btspp://")) {
                    ep_url = spp_url
                }
            }
        }
    }

    fun getDevice() : RemoteDevice? {

        return chosenDevice
    }

    fun extractURL(remoteDevice: RemoteDevice?){
        chosenDevice = remoteDevice as RemoteDevice
        Thread(this::targetDevice).start()
    }

    fun createService(){
        Thread(this::openSocket).start()
    }

    private fun startCommunication() : Thread = Thread {

        while (connected) {
            rx()
            if (msg_flag) {
                msg_flag = false

            }
        }
        t.join()
    }

    private fun openChannel() {

        println("Attempting to open channel")
        try {

            client = Connector.open(ep_url) as StreamConnection

            // Use Timer to allow time for the Connector to fully open SPP connection
            var time  = Timer("", false)
            time.schedule(timerTask {

                m_os = client.openDataOutputStream()
                m_is = client.openDataInputStream()

                println("connected")

                connected = true

                StageControl.connectionChanged("Tool")

                Platform.runLater({
                    CommunicationControl.getControl().connectedBluetooth()
                })

                t.start()

            }, 1000)

        } catch(e: IOException) {
            e.printStackTrace()
        }
    }

    fun closeChannel(){
        connected = false

        if(client != null)
            client.close()
        if(m_is != null)
            m_is.close()
        if(m_os != null)
            m_os.close()

        Platform.runLater({
            CommunicationControl.getControl().searchView()
        })
        StageControl.connectionChanged("Tool")
        Discoverable()
    }

    private fun tx(msg: String){

        try {
            m_os.write(msg.toByteArray())
        }
        catch(e : Exception){
            closeChannel()
        }
    }

    private fun rx(){

        var bytes: ByteArray = kotlin.ByteArray(128)
        if( m_is.available() > 0){
            val read: Int = m_is.available()
            m_is.read(bytes)
            val msg = String(bytes, 0, read)
            bluetoothMessage = MessageParser.splitString(msg)
            println(bluetoothMessage)
        }
    }

    private fun openSocket(){

        try {
            stream = Connector.open(mURL) as StreamConnectionNotifier
        }catch(e: IOException){
            e.printStackTrace()
        }
        try{
            host = stream?.acceptAndOpen()
        }catch (e : IOException){ }
        try{

            m_os = host!!.openDataOutputStream()
            m_is = host!!.openDataInputStream()

            val connected = true

            val msg = "Connected to the STS Calibration Table"

            m_os.write(msg.toByteArray())

            while(connected){
                rx()
            }
        }catch(e : IOException){
            e.printStackTrace()
        }
    }

    private fun targetDevice() {

        synchronized(lock) {
            try {
                var uuidSet: Array<UUID> = Array(2) { ServiceUUID  }
                uuidSet[1] = UUID(0x1101)

                var attrs : IntArray = intArrayOf(0,1,4)

                agent.searchServices(attrs, uuidSet, chosenDevice, mListener)
                lock.wait()
            } catch(e: InterruptedException) {
                println(e)
            }
        }
    }

    fun searchClassic() {
        Thread(this::search).start()
    }

    fun Discoverable() {
        if(lDevice.discoverable != DiscoveryAgent.GIAC)
        try {
            lDevice.discoverable = DiscoveryAgent.GIAC
        }catch(e : Exception){
            println(e)
        }
    }

    fun sendData(msg: String){
        tx(msg)
    }

    private fun search() {
        CommunicationControl.getControl().disableButton()
        synchronized(lock) {
            val started: Boolean = lDevice.discoveryAgent?.startInquiry(DiscoveryAgent.GIAC, mListener)!!
            if (started) {
                println("Scanning for Devices . . .")
                lock.wait()
            }
        }
    }
}