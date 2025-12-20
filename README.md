This is a recipe/pantry app designed to help you efficiently find recipes that conform to your allergies, pantry, and desired ingredients. You can add items to your virtual pantry and also the list of things you can't eat. You can search for recipes by name, ingredient(s), and also your dietary restrictions and your virtual pantry. In the datasheet I use, there's 2.2 million recipes. I'd recommend just parsing 1/3 of the recipes each run if you have a bad PC. If not, you'll want to increase the RAM allocated to this program in launch.json via "-Xmx4g". You'll need at least 10g. 

The only plugins I use are currently the JavaFX jars. They're in Dependencies under src and bin. Main is located in src/app/App and bin/app/App. You can run the program from either a CLI or the run option in your IDE. 

For the rest of the folders - 


RecipeSets holds the recipe csv file
Stylesheets holds css sheets
Scenes holds the javafx fxml files
Images holds all of the images
ServerFiles doesn't have anything right now
And Objects and TextFiles are just what I personally used while testing and debugging



How the code works - 
Main has one line, which calls RecipeBoundariesController.launch(). The ensuing screen gets user input for determining how much of the dataset to parse. Then MainSceneController is called and the dataset is parsed from a static {} initialization call in the class. 

Every ingredient is tallied by number of occurrences. Each ingredient + its tallies is stored in a
SortedRecipes object (which implements compareTo) array. The array is then sorted in descending order.

Every unique ingredient is then added to its a hashset of ingredients in the same order, and data from each parsed recipe gets inserted into a temporary GenericRecipeBlock and added to a GenericRecipeBlock array. 

The pantry and dietary restrictions sections use the collected unique ingredients to provide a list
of valid ingredients as buttons and in an editable combobox to the user, and the program checks to make sure that the ingredients exist before letting the user add them.

The search section has 4 optional search filters - pantry, dietary restrictions, exact phrases, or strings that are just "contained" within potential ingredients - E.G., using the filter "steak" in the "contained" section would pull up all recipes that use "steak" as part of an ingredient string and not necessarily the entire string. Search for only recipes that use your pantry ingredients, avoid all dietary restrictions, or search by keywords, or just pull up all the recipes by using none of those filters.

The returned recipes are displayed on the screen along with a URL to visit the website they came from.
I'm working on adding the capability to save/favorite recipes, and on making the dietary restrictions section more convenient.

I used the recipeNLG database hosted at https://www.kaggle.com/datasets/paultimothymooney/recipenlg