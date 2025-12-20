package Controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.*;
import javafx.scene.Node;
import javafx.application.Application;

import app.SceneManager;


//This class is builds a GUI that's used to get the start and end percent for parsing the database
//from the user

public class RecipeBoundariesController extends Application{
    //Start and end points of the recipe parser as percentiles of the dataset
    private static int recipeBoundariesStartNum;
    private static int recipeBoundariesEndNum;
    //set sends the data in the textfields, help procs an information alert
     @FXML
    private Button recipeBoundariesSetButton;
    @FXML
    private Button recipeBoundariesHelpButton;
    @FXML
    private TextField recipeBoundariesStartTF;
    @FXML
    private TextField recipeBoundariesEndTF;
    //Recipe Boundaries Scene Methods
    @FXML
    public void recipeBoundariesHelp() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setContentText("Because the recipe dataset is so large, it's recommended to only use" +
            " a part of it at a time in order to avoid crashing your computer. Enter a number between 0 and 100"
            + " for the starting percent, and another number for the percent you want to end at.");
        helpAlert.showAndWait();
    }
    @FXML
    public void setRecipeBoundaries(ActionEvent event) {
        String startString = recipeBoundariesStartTF.getText();
        String endString = recipeBoundariesEndTF.getText();
        int startNum = 0;
        int endNum = 0;
        
        try {
            startNum = Integer.parseInt(startString);
            endNum = Integer.parseInt(endString);
            if ((endNum < 0.0) || ((endNum - startNum) < 0.0) || 
            (endNum > 100.0) || (startNum < 0.0) || (startNum > 100)) {
                throw (new Exception());
            }
            recipeBoundariesStartNum = startNum;
            recipeBoundariesEndNum = endNum;
        }
        catch (Exception e) {
            recipeBoundariesHelp();
            return;
        }
        SceneManager.displayRoot("WelcomeScene.fxml");
    }
    @FXML
    public void setStartNum(int num) {
        recipeBoundariesStartNum = num;
    }
    @FXML
    public void setEndNum(int num) {
        recipeBoundariesEndNum = num;
    }
    @FXML
    public static int getStartNum() {
        return recipeBoundariesStartNum;
    }
    @FXML
    public static int getEndNum() {
        return recipeBoundariesEndNum;
    }
    @Override
    public void start(Stage firstStage) {
        Parent root;
        try {
            SceneManager.displayRoot("RecipeBoundariesScene.fxml");
        }
        catch (Exception e){
            System.out.println("Exception in FirstScene.java");
            e.printStackTrace();
        }
        }
    }
