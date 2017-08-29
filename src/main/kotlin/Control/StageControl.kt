package Control

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.text.Text
import SerialPort.SerialPortDelegate
import data.Scenes
import javafx.fxml.LoadException
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import sun.plugin.javascript.navig.Anchor
import sun.plugin.javascript.navig.AnchorArray
import java.lang.reflect.InvocationTargetException

import java.net.URL

class StageControl : Controller<Any>() {

    companion object {

        private val APP_WIDTH = 500.0
        private val APP_HEIGHT = 500.0

        private val bScene : FXMLLoader = FXMLLoader()

        fun RequestBase(width: Double = APP_WIDTH, height: Double = APP_HEIGHT): Scene {
            return Scene(GetBase(), width, height)
        }

        private fun GetBase() : AnchorPane {
            bScene.location = this::class.java.getResource("/Scenes/OriginScene.fxml")
            val bPane = bScene.load<AnchorPane>()

            LoginControl.getLoginPane()

            bScene.getController<StageControl>().insertNavigation()
            bPane.stylesheets.add("/css/Theme.css")
            return bPane
        }

        fun swapCenter(fxmlLoader: FXMLLoader, sceneInfo : Scenes){
            if (fxmlLoader.location == null ) {
                fxmlLoader.location = sceneInfo.getSceneURL()
                bScene.getController<StageControl>().updateCenter(fxmlLoader.load<Pane>(), sceneInfo.style)
            }else{
                bScene.getController<StageControl>().updateCenter(fxmlLoader.getRoot<Pane>())
            }
        }

        fun connectionChanged(type: String){
            bScene.getController<StageControl>().updateFlag(type)
        }

        fun getControl() : StageControl {
            return bScene.getController<StageControl>()
        }
    }

    @FXML private var mWind : Pane = Pane()
    @FXML private var sideBar : Pane = Pane()
    @FXML private var headerText: Text = Text()
    @FXML private var titleBlock : AnchorPane = AnchorPane()
    @FXML private var toolFlag : Circle = Circle()
    @FXML private var comFlag : Circle = Circle()

    override fun initialize(){

        titleBlock.prefWidthProperty().bind(mWind.widthProperty())
        this.headerText.text = "Smart Calibration"
    }

    fun updateCenter(wkSpace: Pane, style: String? = null){
        mWind.children?.clear()
        wkSpace.prefWidthProperty().bind(mWind.widthProperty())
        wkSpace.prefHeightProperty().bind(mWind.heightProperty())
        if(style != null) wkSpace.stylesheets.add(style)
        mWind.children?.add(wkSpace)
    }

    fun updateFlag(type : String){
        if(type == "Tool"){
            if( toolFlag.fill == Color.ORANGERED ) toolFlag.fill =  Color.LIGHTGREEN else toolFlag.fill = Color.ORANGERED
        }
        if(type == "COM"){
            if( comFlag.fill == Color.ORANGERED ) comFlag.fill =  Color.LIGHTGREEN else comFlag.fill = Color.ORANGERED
        }
    }

    fun insertNavigation(){
        SidebarControl.getSideBar(sideBar)
    }
}
