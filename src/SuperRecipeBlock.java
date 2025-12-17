
public class SuperRecipeBlock {
    private String URL;
    private String recipeName;
    private int numOfIngredients;
    String[] ingredients;

    public SuperRecipeBlock(String URL, String recipeName, String[] ingredients) {
        this.URL = URL;
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.numOfIngredients = ingredients.length;
    }
    public SuperRecipeBlock() {
        URL = "example URL";
        recipeName = "example Name";
        numOfIngredients = 0;
    }
    @Override
    public String toString() {
        return recipeName;
    }
    public String getURL() {
        return URL;
    }
    public String getRecipeName() {
        return recipeName;
    }
    public String getIngredientsString() {
        return "super block ingredients";
    }
    public String[] getIngredients() {
        return ingredients;
    }
    public int getNumOfIngredients() {
        return numOfIngredients;
    }

}

