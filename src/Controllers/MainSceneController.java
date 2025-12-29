package Controllers;
import app.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.awt.Desktop;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.*;
import javafx.scene.layout.*;
public class MainSceneController {

        //This is a very beefy class that I haven't had time to organize/split into smaller classes. 
        //Also, some of the comments might be wrong or outdated, so sorry about that. It's still in development
        //So I keep some stuff commented out instead of just deleting them. 
        private static MainSceneController ONLYCONTROLLER;
        public static MainSceneController getController() {
            return ONLYCONTROLLER;
        }
        public static MainSceneController getMainController() {
            if (ONLYCONTROLLER == null) {
                ONLYCONTROLLER = new MainSceneController();
            }
            return ONLYCONTROLLER;
        }
        private MainSceneController() {
        //instantiating the hashmap that is used to store the scenes and save their nodes
        sceneStorer = new HashMap<String, Parent>();
        serialized = new Serialized();
        //determining the boundaries of the recipe set
        final int NLGSIZE = 2231150;
        final int NLGINCREMENTS = NLGSIZE / 100;
        int startPercent = RecipeBoundariesController.getStartNum();
        int endPercent = RecipeBoundariesController.getEndNum();
        if (endPercent <= startPercent) {
            endPercent = startPercent + 1;
        }
        int startNum = startPercent * NLGINCREMENTS;
        int endNum = 0;  
        if (endPercent == 100) {
            endNum = NLGSIZE;
        }
        else {
            endNum = NLGINCREMENTS * endPercent;
        }
        //Getting data by calling methods from StaticGenericThings to parse the recipe set and sort it.
        //controlGenRecBlock is the parsed generic recipe block array
        System.out.println("Parsing recipes from " + startNum + " to " + endNum);
        controlGenRecBlock = StaticGenericThings.ingredientParserArray(startNum, endNum);
        System.out.println("controlGenRecBlock length: " + (controlGenRecBlock != null ? controlGenRecBlock.length : "null"));
        //genHashMap is the hashmap that increments the int value by one each time an ingredient key occurs
        genHashMap = StaticGenericThings.arrayToHashMap(controlGenRecBlock, controlGenRecBlock.length);
        System.out.println("genHashMap size: " + (genHashMap != null ? genHashMap.size() : "null"));
        //controlGenCountIngreds uses the SortedRecipes class to sort the ingredients from most to least common
        controlGenCountIngreds = StaticGenericThings.sortRecipes(genHashMap);
        //controlGenUnGreds is the string array of the ingredients in the same order they occur in controlGenCountIngreds
        controlGenUnGreds = StaticGenericThings.uniqueNames(controlGenCountIngreds);
        //obPantrySelections is the observable list version of controlGenUnGreds
        obPantrySelections = FXCollections.observableArrayList(controlGenUnGreds);
        //The number of parsed ingredients and recipes, which are used to determine the number of pages in the paginations
        numIngreds = controlGenUnGreds.length;
        numRecipes = controlGenRecBlock.length;
        //Setting the corresponding fields in static generic things
        StaticGenericThings.setGenericRecipeArray(controlGenRecBlock);
        StaticGenericThings.setGenericHashMap(genHashMap);
        StaticGenericThings.setCountedGenericIngredients(controlGenCountIngreds);
        StaticGenericThings.setUniqueIngredients(controlGenUnGreds);
        //instantiating various fields so that they can be set with the de-serialized data from the Serializable object that
        //is created and called by SceneManager.saveScenes
        obPantryItems = FXCollections.observableArrayList();
        obRestrictionsItems = FXCollections.observableArrayList();
        obSpecifyItems = FXCollections.observableArrayList();
        obContainingItems = FXCollections.observableArrayList();
        favoritedRecipesBlock = new ArrayList<GenericRecipeBlock>();
        savedRecipesBlock = new ArrayList<GenericRecipeBlock>();
        currentRecipeBlock = new ArrayList<GenericRecipeBlock>();
        restrictionsHashMap = new HashMap<String, String[]>();
        restrictionsParentMap = new HashMap<String, RestrictionsObject>();
        restrictionsSaveSet = new HashSet<String>();
         
        //setting these fields with the de-serialized data as previously explained
         serialized.getAll(obPantryItems, obRestrictionsItems, obContainingItems, obSpecifyItems, 
             savedRecipesBlock, favoritedRecipesBlock, restrictionsHashMap, restrictionsParentMap, restrictionsSaveSet);
             
        //the userRecipesMap follows different serialization logic because it serializes everytime a recipe is submitted, not 
        //everytime the stage is closed
        userRecipesMap = Serialized.deSerializer(userRecipesMapPath, 5, new HashMap<String, String>());
        
        //Instructing the SceneManager stage call the Serialize() method on these fields when the stage is closed by the user
        SceneManager.saveScenes(obPantryItems, obRestrictionsItems, obContainingItems, obSpecifyItems, 
             savedRecipesBlock, favoritedRecipesBlock, restrictionsHashMap, restrictionsParentMap, restrictionsSaveSet);
        
             //Instantiating the filtered lists for the combo boxes
        filteredEqualsItems = new FilteredList<>(obPantrySelections, s -> true);
        filteredContainingItems = new FilteredList<>(obPantrySelections, s -> true);
        filteredRestrictionItems = new FilteredList<>(obPantrySelections, s -> true);
        filteredPantryItems = new FilteredList<>(obPantrySelections, s -> true);
         System.out.println(restrictionsSaveSet);
         System.out.println(restrictionsHashMap.keySet());
         System.out.println(restrictionsParentMap.keySet());
         //System.out.println(controlGenUnGreds);
        }
        
