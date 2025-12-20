package app;
import java.io.Serializable;

public class GenericRecipeBlock extends SuperRecipeBlock implements Serializable {
    //All of the fields and methods in this class except are exactly what you'd think when you read them.
    //"recipeNumber" just holds the line number of the recipe from where it was in the csv file. 
    private String URL;
    private String recipeName;
    private int numOfIngredients;
    private int recipeNumber;
    private String[] ingredients;
    private String[] ingredientQuantities;
    private String[] instructions;
    public GenericRecipeBlock (String URL, String recipeName, String[] ingredients, String[] ingredientQuantities, String[] instructions, int recipeNumber) {
        this.URL = URL;
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.numOfIngredients = ingredients.length;
        this.ingredientQuantities = ingredientQuantities;
        this.instructions = instructions;
        this.recipeNumber = recipeNumber;
    }
    public GenericRecipeBlock() {
        System.out.println("No data gathered");
    }
    public String[] getInstructions() {
        return instructions;
    }
    public String[] getIngredientQuantities() {
        return ingredientQuantities;
    }
    public int getRecipeNumber() {
        return recipeNumber;
    }
    public String toString() {
        String myString = "";
        String ingredientsString = "";
        String quantitiesString = "";
        String instructionsString = "";
        for (int i = 0; i < ingredients.length; ++ i) {
            ingredientsString += ingredients[i];
        }
        for (int i = 0; i < ingredientQuantities.length; ++i) {
            quantitiesString += ingredientQuantities[i];
        }
        for (int i = 0; i < instructions.length; ++i) {
            instructionsString += instructions[i];
        }
        myString += (recipeNumber + " " + recipeName + " " + URL + " " + ingredientsString + " " +
            quantitiesString + " " + instructionsString);
        return myString;
    }
    
    public String getRecipeName() {
        return recipeName;
    }
    public String getIngredientsString() {
        String ingre = "[";
        for (int i = 0; i < ingredients.length; ++i) {
            if (i < ingredients.length-1) {
                ingre += (ingredients[i] + ", ");
            }
            else {
                ingre += ingredients[i];
            }
        }
        ingre += "]";
        return ingre;
    }
    public String[] getIngredients() {
        return ingredients;
    }
    public int getNumOfIngredients() {
        return numOfIngredients;
    }
    public String getURL() {
        return URL;
    }

}
