public class UserRecipes {
    private String recipeTitle;
    private String recipeContent;
    
    public String getRecipeTitle() {
        return recipeTitle;
    }
    public String getRecipeContent() {
        return recipeContent;
    }
    public void setRecipeTitle(String title) {
        recipeTitle = title;
    }
    public void setRecipeContent(String content) {
        recipeContent = content;
    }
    public UserRecipes() {
    }
    public UserRecipes(String title, String content) {
        recipeTitle = title;
        recipeContent = content;
    }
}
