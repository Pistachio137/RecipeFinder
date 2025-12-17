This is a recipe/pantry app designed to help you efficiently find recipes that conform to your allergies, pantry, and desired ingredients. You can add items to your virtual pantry and also the list of things you can't eat. You can search for recipes by name, ingredient(s), and also your dietary restrictions and your virtual pantry. In the datasheet I use, there's 2.2 million recipes. By default, the program only parses a small amount of this. However, I ran into Java memory issues (4 GB used) at 1 million recipes, crashing the program. You can change the default memory usage of your JVM if you want to increase the amount of recipes you can parse. 

The only plugins are currently the JavaFX jars. They're in Dependencies under src. 

For the rest of the folders - 

RecipeSets holds the recipe csv file
Stylesheets holds css sheets
Scenes holds the javafx fxml files
Images holds all of the images
Controllers and ServerFiles don't have anything right now
And Objects and TextFiles are just what I personally used while testing and debugging
If you want to compile and run the program from outside of source, you'll have to change the relative file paths of the folders.

The main logic-based classes are StaticGenericThings, SceneManager, MainSceneController, and GenericRecipeBlock, the rest don't get used as much or are just there as placeholders.

I used the recipeNLG database hosted at https://www.kaggle.com/datasets/paultimothymooney/recipenlg