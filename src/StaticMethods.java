import java.util.*;
import java.io.*;
public class StaticMethods {
    public static ArrayList<RecipeBlock> recipeList(String filePath) {
        //In this method, each line of a text file gets converted into a recipe. The URL, the recipe name,
        //the prep time, the rating, the popularity, and the ingredients list are all stored
        //in a RecipeBlock object, which is then stored in a RecipeBlock ArrayList.
        String tempLine;
        //Fields that will store the information contained in the string.
        String URL = "";
        String recipeName = "";
        String prepTimeString = "";
        String ratingString = "";
        String popularityString = "";
        String ingredient = "";
        //Fields that hold the parsed values from the respective strings
        int popularity = 0;
        double rating = 0.0;
        int prepTime = 0;
        //ArrayList that contains the ingredients for each RecipeBlock
        ArrayList<String> ingredients = new ArrayList<String>();
        //The master list of RecipeBlocks
        ArrayList<RecipeBlock> recipes = new ArrayList<RecipeBlock>();
        //A temporary RecipeBlock which gets added to recipes in each iteration.
        RecipeBlock recipeBlock;
        char currChar;
        //In the text file, a "_" means that the following information is for a different field. 
        //A "{" separates each ingredient.
        int fieldCounter = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            tempLine = reader.readLine();
            while (tempLine != null) {
                //Resetting the field values
                URL = "";
                recipeName = "";
                prepTimeString = "";
                ratingString = "";
                popularityString = "";
                fieldCounter = 0;
                for (int i = 0; i < tempLine.length(); ++i) {
                    currChar = tempLine.charAt(i);
                    //increment fieldCounter and move on to the next character
                    if (currChar == '_') {
                        fieldCounter++;
                        continue;
                    }
                    //Increment ingredients counter and move on to the next character
                    if (currChar == '}') {
                        ingredients.add(ingredient);
                        ingredient = "";
                        continue;
                    }
                    //Uses fieldCounter to increment the appropiate field
                    switch (fieldCounter) {
                        case 0:
                            URL += currChar;
                            break;
                        case 1:
                            recipeName += currChar;
                            break;
                        case 2:
                            prepTimeString += currChar;
                            break;
                        case 3:
                            ratingString += currChar;
                            break;
                        case 4:
                            popularityString += currChar;
                            break;
                        case 5:
                            ingredient += currChar;
                    }
                }
                
                //Changing strings to numbers
                prepTime = Integer.parseInt(prepTimeString);
                rating = Double.parseDouble(ratingString);
                popularity = Integer.parseInt(popularityString);
                //Instantiating a RecipeBlock using the fields that were just extracted
                recipeBlock = new RecipeBlock(URL, recipeName, prepTime, rating, popularity, ingredients);
                //Adding that RecipeBlock to the recipes ArrayList
                recipes.add(recipeBlock);
                //Reading the next line of the text file
                tempLine = reader.readLine();
            }
            //Closing the text file
            reader.close();
        }
        //Catching errors
        catch (IOException io) {
            io.printStackTrace();
            System.out.println("IO error");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("General error");
        }
        /*for (int i = 0; i < recipes.size(); ++i) {
            System.out.println(recipes.get(i));
        }*/
        return recipes;
    }
}
