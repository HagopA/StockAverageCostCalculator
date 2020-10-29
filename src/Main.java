import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private final String ROOT_VIEW_FXML = "/resources/views/StockAverageCalculator.fxml";
    private final String STAGE_TITLE = "Stock Average Calculator";
    private final int SCENE_WIDTH = 700;
    private final int SCENE_HEIGHT = 400;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource(ROOT_VIEW_FXML));
        primaryStage.setTitle(STAGE_TITLE);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
