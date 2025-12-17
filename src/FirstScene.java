import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import java.io.*;

public class FirstScene extends Application{
    
    @Override
    public void start(Stage firstStage) {
        Parent root;
        try {
            SceneManager.displayRoot("HomepageScene.fxml");
        }
        catch (Exception e){
            System.out.println("Exception in FirstScene.java");
            e.printStackTrace();
        }
        }
    }

