package Control

import Bluetooth.BlueDev
import Bluetooth.ClassicBluetoothService
import SerialPort.COMPort
import SerialPort.SerialPortDelegate
import data.Scenes
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.HBox
import javafx.scene.text.Text
import javafx.util.Callback
import utils.Bluetooth.ManualTool
import utils.ValueChangeListener

class CommunicationControl : Controller<Any>() {

    private var delegate : SerialPortDelegate = SerialPortDelegate
    var ClassicService = ClassicBluetoothService
    private var serialConnected : Boolean = false
    private var bluetoothConnected : Boolean = false

    //Temporary virtual tool to demonstrate how tool information is used for calibration
    var connectedTool : ManualTool = ManualTool("04:05:17", "CEM50N3X12G_BTD", 200.0, "1.0")

    companion object {

        private val portList : ObservableList<COMPort> = FXCollections.observableArrayList()
        private val deviceList: ObservableList<BlueDev> = FXCollections.observableArrayList()
        private val BlueScene: FXMLLoader = FXMLLoader()

        fun pushDevice(dev: BlueDev) {
            var exists = false

            deviceList.onEach {
                if (it.localName == dev.localName)
                    exists = true
            }
            if (!exists)
                deviceList.add(dev)
        }

        fun addPort(port : COMPort){
            var exists = false
            portList.onEach {
                if(it.port == port.port)
                    exists = true
            }

            if(!exists)
                portList.add(port)
        }

        fun OpenBluetooth() {
            StageControl.swapCenter(BlueScene, Scenes.BLUESCENE)
        }

        fun getControl() : CommunicationControl {
            return BlueScene.getController<CommunicationControl>()
        }
    }

    @FXML private var BlueWrapper: HBox = HBox()
    @FXML private var blueView: TableView<BlueDev> = TableView()
    @FXML private var friendlyColumn: TableColumn<BlueDev, String> = TableColumn()

    @FXML private var bluetoothButton : Button = Button()
    @FXML private var serialButton : Button = Button()

    @FXML private var opened_port : Text = Text()
    @FXML private var connected_device : Text = Text()
    @FXML private var bt_title : Label = Label()
    @FXML private var serial_title: Label = Label()

    @FXML private var COMView : TableView<COMPort> = TableView()
    @FXML private var portColumn : TableColumn<COMPort, String> = TableColumn()

    override fun initialize() {

        ClassicService.Discoverable()
        ClassicService.createService()
        BlueWrapper.prefHeightProperty().bind(blueView.heightProperty())
        blueView.placeholder = Label("")
        COMView.placeholder = Label("")
        this.initDeviceTable()

        connected_device.managedProperty().bind(connected_device.visibleProperty())
        opened_port.managedProperty().bind(opened_port.visibleProperty())

        bluetoothButton.setOnAction {  ClassicService.searchClassic() }
        serialButton.setOnAction { delegate.getCOMPorts() }
    }

    fun connectedBluetooth() {
        bluetoothConnected = true
        bluetoothButton.styleClass.clear()
        bluetoothButton.styleClass.add("red_button")
        bt_title.text = "Connected Device"
        bluetoothButton.text = "Disconnect"
        connected_device.isVisible = true
        val name: String = ClassicService.getDevice()?.getFriendlyName(false).toString()

        deviceList.clear()
        connected_device.text = name
        bluetoothButton.setOnAction {  ClassicService.closeChannel() }

        CalibrationControl.getControl().updateButton()
    }

    fun openedPort(){
        serialConnected = true
        serialButton.styleClass.clear()
        serialButton.styleClass.add("red_button")
        serial_title.text = "Opened Port"
        serialButton.text = "Disconnect"
        opened_port.isVisible = true

        portList.clear()
        val port : String = delegate.getPort().portName
        opened_port.text = port
        serialButton.setOnAction { delegate.closePort() }

        CalibrationControl.getControl().updateButton()
    }

    fun refreshView(){
        serialConnected = false

        serialButton.styleClass.remove("red_button")
        serialButton.styleClass.add("orange_button")
        serial_title.text = "Available Ports"
        serialButton.text = "Refresh"
        opened_port.isVisible = false

        serialButton.setOnAction { delegate.getCOMPorts() }

        CalibrationControl.getControl().updateButton()
    }

    fun searchView(){
        bluetoothConnected = false

        bluetoothButton.styleClass.remove("red_button")
        bluetoothButton.styleClass.add("orange_button")
        bt_title.text = "Nearby Devices"
        connected_device.isVisible = false
        bluetoothButton.text = "Search"

        bluetoothButton.setOnAction {  ClassicService.searchClassic() }

        CalibrationControl.getControl().updateButton()
    }

    private fun initDeviceTable() {
        friendlyColumn.cellValueFactory = PropertyValueFactory<BlueDev, String>("localName")
        blueView.items = deviceList

        portColumn.cellValueFactory = PropertyValueFactory<COMPort, String>("port")
        COMView.items = portList

        bindColumnHeight(blueView)
        bindColumnHeight(COMView)


        blueView.rowFactory = Callback {
            val row = TableRow<BlueDev>()
            row.onMouseClicked = EventHandler { if (row.item != null) ClassicService.extractURL(row.item.classicDeviceRef) }
            row
        }

        COMView.rowFactory = Callback {
            val row = TableRow<COMPort>()
            row.onMouseClicked = EventHandler { delegate.openPort(row.item.port) }
            row
        }
    }

    fun checkConnections() : Boolean {
        return (bluetoothConnected and serialConnected)
    }

    fun enableButton(){
        bluetoothButton.isDisable = false
    }

    fun disableButton(){
        bluetoothButton.isDisable = true
    }

    private fun bindColumnHeight(table : TableView<*>){

        table.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY

        table.fixedCellSize = 25.0
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.items).add(1.1)))
        table.maxHeightProperty().bind(table.prefHeightProperty())
        table.minHeightProperty().bind(table.prefHeightProperty())
    }
}