        // FXML initialize method - called after FXML fields are injected
        @FXML
        public void initialize() {
        //Instantiating the page factories - only for controls present in the current scene
        restrictionsParentCheck(restrictionsParentMap);
        if (savedPG1 != null) {
            savedPG1.setPageCount(0/recipesIPP);
            savedPG1.setPageFactory((Integer pageIndex) -> createSavedPage(pageIndex, recipesIPP, 0, savedVBox1, 
                savedRecipesBlock, savedPG1, favoritedRecipesBlock, savedPG2));
        }
        if (savedPG2 != null) {
            savedPG2.setPageCount(0/recipesIPP);
            savedPG2.setPageFactory((Integer pageIndex) -> createSavedPage(pageIndex, recipesIPP, 0, savedVBox2, 
                favoritedRecipesBlock, savedPG2, savedRecipesBlock, savedPG1));
        }
        if (searchPG1 != null) {
            searchPG1.setPageCount(numIngreds/recipesIPP);
        }
        if (restrictionsPG1 != null) {
            restrictionsPG1.setPageCount(numIngreds/pantryIPP);
            restrictionsPG1.setPageFactory((Integer pageIndex) -> createPage(pageIndex, restrictionsFP2, obRestrictionsItems, restrictionsIPP));
        }
        if (pantryPG1 != null) {
            pantryPG1.setPageCount(numIngreds/pantryIPP);
            pantryPG1.setPageFactory((Integer pageIndex) -> createPage(pageIndex, pantryFP2, obPantryItems, pantryIPP));
        }
         
        //setting up the combo boxes - only for controls present in the current scene
        if (pantryCB1 != null) {
            pantryCB1.setItems(obPantrySelections);
            filterComboBox(pantryCB1, filteredPantryItems);
        }
        if (restrictionsChoiceCB1 != null) {
            restrictionsChoiceCB1.getItems().addAll(restrictionsSaveSet);
        }
        if (restrictionsCB1 != null) {
            restrictionsCB1.setItems(obPantrySelections);
            filterComboBox(restrictionsCB1, filteredRestrictionItems);
        }
        if (searchEqualsCB != null) {
            searchEqualsCB.setItems(obPantrySelections);
            filterComboBox(searchEqualsCB, filteredEqualsItems);
        }
        if (searchContainingCB != null) {
            searchContainingCB.setItems(obPantrySelections);
            filterComboBox(searchContainingCB, filteredContainingItems);
        }
        }
        
        static HashMap<String, Integer> genHashMap;
        Serialized serialized;

        static GenericRecipeBlock[] controlGenRecBlock;
        static SortedRecipes[] controlGenCountIngreds;
        static String[] controlGenUnGreds;
        static ArrayList<String> ingredsAL;
        static ObservableList<String>obPantrySelections;
        //Fields obtained from parsing.
        //static initializer
        //#region
        //#endregion
        //#region
        //Serialized/deserialized fields block + some of their paths.
        public static Map<String, Parent> sceneStorer;
        ObservableList<String>obPantryItems;
        ObservableList<String>obSpecifyItems;
        ObservableList<String>obContainingItems;
        ObservableList<String> obRestrictionsItems;
        Map<String, String> userRecipesMap;
        //SAVED RECIPES LIBRARY ARRAYLISTS. Stub.
        ArrayList<GenericRecipeBlock> currentRecipeBlock;
        ArrayList<GenericRecipeBlock> favoritedRecipesBlock;
        ArrayList<GenericRecipeBlock> savedRecipesBlock;
        //Paths for the arraylists and the user recipes map.
        String currentRecipeBlockPath = "savedData/currentRecipeBlock.ser";
        String favoritedRecipesBlockPath = "savedData/favoritedRecipesBlock.ser";
        String savedRecipesBlockPath = "savedData/savedRecipesBlock.ser";
        String userRecipesMapPath = "savedData/userRecipesMap.ser";
        FilteredList<String> filteredEqualsItems;
        FilteredList<String> filteredContainingItems;
        FilteredList<String> filteredRestrictionItems;
        FilteredList<String> filteredPantryItems;
        HashMap<String, String[]> restrictionsHashMap;
        HashMap<String, RestrictionsObject> restrictionsParentMap;
        //#endregion
        //string that's used to hold the value of the queried phrase in restrictionsCB1. When the user clicks "add all"
        //on the restrictionsQueueFP, a button is assigned with this phrase and added to the restrictionsHashMapFP
        //which represents all of the phrases that were in the queue pane as buttons
        private static String restrictionsValue = "none";
        //ints used to set the pagination controls
        int numRecipes;
        int numIngreds;
        int pantryIPP = 150;
        int restrictionsIPP = 150;
        int recipesIPP = 50;
        int paginationTracker = 0;
        int recipeNum = 0;
        int pantryCounter = 0;
        //FXML controls
        //#region
        //#region
        //Controls for saved recipes scene
        @FXML
        private Button savedRecipesBackButton;
        @FXML
        private VBox savedVBox1;
        @FXML
        private VBox savedVBox2;
        @FXML
        private VBox savedVBox3;
        @FXML
        private Pagination savedPG1;
        @FXML
        private Pagination savedPG2;
        @FXML
        private Label savedLabel1;
        @FXML
        private Label savedLabel2;
        @FXML
        private TextArea savedTA1;
        @FXML
        private TextArea savedTA2;
        @FXML
        private BorderPane savedBP1;
        @FXML
        private Button savedBackButton;
        @FXML
        private Button savedVBoxExitButton;
        @FXML
        private Hyperlink savedURLLink;
        
        //Controls for Search Scene
        private Boolean pantryYesBoolean = false;
        @FXML
        private ComboBox<String> searchRestrictionsCB;
        @FXML
        private FlowPane searchRestrictionsFP;
        @FXML
        private Button searchHelpButton;
        @FXML
        private Button searchEqualsButton;
        @FXML
        private Button searchContainingButton;
        @FXML
        private ComboBox<String> searchContainingCB;
        @FXML
        private ComboBox<String> searchEqualsCB;
        @FXML
        private CheckBox searchPantryCB;
        @FXML
        private TextField searchRecipeNamesTF;
        @FXML
        private Button superSearchButton;
        @FXML
        private Pagination searchPG1;
        @FXML
        private FlowPane searchEqualsFP;
        @FXML
        private FlowPane searchContainingFP;
        @FXML
        private VBox searchRecipeVBox;
        @FXML
        private Hyperlink urlLink;
        @FXML
        private Label searchNumberLabel;
        @FXML
        private Label searchNameLabel;
        @FXML
        private TextArea searchQuantitiesTA;
        @FXML
        private TextArea searchInstructionsTA;
        @FXML
        private Button recipeVBoxExitButton;
        @FXML
        private Button recipeVBoxSaveButton;
        @FXML
        private Button recipeVBoxFavoriteButton;
        @FXML
        private BorderPane recipeVBoxBP;
        @FXML
        private TextField searchRecipeNameTF;

