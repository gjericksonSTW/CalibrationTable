package data

import java.net.URL

enum class Scenes(private val fxml: String,
                  val style: String){

    BLUESCENE("/Scenes/BlueScene.fxml", "/css/Table.css"),
    LOGINSCENE("/Scenes/LoginScene.fxml", "/css/Theme.css"),
    ORIGINTHEME("Scenes/OriginScene.fxml", "/css/Theme.css"),
    SERIALSCENE("/Scenes/SerialScene.fxml", "/css/Table.css"),
    SIDEBAR("/Scenes/Sidebar.fxml", "/css/Theme.css"),
    CALSCENE("/Scenes/CalibrationScene.fxml", "/css/Theme.css");

    fun getSceneURL(): URL{
        return Scenes::class.java.getResource(this.fxml)
    }
}