package app;
import Controllers.*;
import java.util.*;
import javafx.collections.*;


public class App {
    public static void main(String[] args) {
        //ArrayList<RecipeBlock> recipes = StaticMethods.recipeList("C:\\Tech Stuff\\Computer Science\\recipes.txt");
        RecipeBoundariesController.launch(RecipeBoundariesController.class, args);
        //The start and end limits for the for loop that parses through the list of recipes. There's 2.2 million recipes in the CSV
        //file. It takes a minute or so to process 1 million recipes, so I've been using a smaller number when developing the app.
        /* 
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
        //Creating recipes array, String/Integer hashmap of ingredients/occurrences, an array of that hashmap, and
        //a string array of all the unique ingredients. Then using them to set the static fields in StaticGenericThings for
        //project-wide access.
        GenericRecipeBlock[] genRecipeArray = StaticGenericThings.ingredientParserArray(startNum, endNum);
        HashMap<String, Integer> genHashMap = StaticGenericThings.arrayToHashMap(genRecipeArray, genRecipeArray.length);
        SortedRecipes[] countedGenIngredients = StaticGenericThings.sortRecipes(genHashMap);
        String[] uniqueIngreds = StaticGenericThings.uniqueNames(countedGenIngredients);
        StaticGenericThings.setGenericRecipeArray(genRecipeArray);
        StaticGenericThings.setGenericHashMap(genHashMap);
        StaticGenericThings.setCountedGenericIngredients(countedGenIngredients);
        StaticGenericThings.setUniqueIngredients(uniqueIngreds);
        */
        //GenericRecipeBlock[] genericRecipeArray = StaticGenericThings.ingredientParserArray(end);
        //HashMap<String, Integer> genericHashMap =  StaticGenericThings.arrayToHashMap(genericRecipeArray, genericRecipeArray.length);
       // SortedRecipes[] countedGenericIngredients = StaticGenericThings.sortRecipes(genericHashMap);
        //String[] uniqueIngredients = StaticGenericThings.uniqueNames(genericHashMap);
        //The first scene is launched from FirstScene, and then the MainSceneController class takes it from there.
        
        /*for (int i = 0; i < end; i+=200) {
            System.out.println(genRecipeArray[i].getIngredientsString() + genRecipeArray[i].getURL() + genRecipeArray[i].getRecipeName() + genRecipeArray[i].getNumOfIngredients());
        }*/
        /*for (int i = 0; i < uniqueIngreds.length; ++i) {
            if (uniqueIngreds[i] == null) {
                System.out.println(i + " is null");
            }
            else {
                System.out.println(i + "test passed");
            }
        }
            */
        /* 
        StaticGenericThings.Serializer(genericRecipeArray, "src/Objects/genericRecipeArray.ser");
        StaticGenericThings.Serializer(countedGenericIngredients, "src/Objects/countedGenericIngredients.ser");
        StaticGenericThings.Serializer(uniqueIngredients, "src/Objects/uniqueIngredients.ser");
        */
        /* 
       String serPath = "src/Objects/";
       GenericRecipeBlock[] genericRecipeArray = StaticGenericThings.deSerializer(serPath + "genericRecipeArray.ser");
       SortedRecipes[] countedGenericIngredients = StaticGenericThings.deSerializer(serPath + "countedGenericIngredients.ser");
       String[] uniqueIngredients = StaticGenericThings.deSerializer(serPath + "uniqueIngredients.ser");
       */
      /* 
       String textPath = "src/TextFiles/";
       try {
       FileWriter fileOut = new FileWriter(textPath + "countedGenericIngredients2.txt");
       BufferedWriter writer = new BufferedWriter(fileOut);
       for (int i = 0; i < countedGenericIngredients.length; ++i) {
        writer.write(countedGenericIngredients[i].getName());
        if (i < countedGenericIngredients.length - 1) {
        writer.newLine();
        }
       }
       writer.close();
       fileOut.close();
       }
       catch (Exception e) {
        System.out.println("Writing to file failed.");
        e.printStackTrace();

       }
       */
    }
}
