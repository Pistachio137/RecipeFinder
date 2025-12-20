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
import java.util.List;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.*;
import javafx.scene.layout.*;
public class MainSceneController {

        //This is a very beefy class that I haven't had time to organize/split into smaller classes. 
        //Also, some of the comments might be wrong or outdated, so sorry about that. It's still in development
        //So I keep some stuff commented out instead of just deleting them. 

        // Public getters for key panes
        public FlowPane getPantryFP2() { return pantryFP2; }
        public FlowPane getRestrictionsFP2() { return restrictionsFP2; }
        public FlowPane getSearchContainingFP() { return searchContainingFP; }
        public FlowPane getSearchEqualsFP() { return searchEqualsFP; }
        //Controller class fields, including the serialized fields.
        //#region
        ImageView myImageView;
        //Serialized class object
        Serialized serialized = new Serialized();
        //Fields obtained from parsing.
        static GenericRecipeBlock[] genRecipeArray;
        static HashMap<String, Integer> genHashMap;
        static SortedRecipes[] countedGenIngredients;
        static String[] uniqueIngreds;
        static {
        final int NLGSIZE = 2231150;
        final int NLGINCREMENTS = NLGSIZE / 100;
        int startPercent = RecipeBoundariesController.getStartNum();
        int endPercent = RecipeBoundariesController.getEndNum();
        int startNum = startPercent * NLGINCREMENTS;
        int endNum = 0;  
        if (endPercent == 100) {
            endNum = NLGSIZE;
        }
        else {
            endNum = NLGINCREMENTS * endPercent;
        }
        genRecipeArray = StaticGenericThings.ingredientParserArray(startNum, endNum);
        genHashMap = StaticGenericThings.arrayToHashMap(genRecipeArray, genRecipeArray.length);
        countedGenIngredients = StaticGenericThings.sortRecipes(genHashMap);
        uniqueIngreds = StaticGenericThings.uniqueNames(countedGenIngredients);
        StaticGenericThings.setGenericRecipeArray(genRecipeArray);
        StaticGenericThings.setGenericHashMap(genHashMap);
        StaticGenericThings.setCountedGenericIngredients(countedGenIngredients);
        StaticGenericThings.setUniqueIngredients(uniqueIngreds);
        }
        static GenericRecipeBlock[] controlGenRecBlock = StaticGenericThings.getGenericRecipeArray();
        static SortedRecipes[] controlGenCountIngreds = StaticGenericThings.getCountedGenericIngredients();
        static String[] controlGenUnGreds = StaticGenericThings.getUniqueIngredients();
        static ObservableList<String>obPantrySelections = FXCollections.observableArrayList(controlGenUnGreds);
        int pantryCounter = 0;
        //Serialized/deserialized fields block + some of their paths.
        ObservableList<String>obPantryItems = FXCollections.observableArrayList();
        ObservableList<String>obSpecifyItems = FXCollections.observableArrayList();
        ObservableList<String>obContainingItems = FXCollections.observableArrayList();
        ObservableList<String> obRestrictionsItems = FXCollections.observableArrayList();
        Map<String, String> userRecipesMap = new HashMap<String, String>();
        ArrayList<GenericRecipeBlock> currentRecipeBlock = new ArrayList<GenericRecipeBlock>();
        ArrayList<GenericRecipeBlock> favoritedRecipesBlock = new ArrayList<GenericRecipeBlock>();
        ArrayList<GenericRecipeBlock> savedRecipesBlock = new ArrayList<GenericRecipeBlock>();
        String currentRecipeBlockPath = "savedData/currentRecipeBlock.ser";
        String favoritedRecipesBlockPath = "savedData/favoritedRecipesBlock.ser";
        String savedRecipesBlockPath = "savedData/savedRecipesBlock.ser";
        String userRecipesMapPath = "savedData/userRecipesMap.ser";
        FilteredList<String> filteredEqualsItems = new FilteredList<>(obPantrySelections, s -> true);
        FilteredList<String> filteredContainingItems = new FilteredList<>(obPantrySelections, s -> true);
        public static Map<String, Parent> sceneStorer = new HashMap<String, Parent>();
        //Pagination block. IPP stands for "Items per page".
        int numIngreds = controlGenUnGreds.length;
        int pantryIPP = 150;
        int restrictionsIPP = 150;
        int recipesIPP = 50;
        int paginationTracker = 0;
        int recipeNum = 0;

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
        private Pagination savedPG1 = new Pagination(0/recipesIPP);
        @FXML
        private Pagination savedPG2 = new Pagination(0/recipesIPP);
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
        private Boolean restrictionsYesBoolean = false;
        private Boolean pantryYesBoolean = false;
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
        private CheckBox searchRestrictionsCB;
        @FXML
        private TextField searchRecipeNamesTF;
        @FXML
        private Button superSearchButton;
        @FXML
        private Pagination searchPG1 = new Pagination(numIngreds/recipesIPP);
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
        @FXML
        private Button restrictionHelpButton;
        @FXML
        private Button restrictionBackButton;
        @FXML
        private Button addRestrictionButton;
        @FXML
        private ComboBox<String> restrictionsCB1 = new ComboBox<String>();
        @FXML
        private Pagination restrictionsPG1 = new Pagination(numIngreds/pantryIPP);
        @FXML
        private FlowPane restrictionsFP1;
        @FXML
        private FlowPane restrictionsFP2;
        //#endregion
        
