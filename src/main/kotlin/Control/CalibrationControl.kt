package Control

import data.Scenes
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import utils.Bluetooth.MessageParser

class CalibrationControl : Controller<Any>() {

    companion object {

        var calScene : FXMLLoader = FXMLLoader()

        fun openCalibration(){
            StageControl.swapCenter(calScene, Scenes.CALSCENE)
        }

        fun getControl() : CalibrationControl{
            return calScene.getController<CalibrationControl>()
        }
    }

    @FXML private var xAxis : NumberAxis = NumberAxis()
    @FXML private var yAxis : NumberAxis = NumberAxis()
    @FXML private var calibrationButton : Button = Button()
    @FXML private var CalibrationChart : LineChart<Number, Number> = LineChart<Number, Number>(xAxis, yAxis)
    @FXML private var infoBox : TextArea = TextArea()

    private var targetString = "Target Torque: "
    private var target = 0.0
    private var maxTorque = 0.0
    private var dataSeries : XYChart.Series<Number, Number> = XYChart.Series()
    private var deviceControl : CommunicationControl? = null
    private var increment : Double = 0.0

    override fun initialize() {
        dataSeries.name = "Voltage/Torque"
        infoBox.text = targetString + target
        infoBox.managedProperty().bind(infoBox.visibleProperty())
        infoBox.isVisible = false

        infoBox.textProperty().addListener( { _ -> infoBox.scrollTop = Double.MAX_VALUE })

        calibrationButton.isDisable = true
        calibrationButton.setOnAction { beginCalibration()
                                        //Temporary
                                        deviceControl?.ClassicService!!.sendData("W120000\r\n")}
    }

    private fun beginCalibration() {
        deviceControl = CommunicationControl.getControl()
        maxTorque = deviceControl?.connectedTool!!.MaximumTorque
        increment =  maxTorque / 5

        Platform.runLater({
            calibrationButton.styleClass.clear()
            calibrationButton.styleClass.add("green_button")
            calibrationButton.text = "Next"
            calibrationButton.isDisable = true
            calibrationButton.setOnAction { updateScene("PROGRESS", target) }
            infoBox.isVisible = true
            CalibrationChart.data.add(dataSeries)
            }
        )
    }

    private fun updateChart(value : Double){
        Platform.runLater({
            dataSeries.data.add(XYChart.Data(target, value))
            target += increment
            updateScene("PROGRESS", 0.0)
        })
    }

    fun updateScene(type: String, value: Double){
        when (type){
            "PROGRESS" -> {
                infoBox.appendText("\r\nNew Target: $target")
                var msg = MessageParser.Format(target)
                deviceControl?.ClassicService!!.sendData("W12$msg\r\n")
            }
            "BLUETOOTH" -> {
                infoBox.appendText("\r\nReceived Bluetooth Data: " + value)
                updateChart(value)
                calibrationButton.isDisable  = calibrationButton.isDisable xor calibrationButton.isDisable
            }
            "SERIAL" -> {
                infoBox.appendText("\r\nReceived Serial Data: " + value)
                var msg = MessageParser.Format(value)
                deviceControl?.ClassicService!!.sendData("W12$msg\r\n")
            }
        }
    }

    fun updateButton(){
        calibrationButton.isDisable = !CommunicationControl.getControl().checkConnections()
    }
}