        //#endregion
        //#region
        //Welcome Buttons
        @FXML
        private Button testButton;
        @FXML
        private Button welcomeLoginButton;
        @FXML
        private Button welcomeCreateAccountButton;
        @FXML
        private Button welcomeContinueOfflineButton;
        @FXML
        private Button welcomeAboutButton;
        //Login Buttons and other nodes
        @FXML
        private Button loginHomepageButton;
        @FXML
        private Button loginWelcomeButton;
        @FXML
        private Label loginUsernameLabel;
        @FXML
        private Label loginPasswordLabel;
        @FXML
        private TextField loginPasswordTF;
        @FXML
        private TextField loginUsernameTF;
        //CreateAccount Buttons and other nodes
        @FXML
        private Button createAccountHomepageButton;
        @FXML
        private Button createAccountWelcomeButton;
        @FXML
        private Label createAccountUsernameLabel;
        @FXML
        private Label createAccountPasswordLabel;
        @FXML
        private TextField createAccountPasswordTF;
        @FXML
        private TextField createAccountUsernameTF;
        //Homepage Buttons and other things
        @FXML
        private Button homepageCreateAccountButton;
        @FXML
        private Button homepageLoginButton;
        @FXML
        private Button homepageAboutButton;
        @FXML
        private Button homepageLogoutButton;
        @FXML
        private Button recipeSearchButton;
        @FXML
        private Button editSettingsButton;
        @FXML
        private Button editPreferencesButton;
        @FXML
        private Button customizePantryButton;
        @FXML
        private Button savedRecipesButton;
        @FXML
        private Button submitRecipeButton;
        @FXML
        private ImageView searchIMG;
        @FXML
        private ImageView savedIMG;
        @FXML
        private ImageView uploadIMG;
        @FXML
        private ImageView pantryIMG;
        @FXML
        private ImageView checklistIMG;
        @FXML
        private ImageView settingsIMG;

        @FXML
        private Button searchIMGButton;
        @FXML
        private Button savedIMGButton;
        @FXML
        private Button uploadIMGButton;
        @FXML
        private Button pantryIMGButton;
        @FXML
        private Button checklistIMGButton;
        @FXML
        private Button settingsIMGButton;
        //pantry scene nodes
        //#endregion

        //edit settings controls
        //#region
        @FXML
        private Button settingsBackButton;
        //#endregion
        //Controls for SubmitRecipeScene, AboutScene, etc.
        //#region
        //SubmitRecipeScene controls
        @FXML
        private TextField submitRecipeNameTF;
        @FXML
        private TextArea recipeContentTA;
        @FXML
        private Button submitRecipeBackButton;
        @FXML
        private Button submitRecipeExampleButton;
        @FXML
        private Button submitRecipeSubmitButton;
        //AboutScene controls
        @FXML
        private Button aboutSceneBackButton;
        @FXML
        private TextArea aboutText;
        @FXML
        private Button getAboutText;
        //#endregion
        //Controls for Dietary Preferences
        //#region
        private static String restrictionsChoiceString = null;
        private Boolean openRestrictionSheet = false;
        @FXML
        private BorderPane restrictionsBP1;
        @FXML
        private TextField restrictionsSaveAsTF;
        @FXML
        private FlowPane restrictionsHashMapFP;
        @FXML
        private Button restrictionHelpButton;
        @FXML
        private Button restrictionBackButton;
        @FXML
        private Button addRestrictionButton;
        @FXML
        private Button restrictionsAddAllButton;
        @FXML
        private Button restrictionsClearAllButton;
        @FXML
        private Button restrictionsQueueAllButton;
        @FXML
        private ComboBox<String> restrictionsCB1;
        @FXML
        private Pagination restrictionsPG1;
        @FXML
        private FlowPane restrictionsFP1;
        @FXML
        private FlowPane restrictionsFP2;
        @FXML
        private FlowPane restrictionsQueueFP;
        //Controls for DietaryRestrictionsChoiceScene
        private HashSet<String> restrictionsSaveSet;
        @FXML
        private ComboBox<String> restrictionsChoiceCB1;
        @FXML
        private Button restrictionsChoiceNewButton;
        @FXML
        private Button restrictionsChoiceContinueButton;
        @FXML
        private Button restrictionsChoiceHelpButton;
        @FXML
        private Button restrictionsChoiceBackButton;
        //#endregion
        
        //PantryScene controls
        //#region
        @FXML
        private ComboBox<String> pantryCB1;
        @FXML
        private Button pantryHelpButton;
        @FXML
        private Pagination pantryPG1;
        @FXML
        private SplitPane pantrySplit2;
        @FXML
        private Button pantryBackButton;
        @FXML
        private FlowPane pantryFP1;
        @FXML
        private FlowPane pantryFP2;
        @FXML
        private Button testButton2;
        @FXML
        private FlowPane testFlowPane;
        @FXML
        private HBox pantryHBox2;
        //#endregion
        //#endregion
        //#endregion

    public ArrayList<String> obToArrayList(ObservableList<String> list) {
        ArrayList<String> arrayList = new ArrayList<String>();
        return arrayList;
    }
    