        //PantryScene controls
        //#region
        @FXML
        private ComboBox<String> pantryCB1 = new ComboBox<>();
        //CheckBox[] checkBoxIngredients = StaticGenericThings.getPantryChArray();
        @FXML
        private Pagination pantryPG1 = new Pagination(numIngreds/pantryIPP);
        @FXML
        private SplitPane pantrySplit2 = new SplitPane();
        @FXML
        private Button pantryBackButton;
        @FXML
        private FlowPane pantryFP1 = new FlowPane();
        @FXML
        private FlowPane pantryFP2 = new FlowPane();
        @FXML
        private Button testButton2;
        @FXML
        private Button testButton3 = new Button("Test successful.");
        @FXML
        private FlowPane testFlowPane;
        @FXML
        private HBox pantryHBox2 = new HBox();
        //#endregion


    /* 
    public static void setPantryItems() {
        try {
        StaticGenericThings.Serializer(, "src/LocalData/obPantryItems.ser");
        }
        catch (Exception e) {
            e.printStackTrace();;
            System.out.println("Serialization failed.");
        }
    }
    public ObservableList<String> getPantryItems() {
        try {
        obPantryItems = StaticGenericThings.deSerializer("src/LocalData/obPantryItems.ser");
        return obPantryItems;
        }
        catch (Exception e) {
            e.printStackTrace();;
            System.out.println("Deserialization failed.");
        }
        return FXCollections.observableArrayList();
    }
        */
    public ArrayList<String> obToArrayList(ObservableList<String> list) {
        ArrayList<String> arrayList = new ArrayList();
        return arrayList;
    }
    //Initialize method
    @FXML
    void initialize() {
         //Making scene manager save the scenes to files when the user exits the app
        //Creating recipes array, String/Integer hashmap of ingredients/occurrences, an array of that hashmap, and
        //a string array of all the unique ingredients. Then using them to set the static fields in StaticGenericThings for
        //project-wide access.

         SceneManager.saveScenes(obPantryItems, obRestrictionsItems, obContainingItems, obSpecifyItems,
             savedRecipesBlock, favoritedRecipesBlock);
         //Setting the local observable lists to the serialized lists if possible
         serialized.getAll(obPantryItems, obRestrictionsItems, obContainingItems, obSpecifyItems, 
             savedRecipesBlock, favoritedRecipesBlock);
         pantryPG1.setPageFactory((Integer pageIndex) -> createPage(pageIndex, pantryFP2, obPantryItems, pantryIPP));
         pantryCB1.setItems(obPantrySelections);
         restrictionsPG1.setPageFactory((Integer pageIndex) -> createPage(pageIndex, restrictionsFP2, obRestrictionsItems, restrictionsIPP));
         restrictionsCB1.setItems(obPantrySelections);
         if (searchEqualsCB != null) filterComboBox(searchEqualsCB, filteredEqualsItems);
         if (searchContainingCB != null) filterComboBox(searchContainingCB, filteredContainingItems);
         userRecipesMap = Serialized.deSerializer(userRecipesMapPath, 5, new HashMap<String, String>());
         /*
         favoritedRecipesBlock = Serialized.deSerializer(favoritedRecipesBlockPath, 6, new ArrayList<GenericRecipeBlock>());
         savedRecipesBlock = Serialized.deSerializer(savedRecipesBlockPath, 7, new ArrayList<GenericRecipeBlock>());
         currentRecipeBlock = Serialized.deSerializer(currentRecipeBlockPath, 8, new ArrayList<GenericRecipeBlock>());
            */
         savedPG1.setPageFactory((Integer pageIndex) -> createSavedPage(pageIndex, recipesIPP, 0, savedVBox1, 
            savedRecipesBlock, savedPG1, favoritedRecipesBlock, savedPG2));

        savedPG2.setPageFactory((Integer pageIndex) -> createSavedPage(pageIndex, recipesIPP, 0, savedVBox2, 
            favoritedRecipesBlock, savedPG2, savedRecipesBlock, savedPG1));
        obContainingItems.clear();
        obSpecifyItems.clear();
        obContainingItems.clear();
    }

