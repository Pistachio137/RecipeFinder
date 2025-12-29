package app;
import java.io.*;
import java.util.*;

import javafx.scene.control.CheckBox;
public class StaticGenericThings {
    ////App-wide fields obtained from parsing the recipe csv file
    //#region
    private static GenericRecipeBlock[] genericRecipeArray;// = StaticGenericThings.ingredientParserArray(200000);
    private static HashMap<String, Integer> genericHashMap;// =  StaticGenericThings.arrayToHashMap(genericRecipeArray, genericRecipeArray.length);
    private static SortedRecipes[] countedGenericIngredients;// = StaticGenericThings.sortRecipes(genericHashMap);
    private static String[] uniqueIngredients;// = StaticGenericThings.uniqueNames(genericHashMap);
    //#endregion
    //Getters and settings for the app-wide fields.
    
    //#region
    public static final long serialIUD = (long)1;
    public static String genericPath;
    static {
        genericPath = "/RecipeSets/RecipeNLG_dataset.csv";
    }
    public static void setGenericRecipeArray(GenericRecipeBlock[] array) {
        genericRecipeArray = array;
    }
    public static GenericRecipeBlock[] getGenericRecipeArray() {
        return genericRecipeArray;
    }
    public static void setGenericHashMap(HashMap<String, Integer> hashMap) {
        genericHashMap = hashMap;
    }
    public static HashMap<String, Integer> getGenericHashMap() {
        return genericHashMap;
    }
    public static void setCountedGenericIngredients(SortedRecipes[] sortedRecipes) {
        countedGenericIngredients = sortedRecipes;
    }
    public static SortedRecipes[] getCountedGenericIngredients() {
        return countedGenericIngredients;
    }
    public static void setUniqueIngredients(String[] array) {
        uniqueIngredients = array;
    }
    public static String[] getUniqueIngredients() {
        return uniqueIngredients;
    }
    public static CheckBox[] returnCHArray(String[] sortedIngredients) {
        CheckBox[] array = new CheckBox[sortedIngredients.length];
        for (int i = 0; i < sortedIngredients.length; ++i) {
            CheckBox tempCheckBox = new CheckBox();
            tempCheckBox.setText(sortedIngredients[i]);
            array[i] = tempCheckBox;
        }
        return array;
    }
    //#endregion;
    //Method that parses the csv recipes into an array. 
    public static GenericRecipeBlock[] ingredientParserArray(int start, int end) {
    String line = "no";
    String numberString = "";
    String recipeName = "";
    String word = "";
    String URL = "";
    int number = 0;
    char myChar = 'a';
    int g = 0;
    boolean lineFail = false;
    int tracker = 0;
    ArrayList<String> ingredientList = new ArrayList<String>();
    ArrayList<String> instructionList = new ArrayList<String>();
    ArrayList<String> quantityList = new ArrayList<String>();
    ArrayList<Integer> errorCatcher = new ArrayList<Integer>();
    GenericRecipeBlock[] genericRecipeBlocks = new GenericRecipeBlock[end - start];

    try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(StaticGenericThings.class.getResourceAsStream(genericPath)));
        while (tracker < start) {
        line = reader.readLine();
        tracker += 1;
        }
        for (int i = start; i < end; ++i) {
            if (lineFail) {
                System.out.println(numberString);
            }
            lineFail = true;
            URL = "";
            recipeName = "";
            numberString = "";
            ingredientList.clear();
            quantityList.clear();
            instructionList.clear();
            try {
            //System.out.println(i + " large loop");
            line = reader.readLine();
            for (int j = 0; j < line.length(); ++j) {
                //System.out.println(j + " smaller loop");
                myChar = line.charAt(j);
                while (true) {
                    numberString += line.charAt(j);
                    j++;
                    if (line.charAt(j) == ',') {
                        break;
                    }
                    //System.out.print(j + "first while loop ");
                }
                //System.out.println(numberString);
                while (true) {
                    j++;
                    myChar = line.charAt(j);
                    if (myChar == ',') {
                        break;
                    }
                    recipeName += myChar;
                }
                //System.out.println(recipeName);
                j+=3;
                myChar = line.charAt(j);
                //quantities array
                    while (myChar != ']') {
                        //System.out.print("e");
                        if ((myChar != '"') && (myChar != ',')) {
                            word += myChar;
                        }
                        else if (word.length() > 0){
                            quantityList.add(word);
                            //System.out.print(word);
                            word = "";
                        }
                        j++;
                        myChar = line.charAt(j);
                    }
                    j++;
                    myChar = line.charAt(j);
                    //Instructions array
                    while (myChar != ']') {
                        if ((myChar != '"') && (myChar != ',') && (myChar != '[')) {
                            word += myChar;
                        }
                        else if (word.length() > 0){
                            instructionList.add(word);
                            //System.out.print(word);
                            word = "";
                        }
                        j++;
                        myChar = line.charAt(j);
                    }
                    j+=3;
                    myChar = line.charAt(j);
                    //URL
                    //System.out.print("\n" + myChar);
                    while (myChar != ',') {
                        word += myChar;
                        myChar = line.charAt(j);
                        j++;
                    }
                    URL = word;
                    //System.out.println(URL);
                    word = "";
                    while (myChar != '[') {
                        j++;
                        myChar = line.charAt(j);
                        //System.out.print("1");
                    }
                    //ingredient array
                    while (myChar != ']') {
                        if ((myChar != '"') && (myChar != ',') && (myChar != '[')) {
                            word += myChar;
                            if (word.equals(" ")) {
                                word = "";
                            }
                        }
                        else if (word.length() > 0) {
                            word.trim();
                            word.toLowerCase();
                            ingredientList.add(word);
                            //System.out.print("Ingredient:" + word);
                            word = "";
                        }
                        j++;
                        myChar = line.charAt(j);
                    } 
                    number = Integer.parseInt(numberString);
                    String[] tempArray1 = new String[ingredientList.size()];
                    String[] tempArray2 = new String[quantityList.size()];
                    String[] tempArray3 = new String[instructionList.size()];
                    URL = URL.replace("wwww.", "https://");
                    for (int k = 0; k < tempArray1.length; ++k) {
                        tempArray1[k] = ingredientList.get(k);
                    }
                    for (int k = 0; k < tempArray2.length; ++k) {
                        tempArray2[k] = quantityList.get(k);
                    }
                    for (int k = 0; k < tempArray3.length; ++k) {
                        tempArray3[k] = instructionList.get(k);
                    }
                    if (((URL == null) || (recipeName == null) || (tempArray1 == null) || (tempArray2 == null) || (tempArray3 == null) || (numberString == null))) {
                        break;
                    }
                    GenericRecipeBlock tempGenericRecipeBlock = new GenericRecipeBlock(URL, recipeName, tempArray1, tempArray2, tempArray3, number);
                    if (tempGenericRecipeBlock == null) {
                        break;
                    }
                    genericRecipeBlocks[i - start] = tempGenericRecipeBlock;
                    lineFail = false;
                    if ((i % 200000) == 0) {
                        System.out.println(g);
                        System.out.println("total memory in MB" + (Runtime.getRuntime().totalMemory() / 1048576));
                        System.out.println("free memory in MB" + (Runtime.getRuntime().freeMemory() / 1048576));
                        System.gc();
                        System.out.println("total memory in MB" + (Runtime.getRuntime().totalMemory() / 1048576));
                        System.out.println("free memory in MB" + (Runtime.getRuntime().freeMemory() / 1048576));
                        System.out.println(g);
                        g++;
                    }
                    break;
                    }
                    
                    
                }
                catch (Exception e) {
                    System.out.println("Line " + (i + 2) + "failed, moving on.");
                    errorCatcher.add(i+2);
                }
            }
            //System.out.println(errorCatcher.size());
            for (int i = 0; i < errorCatcher.size(); ++i) {
                System.out.println("Test");
                System.out.println(errorCatcher.get(i));
            }
                
            
        reader.close();
    }
    catch (IOException io) {
        System.out.println("I/O exception in StaticGenericFields.java");
        io.printStackTrace();
    }
    catch (Exception e) {
        System.out.println("General exception in StaticGenericFields.java");
        e.printStackTrace();
    }
    return genericRecipeBlocks;
}

