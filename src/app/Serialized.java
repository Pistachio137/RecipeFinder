package app;
//This class is designed to serialize and deserialized objects. 

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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.*;
import javafx.scene.layout.*;
        //This is a dedicated class for serialization and deserialization. It's used with the singleton SceneManager
        //to serialize and deSerialize user data.
        public class Serialized {
        //Serialization path variables
        final static String LD = "savedData/";
        final static String obContainingItemsPath = LD + "obContainingItems.ser";
        final static String obPantryItemsPath = LD + "obPantryItems.ser";
        final static String obSpecifyItemsPath = LD + "obSpecifyItems.ser";
        final static String obRestrictionsItemsPath = LD + "obRestrictionsItems.ser";
        final static String currBlocksPath = LD + "currentRecipeBlock.ser";
        final static String savedBlocksPath = LD + "savedRecipesBlock.ser";
        final static String favoritedBlocksPath = LD + "favoritedRecipesBlock.ser";
        final static String restrictionHashMapPath = LD + "restrictionHashMap.ser";
        final static String restrictionHashMapParentPath = LD + "restrictionHashMapParent.ser";
        final static String restrictionSaveSetPath = LD + "restrictionSaveSet.ser";

        //Observable list clones for getting and setting the useful observable lists in in MainSceneController
        ObservableList<String>obPantryItems = FXCollections.observableArrayList();
        ObservableList<String>obSpecifyItems = FXCollections.observableArrayList();
        ObservableList<String>obContainingItems = FXCollections.observableArrayList();
        ObservableList<String> obRestrictionsItems = FXCollections.observableArrayList();
        //Serialization fields. The 4 array lists correspond to the 4 user-editable observable lists in MainSceneController.
        //obPI serializes the Pantry items, obSI serializes the Specific Items (from the "equals" flowpane in the search scene)
        //obCI is for the containing items, obRI is for the restrictions items of the current dietary restrictions sheet.
        //restrHashMap is the hashmap of the bulk section of the current dietary restrictions sheet, restrHashMapParent
        //is a hashmap of all dietary restrictions sheets, and restrSaveSet is the set of dietary restrictions sheets saveNames.
        ArrayList<String>obPIArrayList = new ArrayList<>();
        ArrayList<String>obSIArrayList = new ArrayList<>();
        ArrayList<String>obCIArrayList = new ArrayList<>();
        ArrayList<String>obRIArrayList = new ArrayList<>();
        ArrayList<GenericRecipeBlock> currBlocks = new ArrayList<GenericRecipeBlock>();
        ArrayList<GenericRecipeBlock> savedBlocks = new ArrayList<GenericRecipeBlock>();
        ArrayList<GenericRecipeBlock> favoritedBlocks = new ArrayList<GenericRecipeBlock>();
        HashMap<String, String[]>restrHashMap = new HashMap<String, String[]>();
        HashMap<String, RestrictionsObject>restrHashMapParent = new HashMap<String, RestrictionsObject>();
        Set<String> restrSaveSet = new HashSet<String>();
    //method for converting observable lists to array lists
    public static ArrayList<String> OLtoAL(ObservableList<String> ol) {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < ol.size(); ++i) {
            al.add(ol.get(i));
        }
        return al;
    }
    //method for serializing objects 
    public static <T> void Serializer(T thing, String filePath) {
        try {
            System.out.println("Saving to: " + new File(filePath).getAbsolutePath());
            File file = new File(filePath);
            file.getParentFile().mkdirs();
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(thing);
        out.close();
        fileOut.close();
        //System.out.println(thing + " serialized successfully! Size " + ((ArrayList)thing).size());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    //method for deserializing objects
    public static <T> T  deSerializer(String filePath, int test, T empty) {
    try {
        System.out.println("deSerializer called");
        System.out.println("Pulling from: " + new File(filePath).getAbsolutePath());
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        T thing = (T) in.readObject();
        in.close();
        fileIn.close();
        System.out.println("deserialization successful for item " + test);
        //System.out.println(((ArrayList)thing).size());
        return thing;
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("deserialization failed for item " + test);
        }
        return empty;

}   
    //Constructs the object and sets the observable lists with the deserialized array lists
    public Serialized() {
        obPIArrayList = deSerializer(obPantryItemsPath, 1, new ArrayList<String>());
        obSIArrayList = deSerializer(obSpecifyItemsPath, 2, new ArrayList<String>());
        obCIArrayList = deSerializer(obContainingItemsPath, 3, new ArrayList<String>());
        obRIArrayList = deSerializer(obRestrictionsItemsPath, 4, new ArrayList<String>());
        obPantryItems = FXCollections.observableArrayList(obPIArrayList);
        obSpecifyItems = FXCollections.observableArrayList(obSIArrayList);
        obContainingItems = FXCollections.observableArrayList(obCIArrayList);
        obRestrictionsItems = FXCollections.observableArrayList(obRIArrayList);
        //currBlocks = deSerializer(currBlocksPath, 6, new ArrayList<GenericRecipeBlock>());
        savedBlocks = deSerializer(savedBlocksPath, 7, new ArrayList<GenericRecipeBlock>());
        favoritedBlocks = deSerializer(favoritedBlocksPath, 8, new ArrayList<GenericRecipeBlock>());
        restrHashMap = deSerializer(restrictionHashMapPath, 9, new HashMap<String, String[]>());
        restrHashMapParent = deSerializer(restrictionHashMapParentPath, 9, new HashMap<String, RestrictionsObject>());
        restrSaveSet = deSerializer(restrictionSaveSetPath, 10, new HashSet<String>());
    }
    public Serialized(int num) {
    }
    //Converts the observable lists to array lists. Serializes them and the other called objects.
    public void serializeAll() {
        Serializer((OLtoAL(obPantryItems)), obPantryItemsPath);
        Serializer((OLtoAL(obRestrictionsItems)), obRestrictionsItemsPath);
        Serializer((OLtoAL(obContainingItems)), obContainingItemsPath);
        Serializer((OLtoAL(obSpecifyItems)), obSpecifyItemsPath);
        //Serializer(currBlocks, currBlocksPath);
        Serializer(savedBlocks, savedBlocksPath);
        Serializer(favoritedBlocks, favoritedBlocksPath);
        Serializer(restrHashMap, restrictionHashMapPath);
        Serializer(restrHashMapParent, restrictionHashMapParentPath);
        Serializer(restrSaveSet, restrictionSaveSetPath);
    }
    //Sets the parameters equal to this object's fields, designed to be used after the object is constructed 
    public void getAll(ObservableList<String> ol1, ObservableList<String> ol2, ObservableList<String> ol3, ObservableList<String> ol4,
         ArrayList<GenericRecipeBlock>blocks2, ArrayList<GenericRecipeBlock>blocks3, HashMap<String, String[]> hash1, 
         HashMap<String, RestrictionsObject>hash2, HashSet<String> hashset1) {
        ol1.setAll(obPantryItems);
        ol2.setAll(obRestrictionsItems);
        ol3.setAll(obContainingItems);
        ol4.setAll(obSpecifyItems);
        blocks2.clear();
        blocks2.addAll(savedBlocks);
        blocks3.clear();
        blocks3.addAll(favoritedBlocks);
        hash1.clear();
        hash1.putAll(restrHashMap);
        hash2.clear();
        hash2.putAll(restrHashMapParent);
        hashset1.clear();
        hashset1.addAll(restrSaveSet);
    }
    //Sets all of the fields equal to the parameters, designed to be used when the GUI is closed
    public void setAll(ObservableList<String> ol1, ObservableList<String> ol2, ObservableList<String> ol3, ObservableList<String> ol4,
         ArrayList<GenericRecipeBlock>blocks2, ArrayList<GenericRecipeBlock>blocks3, HashMap<String, String[]> hash1, 
         HashMap<String, RestrictionsObject>hash2, HashSet<String> hashset1) {
        obPantryItems = ol1;
        obRestrictionsItems = ol2;
        obContainingItems = ol3;
        obSpecifyItems = ol4;
        savedBlocks = blocks2;
        favoritedBlocks = blocks3;
        restrHashMap = hash1;
        restrHashMapParent = hash2;
        restrSaveSet = hashset1;
    }
}