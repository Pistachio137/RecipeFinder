import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import javafx.collections.ObservableList;


//This is a private singleton that I use to preserve scene data when switching scenes. 
//I used an AI to help me write this and it got very verbose.
public class SceneManager {
                        /**
                         * Returns an EventHandler that removes the button and the word from the list when triggered.
                         * @param word The string associated with the button.
                         * @param list The ObservableList to remove the word from.
                         * @param pane The Pane to remove the button from.
                         * @return EventHandler<ActionEvent>
                         */
                        public static EventHandler<ActionEvent> removeButtonAndWordHandler(String word, ObservableList<String> list, Pane pane) {
                            return new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    Button btn = (Button) event.getSource();
                                    pane.getChildren().remove(btn);
                                    list.remove(word);
                                }
                            };
                        }
                // Modify a specific child pane in the currently displayed scene, without reloading the scene
    /**
     * Update a specific child pane in the currently displayed scene, adding buttons for each item and assigning a custom action.
     * @param paneId The fx:id of the pane to update.
     * @param items The list of button labels to add.
     * @param handler The action to assign to each button (receives ActionEvent; button label is set as userData).
     */
    public static void updatePaneInCurrentScene(String paneId, ObservableList<String> items, EventHandler<ActionEvent> handler) {
        Parent root = ONLYSCENE.getRoot();
        if (root == null) return;
        // Try to get the controller if possible (works if root was loaded with FXMLLoader and controller is set as user data)
        Object controller = null;
        if (root.getProperties().containsKey("fx:controller")) {
            controller = root.getProperties().get("fx:controller");
        }
        // Fallback: try to find the pane by fx:id in the scene graph
        Pane targetPane = null;
        if (controller instanceof MainSceneController) {
            switch (paneId) {
                case "pantryFP2":
                    targetPane = ((MainSceneController) controller).getPantryFP2();
                    break;
                case "restrictionsFP2":
                    targetPane = ((MainSceneController) controller).getRestrictionsFP2();
                    break;
                case "searchContainingFP":
                    targetPane = ((MainSceneController) controller).getSearchContainingFP();
                    break;
                case "searchEqualsFP":
                    targetPane = ((MainSceneController) controller).getSearchEqualsFP();
                    break;
            }
        }
        if (targetPane == null) {
            // Try lookup by fx:id
            targetPane = (Pane) root.lookup("#" + paneId);
        }
        if (targetPane != null && items != null) {
            // Do not clear children; just add new buttons for items
            for (String item : items) {
                Button button = new Button(item);
                button.setUserData(item); // Store label for handler
                button.setOnAction(removeButtonAndWordHandler(item, items, targetPane));
                targetPane.getChildren().add(button);
            }
        }
    }
              // Display root and add buttons to a specific child pane by fx:id
            private static final SceneManager INSTANCE = new SceneManager();
            private static final Map<String, Parent> ROOTSTORER = new HashMap<String, Parent>();
            private static Pane temporaryPane = new Pane();
            private static final Scene ONLYSCENE = new Scene(temporaryPane);
            private static final Stage ONLYSTAGE = new Stage();
            private static String local = "/LocalData/obPantryItems.ser";
            
            private SceneManager() {
            }
            //Used to serialize scenes.
            public static Serialized saveScenes(ObservableList<String> ol1, ObservableList<String> ol2, ObservableList<String> ol3, ObservableList<String> ol4,
                 ArrayList<GenericRecipeBlock> blocks2, ArrayList<GenericRecipeBlock> blocks3){
              Serialized serialized = new Serialized();  
              ONLYSTAGE.setOnCloseRequest((e) -> {
                    serialized.setAll(ol1, ol2, ol3, ol4, blocks2, blocks3);
                    serialized.serializeAll();
                });
                return serialized;
            }
            public static SceneManager getSceneManager() {
                return INSTANCE;
            }
            public static void displayRoot(String name) {
                System.out.println(ROOTSTORER.size());
                String rootPath = "Scenes/" + name;
                Parent root = ROOTSTORER.get(rootPath);
                if (root == null) {
                    URL url = SceneManager.class.getResource(rootPath);
                    try {
                        FXMLLoader loader = new FXMLLoader(url);
                        root = loader.load();
                        Object controller = loader.getController();
                        if (controller != null) {
                            root.getProperties().put("fx:controller", controller);
                        }
                        ROOTSTORER.put(rootPath, root);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Failed to load FXML for " + name + ", root is null");
                        return; // Prevent setting null root
                    }
                }
                if (root == null) {
                    System.out.println("Root is still null for " + name + ", cannot set scene");
                    return;
                }
                ONLYSCENE.setRoot(root);
                ONLYSTAGE.setScene(ONLYSCENE);
                ONLYSTAGE.show();
            }

}

