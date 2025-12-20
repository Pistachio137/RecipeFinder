package app;
import java.util.*;
public class RecipeBlock {
    private String URL;
    private String recipeName;
    private int prepTime;
    private double rating;
    private int popularity;
    private int numOfIngredients;
    private ArrayList<String> ingredients;

    public RecipeBlock(String URL, String recipeName, int prepTime, double rating, int popularity, ArrayList<String> ingredients) {
        this.URL = URL;
        this.recipeName = recipeName;
        this.prepTime = prepTime;
        this.rating = rating;
        this.popularity = popularity;
        this.ingredients = ingredients;
        numOfIngredients = ingredients.size();
    }
    public RecipeBlock() {
        URL = "example URL";
        recipeName = "example Name";
        prepTime = 0;
        rating = 0.0;
        popularity = 7;
        ingredients = new ArrayList<String>();
    }
    @Override
    public String toString() {
        return recipeName;
    }

}
