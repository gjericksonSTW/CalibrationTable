package Stage

import Control.StageControl
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage


class OriginScene : Application() {

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            Application.launch(OriginScene::class.java, *args)
        }
    }

    override fun start(primaryStage: Stage) {

        try {
            primaryStage.title = "Smart Calibration"
            primaryStage.scene = StageControl.RequestBase()
            primaryStage.icons.add(Image("./media/STS_logo.png"))
            primaryStage.minWidth = 750.0
            primaryStage.minHeight = 500.0

            primaryStage.show()
        } catch(e: Exception){
            e.printStackTrace()
        }
    }

    override fun stop() {
        super.stop()
    }
}