         /* 
         StaticGenericThingsdeSerializer(obPantryItemsPath, obPantryItems, 1);
         StaticGenericThings.deSerializer(obRestrictionsItems, obRestrictionsItemsPath, 2);
         StaticGenericThings.deSerializer(obSpecifyItems, obSpecifyItemsPath, 3);
         StaticGenericThings.deSerializer(obContainingItems, obContainingItemsPath, 4);
         */
         /* 
         for (int i = 0; i < obPantryItems.size(); ++i) {
            String buttonName = obPantryItems.get(i);
            Button button = new Button(buttonName);
            pantryFP2.getChildren().add(button);
            button.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                pantryFP2.getChildren().remove(button);
                obPantryItems.remove(buttonName);
            }
        });
    }       
        for (int i = 0; i < obRestrictionsItems.size(); ++i) {
            String buttonName = obRestrictionsItems.get(i);
            Button button = new Button(buttonName);
            restrictionsFP2.getChildren().add(button);
            button.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                restrictionsFP2.getChildren().remove(button);
                obRestrictionsItems.remove(buttonName);
        }
        });
        }
    }
        */

    //Search Scene Methods
    //restrictions yes, pantry yes, 4 observable lists, conGenRecBlock, currentRecipeBlock, searchPG1, setPagination searchPG1.setPageFactory((Integer pageIndex) -> createRecipePage(pageIndex, recipesIPP);
    //obPantryItems, obRestrictionsItems, obSpecifyItems, obContainingItems
    @FXML
    void findRecipes() {
        currentRecipeBlock.clear();
        System.out.println("Entering findRecipes()");
        int errorsNum = 0;
        String nameSearch = searchRecipeNameTF.getText().toLowerCase();
        boolean searchByName = false;
        if ((nameSearch != null) && (!nameSearch.trim().equals(""))) {
            searchByName = true;
            }
        outerLoop:
        for (int i = 0; i < controlGenRecBlock.length; ++i) {
            System.out.println("loop " + i);
            try {
            int specifyCounter = 0;
            int containingCounter = 0;
            String recipeName = controlGenRecBlock[i].getRecipeName().toLowerCase();
            String[] ingredients = controlGenRecBlock[i].getIngredients();
            HashSet<String> ingredientsSet = new HashSet<String>(Arrays.asList(ingredients));
            if (searchByName) {
                if (!recipeName.contains(nameSearch)) {
                    continue outerLoop;
                }
            }
            if (restrictionsYesBoolean) {
                HashSet<String> restrictionsSet = new HashSet<String>(obRestrictionsItems);
                for (String item : ingredients) {
                    if (restrictionsSet.contains(item)) {
                        continue outerLoop;
                    }
                }
                }
            if (pantryYesBoolean) {
                HashSet<String> pantrySet = new HashSet<String>(obPantryItems);
                for (String item : ingredients) {
                    if (!pantrySet.contains(item)) {
                        continue outerLoop;
                    }
                }
            }
            HashSet<String> specifySet = new HashSet<String>(obSpecifyItems);
            for (String item : obSpecifyItems) {
                if (!ingredientsSet.contains(item)) {
                    continue outerLoop;
                }
            }
            
            for (String query : obContainingItems) {
                for (String item : ingredients) {
                    if (item.contains(query)) {
                        containingCounter ++;
                        break;
                    }
                }
            }
            if (containingCounter < obContainingItems.size()) {
                containingCounter = 0;
                continue outerLoop;
            }
                
            currentRecipeBlock.add(controlGenRecBlock[i]);
            System.out.println(currentRecipeBlock.size());
            
        }
        catch (Exception e) {
            System.out.println("Error occurred while converting controlGenRecBlock to currentRecipeBlock at recipe block number " + i);
            errorsNum++;
            if (errorsNum > 49) {
                System.out.println("Exceptions exceeded. Stopping the process.");
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
    void restrictionsYes() {
        restrictionsYesBoolean = searchPantryCB.isSelected();
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
    //building block for adding a button to a pane
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
    @FXML
    void addToRestrictionsCB() {
        addButtonToStockCB(restrictionsCB1, restrictionsFP2, obRestrictionsItems, obPantrySelections);
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
    }
    @FXML 
    void editPreferences(ActionEvent event) throws IOException {
        SceneManager.displayRoot("DietaryRestrictionsScene.fxml");
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
    //#region
    

}