    public FlowPane getPantryFP2() {
        return pantryFP2;
    }
    public FlowPane getRestrictionsFP2() {
        return restrictionsFP2;
    }
    public FlowPane getSearchEqualsFP() {
        return searchEqualsFP;
    }
    public FlowPane getSearchContainingFP() {
        return searchContainingFP;
    }
    //Search Scene Methods
    @FXML
    void populateFXFromCB(ActionEvent event, Pane pane) {
        String cbText = (String)((ComboBox)event.getSource()).getValue();
        Button button = new Button(cbText);
        pane.getChildren().add(button);
        button.setOnAction((e) -> {
            pane.getChildren().remove(button);
        });
    }
    @FXML
    void populateSearchRestrictionsFP(ActionEvent event) {
        populateFXFromCB(event, searchRestrictionsFP);
    }
    @FXML
    void findRecipes() {
        //remove old search results
        currentRecipeBlock.clear();
        System.out.println("Entering findRecipes()");
        //used to count the number of caught exceptions
        int errorsNum = 0;
        //search via name if the user enters something in that textfield
        String nameSearch = searchRecipeNameTF.getText().toLowerCase();
        boolean searchByName = false;
        if ((nameSearch != null) && (!nameSearch.trim().equals(""))) {
            searchByName = true;
            }
        //add all restricted items to this hashset, given by the save names present on the buttons populating searchRestrictionsFP
        HashSet<String> restrictionsHashSet = new HashSet<String>();
        for (int i = 0; i < searchRestrictionsFP.getChildren().size(); ++i) {
                String saveKey = (String)((Button)searchRestrictionsFP.getChildren().get(i)).getText();
                RestrictionsObject tempObject = restrictionsParentMap.get(saveKey);
                HashMap <String, String[]> tempHash = (HashMap <String, String[]>)tempObject.getHashMap().clone();
                for (String[] stringArray : tempHash.values()) {
                    for (String string : stringArray) {
                        restrictionsHashSet.add(string);
                    }
                }
                for (String string : tempObject.getArrayList()) {
                    restrictionsHashSet.add(string);
                }
            }
        //the loop is named so that the program can continue onto the next iteration using a break statement from an inner loop
        outerLoop:
        for (int i = 0; i < controlGenRecBlock.length; ++i) {
            System.out.println("loop " + i);
            try {
            int specifyCounter = 0;
            System.out.println("Test z");
            int containingCounter = 0;
            System.out.println("Test a");
            String recipeName = controlGenRecBlock[i].getRecipeName().toLowerCase();
            System.out.println("Test b");
            String[] ingredients = controlGenRecBlock[i].getIngredients();
            System.out.println("Test c");
            HashSet<String> ingredientsSet = new HashSet<String>(Arrays.asList(ingredients));
            System.out.println("Test d");
            if (searchByName) {
                if (!recipeName.contains(nameSearch)) {
                    continue outerLoop;
                }
            }
            System.out.println("Test 1");
            //search stub
            if (pantryYesBoolean) {
                HashSet<String> pantrySet = new HashSet<String>(obPantryItems);
                for (String item : ingredients) {
                    if (!pantrySet.contains(item)) {
                        continue outerLoop;
                    }
                }
            }
            HashSet<String> specifySet = new HashSet<String>(obSpecifyItems);
            for (String item : specifySet) {
                if (!ingredientsSet.contains(item)) {
                    continue outerLoop;
                }
            }
            System.out.println("Test 2");
            for (String query : obContainingItems) {
                for (String item : ingredients) {
                    if (item.contains(query)) {
                        containingCounter ++;
                        break;
                    }
                }
            }
            System.out.println("Test 3");
            if (containingCounter < obContainingItems.size()) {
                containingCounter = 0;
                continue outerLoop;
            }
            System.out.println("Test 4");
            for (String ingredient : restrictionsHashSet) {
                if (ingredientsSet.contains(ingredient)) {
                    continue outerLoop;
                }
            }
            currentRecipeBlock.add(controlGenRecBlock[i]);
            System.out.println(currentRecipeBlock.size());
            
        }
        catch (Exception e) {
            System.out.println("Error occurred while converting controlGenRecBlock to currentRecipeBlock at recipe block number " + i);
            errorsNum++;
            if (errorsNum > 49) {
                System.out.println("Exceptions exceeded. Stopping the process.");
                e.printStackTrace();
                return;

            }
        }
        }
        Serialized.Serializer(currentRecipeBlock, currentRecipeBlockPath);
        searchPG1.setPageCount(currentRecipeBlock.size() / recipesIPP);
        System.out.println("Moving to page factory, recipe block size is " + currentRecipeBlock.size());
        searchPG1.setPageFactory((Integer pageIndex) -> createRecipePage(pageIndex, recipesIPP));
    }
    //#region
    
    @FXML
    void searchEquals() {
        addToSearch(searchEqualsCB, filteredEqualsItems, obSpecifyItems, searchEqualsFP);
    }
    @FXML
    void searchContaining() {
        addToSearch(searchContainingCB, filteredContainingItems, obContainingItems, searchContainingFP);
    }
    @FXML
    void pantryYes() {
        pantryYesBoolean = searchPantryCB.isSelected();
    }
    //#endregion
    //Pantry Scene Methods
    //#region
    //Creating page child container

