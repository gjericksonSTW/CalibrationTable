package Control

import Stage.OriginScene
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import java.util.*

class SidebarControl : Controller<Any>() {

    companion object {

        private val SideBar : FXMLLoader = FXMLLoader()

        fun getSideBar(parent: Pane){

            val font = OriginScene::class.java.getResourceAsStream("/css/fa/fontawesome-webfont.ttf")
            Font.loadFont(font, 10.0)

            SideBar.location = this::class.java.getResource("/Scenes/Sidebar.fxml")
            SideBar.resources = ResourceBundle.getBundle("css.fa.fontawesome")
            val sidePane = SidebarControl.SideBar.load<Pane>()
            sidePane.stylesheets.add("/css/Theme.css")

            val control = SideBar.getController<SidebarControl>()
            control.Directory.prefWidthProperty().bind(parent.widthProperty())
            control.Directory.prefHeightProperty().bind(parent.heightProperty())

            parent.children.add(SideBar.getRoot())
        }

        fun showIcons(){
            SideBar.getController<SidebarControl>().showAll()
        }
    }

    @FXML private var Directory : VBox = VBox()
    @FXML private var bt_ico : Button = Button()
    @FXML private var home_ico: Button = Button()
    @FXML private var expand_ico: Button = Button()
    @FXML private var help_ico: Button = Button()
    @FXML private var sign_out_ico : Button = Button()


    override fun initialize() {

        bt_ico.setOnAction { handleToolButton() }
        home_ico.setOnAction { handleHomeButton() }
        expand_ico.setOnAction { handleExpandButton() }
        help_ico.setOnAction { handleHelpButton() }
        sign_out_ico.setOnAction { handleSignOut() }

        bt_ico.managedProperty().bind(bt_ico.visibleProperty())
        home_ico.managedProperty().bind(home_ico.visibleProperty())
        sign_out_ico.managedProperty().bind(sign_out_ico.visibleProperty())
    }

    private fun handleToolButton(){
        CommunicationControl.OpenBluetooth()
    }

    private fun handleHomeButton(){
        CalibrationControl.openCalibration()
    }

    private fun handleExpandButton(){

    }

    private fun handleHelpButton(){

    }

    private fun handleSignOut(){

        LoginControl.getLoginPane()
        home_ico.isVisible = false
        bt_ico.isVisible = false
        sign_out_ico.isVisible = false
    }

    private fun showAll(){
        home_ico.isVisible = true
        bt_ico.isVisible = true
        sign_out_ico.isVisible = true

    }

}