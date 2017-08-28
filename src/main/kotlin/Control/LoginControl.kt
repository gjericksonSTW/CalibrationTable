package Control

import LDAP.LDAP
import data.Scenes
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class LoginControl : Controller<Any>() {


    companion object {

        private var loginScene : FXMLLoader = FXMLLoader()

        fun getLoginPane(){
            StageControl.swapCenter(loginScene, Scenes.LOGINSCENE)
        }
    }

    @FXML private var loginField : TextField = TextField()
    @FXML private var passField : PasswordField = PasswordField()
    @FXML private var submitButton : Button = Button()
    @FXML private var err : Label = Label()

    override fun initialize() {

        submitButton.isDefaultButton

        loginField.setOnKeyPressed { e -> handleKey(e) }
        passField.setOnKeyPressed { e -> handleKey(e) }

        submitButton.setOnAction { validateCredentials() }

        LDAP.initialize()
    }

    private fun clearForm(){
        passField.text = ""
        loginField.text = ""
    }

    private fun handleKey(e : KeyEvent){
        if(e.code == KeyCode.ENTER){
            validateCredentials()
        }
    }

    private fun validateCredentials() {
        val username = loginField.text as String
        val password = passField.text as String

        clearForm()

        if (LDAP.checkUsername(username)) {
            if (LDAP.checkPassword(password, username)) {
                CalibrationControl.openCalibration()
                SidebarControl.showIcons()
                err.text = ""
            }else{
                err.text = "Incorrect Password"
            }
        } else {
            err.text = "Username does not exist"
        }

        LDAP.clearDir()
    }
}