//Method that counts how many times each ingredient occurs in the overall array, returning a <String, Integer> hashmap.
public static HashMap<String, Integer> arrayToHashMap(GenericRecipeBlock[] recipes, int limit) {
    String[] ingredients;
    String ingredient;
    Integer num = 0;
    HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
    for (int i = 0; i < limit; ++i) {
        if (recipes[i] == null) {
            continue;
        }
        ingredients = recipes[i].getIngredients();
        for (int j = 0; j < ingredients.length; ++j) {
            ingredient = ingredients[j].toLowerCase();
        hashMap.putIfAbsent(ingredient, 0);
        num = hashMap.get(ingredient);
        hashMap.put(ingredient, (num + 1));
        }
    }
    return hashMap;
}

//Method that converts the hashmap to a set of keys, then converts that to an array of the class SortedRecipes,
//Which implements compareTo to sort based on the "number" int that SortedRecipes stores as a field.
public static SortedRecipes[] sortRecipes(HashMap<String, Integer> hashMap) {
    Set<String> recipeSet = hashMap.keySet();
    String[] keyStrings = recipeSet.toArray(new String[0]);
    SortedRecipes[] sortedRecipes = new SortedRecipes[recipeSet.size()];
    SortedRecipes tempRecipe;
    String ingredient = "";
    int number = 0;
    for (int i = 0; i < keyStrings.length; ++i) {
        ingredient = keyStrings[i];
        number = hashMap.get(ingredient);
        tempRecipe = new SortedRecipes(ingredient, number);
        sortedRecipes[i] = tempRecipe;
    }
    Arrays.sort(sortedRecipes, Collections.reverseOrder());
    return sortedRecipes;
}

//This method is just an array of each unique ingredients, which is obtained from the SortedRecipes array.
public static String[] uniqueNames(SortedRecipes[] array) {
    String[] orderedIngredients = new String[array.length];
    for (int i = 0; i < array.length; ++i) {
        orderedIngredients[i] = array[i].getName();
    }
    return orderedIngredients;
}

//Just a test method that I didn't end up using
public static void genericAssimilator() {
    String line;
    String ingredient = "";
    int bracketCounter = 0;
    char myChar;
    ArrayList<String> tempArrayList = new ArrayList<String>();

    try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(StaticGenericThings.class.getResourceAsStream(genericPath)));
        for (int i = 0; i < 1000; ++i) {
            line = reader.readLine();
            for (int j = 0; j < line.length(); ++j) {
                myChar = line.charAt(j);
                if (myChar == '[') {
                    bracketCounter += 1;
                }

                if (bracketCounter == 3) {
                    if ((myChar != '"') && (myChar != '[') && (myChar != ',')) {
                        ingredient += myChar;
                    }
                    else if (ingredient.length() > 0) {
                        tempArrayList.add(ingredient);
                        ingredient = "";
                    }
                }
            }
            bracketCounter = 0;
        }
        reader.close();
    }
    catch (IOException io) {
        System.out.println("I/O exception in StaticGenericFields.java");
        io.printStackTrace();
    }
    catch (Exception e) {
        System.out.println("General exception in StaticGenericFields.java");
        e.printStackTrace();
    }
}


}