    @FXML
    public FlowPane createPage(int pageIndex, Pane pane2, ObservableList<String> observableList, int IPP) {
        FlowPane flowPane = new FlowPane();
        int page = pageIndex * IPP;
        for (int i = page; i < page + IPP; i++) {
            String ingredientName = controlGenUnGreds[i];
            Button button = new Button(ingredientName);
            button.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                    addButtonToStock(ingredientName, pane2, observableList);
            } 
            });
            flowPane.getChildren().add(button);
        }
        return flowPane;
    };
    @FXML
    public VBox createSavedPage(int pageIndex1, int IPP, int savedNum, VBox vbox, ArrayList<GenericRecipeBlock> recipesBlock, Pagination pg1, ArrayList<GenericRecipeBlock> otherRecipesBlock, Pagination pg2) {
        System.out.println("recipesBlock size = " + recipesBlock.size());
        VBox pageVBox = new VBox();
        int page = pageIndex1 * IPP;
        int exceptionCounter = 0;
        for (int i = page; i < page + IPP; i++) {
            try {
            Button button = new Button("Show recipe.");
            Button button2 = new Button();
            Button button3 = new Button("Discard recipe.");
            if (recipesBlock.equals(savedRecipesBlock)) {
                button3.setOnAction((e) -> {
                    discardSavedRecipe(e);
                    savedPG1.setPageCount(savedRecipesBlock.size() / recipesIPP);
                    savedPG1.setPageFactory((Integer pageIndex2) -> createSavedPage(pageIndex2, recipesIPP, 0, savedVBox1, 
            savedRecipesBlock, savedPG1, favoritedRecipesBlock, savedPG2));

                });
                button2.setText("Add to favorites.");
                button2.setOnAction((e) -> {
                    favoriteFromLibrary(e);
                    savedPG2.setPageCount(favoritedRecipesBlock.size() / recipesIPP);
                    savedPG2.setPageFactory((Integer pageIndex2) -> createSavedPage(pageIndex2, recipesIPP, 0, savedVBox2, 
            favoritedRecipesBlock, savedPG2, savedRecipesBlock, savedPG1));
                });
            }
            else if (recipesBlock.equals(favoritedRecipesBlock)) {
                button3.setOnAction((e) -> {
                    discardFavoritedRecipe(e);
                    savedPG2.setPageCount(favoritedRecipesBlock.size() / recipesIPP);
                    savedPG2.setPageFactory((Integer pageIndex2) -> createSavedPage(pageIndex2, recipesIPP, 0, savedVBox2, 
            favoritedRecipesBlock, savedPG2, savedRecipesBlock, savedPG1));
                });
                button2.setText("Add to saved.");
                button2.setOnAction((e) -> {
                    saveFromLibrary(e);
                    savedPG1.setPageCount(savedRecipesBlock.size() / recipesIPP);
                    savedPG1.setPageFactory((Integer pageIndex2) -> createSavedPage(pageIndex2, recipesIPP, 0, savedVBox1, 
            savedRecipesBlock, savedPG1, favoritedRecipesBlock, savedPG2));
                });
            }
            String recipeName = recipesBlock.get(i).getRecipeName();
            savedNum ++;
            Label nameLabel1 = new Label(recipeName);
            Label numberLabel = new Label("Recipe no. " + savedNum);
            VBox vbox2 = new VBox(nameLabel1, numberLabel, button, button2, button3);
            button.setOnAction((e) -> {
            int nodeNum = Integer.parseInt(((Label)((VBox)button.getParent()).getChildren().get(1)).getText().replace("Recipe no. ", ""));
            nodeNum --;
            String recipeName2 = recipesBlock.get(nodeNum).getRecipeName();
            String recipeURL = recipesBlock.get(nodeNum).getURL();
            String[] instructions = recipesBlock.get(nodeNum).getInstructions();
            String[] quantities = recipesBlock.get(nodeNum).getIngredientQuantities();
            String quantitiesString = "";
            String instructionsString = "";
            for (int j = 0; j < quantities.length; ++j) {
                quantitiesString += quantities[j] + "\n";
            }
            for (int j = 0; j < instructions.length; ++j) {
                instructionsString += instructions[j] + "\n";
            }
            savedTA1.setText(quantitiesString);
            savedTA2.setText(instructionsString);
            savedLabel1.setText(recipeName);
            savedURLLink.setText(recipeURL);
            savedURLLink.setOnAction((d) -> {
                try {
                Desktop.getDesktop().browse(new URI(recipeURL));
                }
                catch (Exception c) {
                    badLinkAlert();
                }
            });
            showRecipeVBoxBP(savedBP1);
            });
            pageVBox.getChildren().add(vbox2); // Add to the new pageVBox
        }
            catch (NumberFormatException n) {
                System.out.println("Couldn't parse page number at recipe " + i);
                ++exceptionCounter;
            }
            catch (Exception e) {
                System.out.println("Failed getting recipe fields at recipe " + i);
                ++exceptionCounter;
            }
            if (exceptionCounter > 14) {
                break;
            }

        }
        System.out.println("Returning pageVBox");
        return pageVBox;
    }
    @FXML
    public VBox createRecipePage(int pageIndex, int IPP) {
        recipeNum = 0;
        VBox vbox = new VBox();
        int page = pageIndex * IPP;
        int exceptionCounter = 0;
        for (int i = page; i < page + IPP; i++) {
            System.out.println("Pagination processing recipe " + paginationTracker);
            paginationTracker += 1;
            try {
            Button button = new Button("Show recipe.");
            String recipeName = currentRecipeBlock.get(i).getRecipeName();
            recipeNum ++;
            Label nameLabel1 = new Label(recipeName);
            Label numberLabel = new Label("Recipe no. " + recipeNum);
            VBox vbox2 = new VBox(nameLabel1, numberLabel, button);
            button.setOnAction((e) -> {
            int nodeNum = Integer.parseInt(((Label)((VBox)button.getParent()).getChildren().get(1)).getText().replace("Recipe no. ", ""));
            nodeNum --;
            String recipeName2 = currentRecipeBlock.get(nodeNum).getRecipeName();
            String recipeURL = currentRecipeBlock.get(nodeNum).getURL();
            String[] instructions = currentRecipeBlock.get(nodeNum).getInstructions();
            String[] quantities = currentRecipeBlock.get(nodeNum).getIngredientQuantities();
            String quantitiesString = "";
            String instructionsString = "";
            for (int j = 0; j < quantities.length; ++j) {
                quantitiesString += quantities[j] + "\n";
            }
            for (int j = 0; j < instructions.length; ++j) {
                instructionsString += instructions[j] + "\n";
            }
            searchQuantitiesTA.setText(quantitiesString);
            searchInstructionsTA.setText(instructionsString);
            searchNameLabel.setText(recipeName);
            urlLink.setText(recipeURL);
            urlLink.setOnAction((d) -> {
                try {
                Desktop.getDesktop().browse(new URI(recipeURL));
                }
                catch (Exception c) {
                    badLinkAlert();
                }
            });
            showSearchVBox();
            });
            vbox.getChildren().add(vbox2);
        }
            catch (NumberFormatException n) {
                System.out.println("Couldn't parse page number at recipe " + i);
                ++exceptionCounter;
            }
            catch (Exception e) {
                System.out.println("Failed getting recipe fields from currentRecipeBlock at recipe " + i);
                ++exceptionCounter;
            }
            if (exceptionCounter > 14) {
                break;
            }

        }
        System.out.println("Returning vbox");
        return vbox;
    }
    
    @FXML
    public void addButtonToStock(String buttonName, Pane pane, List<String> itemList) {
        if (!itemList.contains(buttonName)) {
            itemList.add(buttonName);
            Button button = new Button(buttonName);
            pane.getChildren().add(button);
            // Use SceneManager's handler for consistent removal
            button.setOnAction(SceneManager.removeButtonAndWordHandler(buttonName, (ObservableList<String>) itemList, pane));
        }
    }
    //general building block for adding buttons to panes
    @FXML
    void addButtonToStockCB(ComboBox<String> comboBox, Pane pane, List<String> itemList, ObservableList<String>selectionsList) {
        String buttonName = comboBox.getValue();
        if (selectionsList.contains(buttonName) && (!itemList.contains(buttonName))) {
            addButtonToStock(buttonName, pane, itemList);
        }
    }
    //method for adding button to pantry
    @FXML
    void addToPantryCB() {
        addButtonToStockCB(pantryCB1, pantryFP2, obPantryItems, obPantrySelections);
    }
    //#endregion
    //Dietary Restrictions Scene Methods
    //#region
    //To-do - test object assignment
    //object assignment DOES work for javafx controls
    public HashMap<String, RestrictionsObject> getParentMap() {
        return restrictionsParentMap;
    }
    @FXML
    static void restrictionsParentCheck(HashMap<String, RestrictionsObject> objectHashMap) {
        Set<String> keySet = objectHashMap.keySet();
        for (String key : keySet) {
            restrictionsArrayListCheck(objectHashMap.get(key).getArrayList());
            restrictionsHashMapCheck(objectHashMap.get(key).getHashMap());
        }
    }
    @FXML
    static void restrictionsArrayListCheck(List<String> list) {
        if (list.isEmpty()) {
            System.out.println("list<String> is empty.");
        }
        else {
        System.out.println(list);
        }
    }
    @FXML
    static void restrictionsHashMapCheck(HashMap<String, String[]> hashMap) {
        if (hashMap.isEmpty()) System.out.println("HashMap<String, String[]> is empty.");
        else {
        System.out.println(hashMap.keySet());
        for (String[] stringArray : hashMap.values()) {
            for(String string: stringArray) {
                System.out.print(string);
            }
            System.out.println();
        }
    }
    }
    //sends the user back to the dietary restrictions choice scene
    @FXML
    void restrictionsSelection() {
        SceneManager.displayRoot("DietaryRestrictionsChoiceScene.fxml");
        restrictionsChoiceCB1.getItems().clear();
        restrictionsChoiceCB1.getItems().addAll(restrictionsSaveSet);
    }
    @FXML
    //Adds the current restrictions hashmap and observable list to a parent hashmap, 
    //which will get serialized using other methods when the gui is closed.
    void restrictionsSave() {
        String saveName = restrictionsSaveAsTF.getText();
        ArrayList<String> obRestAL = new ArrayList<String>(obRestrictionsItems);
        if (saveName != null && saveName.trim() != "") {
            RestrictionsObject tempObject = new RestrictionsObject((HashMap<String, String[]>)restrictionsHashMap.clone(), obRestAL);
            if (restrictionsParentMap.putIfAbsent(saveName, tempObject) != null) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Error! Save name already in use.");
                error.showAndWait();
            }
            else {
                System.out.println("Dietary restrictions sheet saved successfully!");
                restrictionsSaveSet.add(saveName);
                restrictionsChoiceCB1.setValue(null);
                restrictionsChoiceCB1.getItems().clear();
                restrictionsChoiceCB1.getItems().addAll(restrictionsSaveSet);
            }

        }
        else {
            Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Please enter a valid save name.");
                error.showAndWait();
            
        }
        
    }
    //Adds every value in the restrictionsQueueFP to the restrictionsHashMapFP, with one button representing a group of ingredients.
    @FXML
    void restrictionsAddAll() {
        int size = restrictionsQueueFP.getChildren().size();
        String[] array;
        if (size > 0) {
            array = new String[size];
        for (int i = 0; i < size; ++i) {
            String string = ((Button)restrictionsQueueFP.getChildren().get(i)).getText();
            array[i] = string;
        }
        //keeps the hashmap values from being overwritten
        while (true) {
            if (restrictionsHashMap.putIfAbsent(restrictionsValue, array) == null) {
                break;
            }
            restrictionsValue += ".";
        }
        Button button = new Button(restrictionsValue);
        restrictionsHashMapFP.getChildren().add(button);
        button.setOnAction((e) ->{
            restrictionsHashMap.remove(button.getText());
            restrictionsHashMapFP.getChildren().remove(button);
            });
    }
        restrictionsQueueFP.getChildren().clear();
    }
    //Removes all the buttons from the restrictionsQueueFP
    @FXML
    void restrictionsClearAll() {
        restrictionsQueueFP.getChildren().clear();
    }
    //Moves all the strings in the filteredRestrictionsList to the restrictionsQueuePane as buttons,
    //provided the conditions are met. Also, gives the buttons an actionevent to remove them when they're
    //clicked.
    @FXML
    void restrictionsQueueAll() {
        String string = restrictionsCB1.getValue();
        if (restrictionsCB1 != null && string.length() >= 2) {
            restrictionsValue = string + "..";
            for (int i = 0; i < filteredRestrictionItems.size(); ++i) {
                String ingredient = filteredRestrictionItems.get(i);
                if (obRestrictionsItems.contains(ingredient)) continue;
                Button button = new Button(ingredient);
                restrictionsQueueFP.getChildren().add(button);
                button.setOnAction((e) -> {
                    restrictionsQueueFP.getChildren().remove(button);
                });
            }
        }
    }
    //Instructions on how to use the Dietary Restrictions Scene
    @FXML
    void restrictionsHelp() {
        Alert resHelpAlert = new Alert(Alert.AlertType.INFORMATION);
        resHelpAlert.setContentText("This is the page where you can add dietary restrictions. When " +
            "you search for recipes with the search page, you can avoid every ingredient you add here by " +
            "ticking the checkbox. You can click an individual ingredient to add it, or you can " + 
            "use the dropdown menu. " + "If you use the dropdown menu, you'll have the option to add every" +
            " ingredient that includes the phrase you current have typed in. For example, typing in \"peanuts\""+
            " and then clicking \"move all\" will transfer every ingredient that includes the phrase \"peanuts\""+
            "to the \"holding area\". There, you can click an ingredient to clear it from the holding area " +
            "And you can click the \"Add All\" button to add every ingredient currently in the holding area to " +
            "the confirmed restrictions area. Or, if you misclicked, you can click \"Clear All\" to clear. "+
            "When you're done, you can save this page by entering a save name in the text field and then clicking save.");
        resHelpAlert.showAndWait();
    }
    @FXML
    void addToRestrictionsCB() {
        addButtonToStockCB(restrictionsCB1, restrictionsFP2, obRestrictionsItems, obPantrySelections);
    }
    //#endregion
    //DietaryRestrictionsChoiceScene methods
    //#region
    //Lets the user edit a saved DietaryRestrictionsScene page
    @FXML
    void restrictionsChoice() {
        restrictionsChoiceString = restrictionsChoiceCB1.getValue();
        //restrictionsChoiceCB1.setValue(null);
        if (restrictionsChoiceString != null) {
            //openRestrictionSheet = true;
            SceneManager.displayRoot("DietaryRestrictionsScene.fxml");
            ObservableList<String> tempList = FXCollections.observableArrayList();
            restrictionsFP2.getChildren().clear();
            restrictionsQueueFP.getChildren().clear();
            restrictionsHashMapFP.getChildren().clear();
            restrictionsCB1.setValue(null);
            RestrictionsObject tempObject = restrictionsParentMap.get(restrictionsChoiceString);
            obRestrictionsItems.setAll(tempObject.getArrayList());
            for (int i = 0; i < obRestrictionsItems.size(); ++i) {
                String name = obRestrictionsItems.get(i);
                Button button = new Button(name);
                restrictionsFP2.getChildren().add(button);
                button.setOnAction((e) -> {
                    obRestrictionsItems.remove(button.getText());
                    restrictionsFP2.getChildren().remove(button);
                });
            }
            restrictionsHashMap = (HashMap<String, String[]>)tempObject.getHashMap().clone();
            Set<String> restrictionsSet = restrictionsHashMap.keySet();
            for (String label : restrictionsSet) {
                Button button = new Button(label);
                restrictionsHashMapFP.getChildren().add(button);
                button.setOnAction((e) ->{
                restrictionsHashMap.remove(button.getText());
                restrictionsHashMapFP.getChildren().remove(button);
            });
            }
            //openRestrictionSheet = false;
        }
    }
    @FXML
    void clearRestrictionsData() {
        obRestrictionsItems.clear();
        restrictionsHashMap.clear();
        Platform.runLater(() -> {
            SceneManager.clearPane("restrictionsFP2");
            SceneManager.clearPane("restrictionsQueueFP");
            SceneManager.clearPane("restrictionsHashMapFP");
        });
    }
    //Wipes the DietaryRestrictionsScene page clean
    @FXML
    void restrictionsChoiceNew() {
        SceneManager.displayRoot("DietaryRestrictionsScene.fxml");
        Parent root = SceneManager.getCurrentRoot();
        Object controller = root.getProperties().get("fx:controller");
        if (controller instanceof MainSceneController) {
            MainSceneController newController = (MainSceneController) controller;
            newController.clearRestrictionsData();
        }
    }
    //Lets the user continue from where they left off
    @FXML
    void restrictionsChoiceContinue() {
        SceneManager.displayRoot("DietaryRestrictionsScene.fxml");
    }
    @FXML
    void restrictionsChoiceHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("In the dietary restrictions area, you can save as many individual " +
            "dietary preference sheets as you want. In this area, you can choose to either edit an exist dietary " +
            "preference sheet or create a new one. Choose an existing sheet from the drop down menu, or click the " +
            "\"create a new page\" button.");
        alert.showAndWait();
    }
    //#endregion
    //Submit Recipe Scene Methods
    //#region
    @FXML
    void exampleRecipe(ActionEvent event) throws IOException {
        SwitchScene("ExampleRecipeScene.fxml", event);
    }
    @FXML
    void submitRecipeAction(ActionEvent event) {

        String recipeName = submitRecipeNameTF.getText();
        String recipeContent = recipeContentTA.getText();
        if (recipeName.equals(null) || (recipeName.trim().isEmpty())) {
            emptyFieldAlert("recipe title field");
            System.out.println("Enter a recipe name!");
            return;
        }
        else if (recipeContent.equals(null) || (recipeContent.trim().isEmpty())) {
            System.out.println("Enter the recipe information!");
            emptyFieldAlert("recipe content field");
            return;
        }
        if (userRecipesMap.putIfAbsent(recipeName, recipeContent) != null) {
            Alert duplicateRecipeAlert = new Alert(Alert.AlertType.ERROR);
            duplicateRecipeAlert.setContentText("Your recipe has already been saved!");
            duplicateRecipeAlert.showAndWait();
            return;
        }
        Serialized.Serializer(userRecipesMap, "savedData/userRecipesMap.ser");
    }
    public void emptyFieldAlert(String field) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Please fill out " + field + ".");
        alert.showAndWait();
    }
    //Switch scene method
    public void SwitchScene(String sceneName, ActionEvent event) throws IOException {
        SceneManager.displayRoot(sceneName);
        /*
        String scenePath = "/Scenes/" + sceneName;
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if (sceneStorer.get(scenePath) == null) {
            Parent root = FXMLLoader.load(getClass().getResource(scenePath));
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            sceneStorer.put(scenePath, root);
            System.out.println("fail?");
        }
        else {
            Scene savedScene = new Scene(sceneStorer.get(scenePath));
            stage.setScene(savedScene);
            stage.show();
            System.out.println("Success?");
        }
            */
    }

    //login, welcome page, create account, continue offline methods
    //#region
    public void login(ActionEvent event) throws IOException {
        SwitchScene("LoginScene.fxml", event);
    }

    @FXML
    void continueOffline(ActionEvent event) throws IOException{
        SceneManager.displayRoot("HomepageScene.fxml");
    }

    @FXML
    void createAccount(ActionEvent event) throws IOException{
        if (true) /*STUB*/ {
        SwitchScene("CreateAccountScene.fxml", event);
        }
    }
    @FXML
    void welcome(ActionEvent event) throws IOException{
        SwitchScene("WelcomeScene.fxml", event);
    }
    //#endregion
    @FXML
    void about(ActionEvent event) throws IOException {
        SwitchScene("AboutScene.fxml", event);
    }
    @FXML
    void logout(ActionEvent event) throws IOException {

    }
    //homepage methods
    //#region

    @FXML 
    void homepage(ActionEvent event) throws IOException {
        SceneManager.displayRoot("HomepageScene.fxml");
    }
    @FXML 
    void searchRecipes(ActionEvent event) throws IOException {
        SceneManager.displayRoot("SearchScene.fxml");
        SceneManager.updatePaneInCurrentScene("searchEqualsFP", obSpecifyItems, null);
        SceneManager.updatePaneInCurrentScene("pantryContainingFP", obContainingItems, null);
        searchRestrictionsCB.getItems().clear();
        searchRestrictionsCB.getItems().addAll(restrictionsSaveSet);
    }
    @FXML 
    void editPreferences(ActionEvent event) throws IOException {
        SceneManager.displayRoot("DietaryRestrictionsChoiceScene.fxml");
        SceneManager.updatePaneInCurrentScene("restrictionsFP2", obRestrictionsItems, null);
    }
    @FXML 
    void customizePantry(ActionEvent event) throws IOException {
        SceneManager.displayRoot("PantryScene.fxml");
        SceneManager.updatePaneInCurrentScene("pantryFP2", obPantryItems, null);
    }
    @FXML 
    void submitRecipe(ActionEvent event) throws IOException {
        SceneManager.displayRoot("SubmitRecipeScene.fxml");
    }
    @FXML 
    void savedRecipes(ActionEvent event) throws IOException {
        SceneManager.displayRoot("SavedRecipesScene.fxml");
        savedPG1.setPageCount(savedRecipesBlock.size() / recipesIPP);
        savedPG2.setPageCount(favoritedRecipesBlock.size() / recipesIPP);
    }
    @FXML 
    void editSettings(ActionEvent event) throws IOException {
        SceneManager.displayRoot("EditSettingsScene.fxml");
    }
    //#endregion
    //General methods 1
    @FXML
    void badLinkAlert() {
        Alert badLink = new Alert(Alert.AlertType.ERROR);
        badLink.setContentText("Deprecated link.");
        badLink.showAndWait();
    }
    @FXML
    void hideSearchVBox() {
        hideRecipeVBoxBP(recipeVBoxBP);
    }
    @FXML
    void hideSavedVBox() {
        hideRecipeVBoxBP(savedBP1);
    }
    @FXML
    void showSearchVBox() {
        showRecipeVBoxBP(recipeVBoxBP);
    }
    @FXML
    void showSavedVBox() {
        showRecipeVBoxBP(savedBP1);
    }
    @FXML
    void hideRecipeVBoxBP(Pane recipeVBoxBP) {
        recipeVBoxBP.setVisible(false);
    }
    @FXML
    void showRecipeVBoxBP(Pane recipeVBoxBP) {
        recipeVBoxBP.setVisible(true);
    }
    @FXML
    void saveFromLibrary(ActionEvent event) {
        saveRecipe(event, favoritedRecipesBlock, savedVBox1);
    }
    @FXML
    void saveFromSearch(ActionEvent event) {
        saveRecipe(event, currentRecipeBlock, searchRecipeVBox);
    }
    @FXML
    void favoriteFromLibrary(ActionEvent event) {
        favoriteRecipe(event, savedRecipesBlock, savedVBox2);
    }
    @FXML
    void favoriteFromSearch(ActionEvent event) {
        favoriteRecipe(event, currentRecipeBlock, searchRecipeVBox);
    }
    @FXML
    void saveRecipe(ActionEvent event, ArrayList<GenericRecipeBlock> sourceBlockList, VBox parentVBox) {
        Button button = ((Button)event.getSource());
        String recipeName = ((Label)parentVBox.getChildren().get(0)).getText();
        for (int i = 0; i < savedRecipesBlock.size(); ++i) {
            if (savedRecipesBlock.get(i).getRecipeName().equals(recipeName)) {
                savedRecipesBlock.add(0, savedRecipesBlock.get(i));
                savedRecipesBlock.remove(i + 1);
                return;
            }
        }
        for (int i = 0; i < sourceBlockList.size(); ++i) {
            if (recipeName.equals(sourceBlockList.get(i).getRecipeName())) {
                savedRecipesBlock.add(0, sourceBlockList.get(i));
                return;
            }
        }

    }   
    @FXML
    void favoriteRecipe(ActionEvent event, ArrayList<GenericRecipeBlock> sourceBlockList, VBox parentVBox) {
        Button button = ((Button)event.getSource());
        String recipeName = ((Label)parentVBox.getChildren().get(0)).getText();
        for (int i = 0; i < favoritedRecipesBlock.size(); ++i) {
            if (favoritedRecipesBlock.get(i).getRecipeName().equals(recipeName)) {
                favoritedRecipesBlock.add(0, favoritedRecipesBlock.get(i));
                favoritedRecipesBlock.remove(i + 1);
                return;
            }
        }
        for (int i = 0; i < sourceBlockList.size(); ++i) {
            if (recipeName.equals(sourceBlockList.get(i).getRecipeName())) {
                favoritedRecipesBlock.add(0, sourceBlockList.get(i));
                return;
            }
        }
    }
    @FXML
    void discardRecipe(ActionEvent event, ArrayList<GenericRecipeBlock> recipesBlock) {
        VBox vbox = ((VBox)((Button)event.getSource()).getParent());
        VBox parentVBox = (VBox)vbox.getParent();
        String recipeName = ((Label)vbox.getChildren().get(0)).getText();
        parentVBox.getChildren().remove(vbox);
        for (GenericRecipeBlock recipe : recipesBlock) {
            if (recipeName.equals(recipe.getRecipeName())) {
                recipesBlock.remove(recipe);
            }
        }
    }
    @FXML
    void discardFavoritedRecipe(ActionEvent event) {
        discardRecipe(event, favoritedRecipesBlock);
    }
    @FXML
    void discardSavedRecipe(ActionEvent event) {
        System.out.println("discardSavedRecipe called with event: " + event);
        discardRecipe(event, savedRecipesBlock);
    }
    public void filterComboBox (ComboBox<String> comboBox, FilteredList<String> filteredItems) {
        comboBox.setItems(filteredItems);
        comboBox.setEditable(true);

        comboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            String text = newText == null ? "" : newText.trim();

            // Defer predicate update to next UI cycle to avoid recursion
            Platform.runLater(() -> {
                filteredItems.setPredicate(item -> {
                    if (text.isEmpty()) {
                        return true;
                    }
                    return item.toLowerCase().contains(text.toLowerCase());
                });

                if (!filteredItems.isEmpty() && !comboBox.isShowing()) {
                    comboBox.show();
                } else if (filteredItems.isEmpty()) {
                    comboBox.hide();
                }
            });
        });
    }
    public void addAllItems(ComboBox<String> comboBox, FilteredList<String> filteredItems, Pane pane) {
        String value = comboBox.getValue();
        if ((value == null) || value.trim().equals("")) {
            System.out.println("Please enter a type of food! This function adds all ingredients containing the phrase that you enter.");//REPLACEWITHALERT
        }
        else {
            for (int i = 0; i < filteredItems.size(); ++i) {
                String buttonName = filteredItems.get(i);
                Button button = new Button(buttonName);
                pane.getChildren().add(button);
                button.setOnAction((e) -> {
                    pane.getChildren().remove(button);
                });

            }
        }
    }
    public void addToSearch(ComboBox<String> comboBox, FilteredList<String> filteredItems, ObservableList<String>list, Pane pane) {
        String value = comboBox.getValue();
        if ((value == null) || value.trim().equals("")) {
            System.out.println("Please enter a type of food! This function adds all ingredients containing the phrase that you enter.");//REPLACEWITHALERT
        }
        else if (filteredItems.contains(value)) {
            boolean alreadyExists = pane.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> ((Button) node).getText())
                .anyMatch(text -> text.equals(value));
            
            if (!alreadyExists) {
                Button button = new Button(value);
                pane.getChildren().add(button);
                list.add(value);
                button.setOnAction((e) -> {
                    pane.getChildren().remove(button);
                    list.remove(value);
                });
            }
            
            Platform.runLater(() -> {
                comboBox.getSelectionModel().clearSelection();
                comboBox.getEditor().clear();
            });
        }
    }
    //alert methods
    //#region
    @FXML
    void pantryHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("This is your virtual pantry. You may or may not find it convenient, because it could take a long time " +
            "to add everything in your actual pantry. But if you do decide to use it, you can choose to search for recipes that only contain " +
            "the ingredients that you added to your virtual pantry. Click an ingredient button or use the scrolldown menu (accepts typing as well) " +
            "to add ingredients to your virtual pantry. Click a button to remove that ingredient from your pantry."
        );
        alert.showAndWait();
    }
    @FXML
    void searchHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("In this section you can search for recipes. You can set search to a combination of your pantry, your dietary restrictions sheets, " +
            "and/or keywords, or you can search all of the recipes without any filters.");
        alert.showAndWait();
    }
    @FXML
    void saveHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("This is where your saved or favorited recipes are stored.");
        alert.showAndWait();
    }
}

