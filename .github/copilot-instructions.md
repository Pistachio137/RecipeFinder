# AI Coding Agent Instructions for RecipeFinder

## Project Overview
RecipeFinder is a JavaFX desktop application that helps users find recipes based on their pantry ingredients, dietary restrictions, and search criteria. It parses a large dataset of 2.2 million recipes from RecipeNLG, tallies ingredient frequencies, and provides filtering/search capabilities.

## Architecture
- **Entry Point**: `src/app/App.java` launches `RecipeBoundariesController` for dataset parsing boundaries, then `MainSceneController` handles the main UI and data processing.
- **Data Flow**: CSV parsing → ingredient tallying/sorting → UI population → user interactions → serialization of user data.
- **Key Components**:
  - `StaticGenericThings`: Holds parsed recipe arrays, ingredient hashmaps, and sorted data.
  - `MainSceneController`: Singleton controller managing UI logic, search, and data persistence.
  - `SceneManager`: Singleton preserving scene state during navigation.
  - `Serialized`: Handles saving/loading user data (pantry, restrictions, saved recipes) to `savedData/` as `.ser` files.

## Data Models
- `SuperRecipeBlock`: Base class with URL, name, ingredients array.
- `GenericRecipeBlock`: Extends SuperRecipeBlock, adds quantities, instructions, recipeNumber; implements Serializable.
- `RecipeBlock`: Separate class with prepTime, rating, popularity; ingredients as ArrayList.

## UI Structure
- **Scenes**: FXML files in `Scenes/` define layouts.
- **Controllers**: Classes in `Controllers/` handle events and logic.
- **Styling**: CSS files in `Stylesheets/`.
- **Assets**: Images in `Images/`.

## Critical Workflows
- **Dataset Parsing**: Occurs in `MainSceneController` constructor. Parses subset of RecipeNLG_dataset.csv (default 1/3 for development). Full parsing requires 10GB RAM.
- **Build**: Compile `src/` to `bin/` using JavaFX modules:
  ```
  javac --module-path src/Dependencies/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml,javafx.base -cp src;bin -d bin src/**/*.java
  ```
- **Run**: Use VS Code launch config or:
  ```
  java --module-path src/Dependencies/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml,javafx.base -cp src;bin -Xms1g -Xmx4g app.App
  ```
- **Debugging**: Increase heap size for full dataset. Parse smaller ranges during development.

## Project Conventions
- **File Organization**:
  - `RecipeSets/`: Dataset CSV and processed text files.
  - `TextFiles/`: Intermediate processing outputs (categorized ingredients, counts).
  - `savedData/`: Serialized user data (.ser files).
- **Naming**: ObservableLists prefixed with `ob` (e.g., `obPantryItems`).
- **Persistence**: User data saved via `Serialized.Serializer()` to `savedData/` on app exit.
- **UI Updates**: Use `SceneManager.updatePaneInCurrentScene()` for dynamic pane modifications without reloading scenes.

## Integration Points
- **JavaFX Dependencies**: SDK in `src/Dependencies/javafx-sdk-21/` and `bin/Dependencies/javafx-sdk-21/`.
- **Dataset**: RecipeNLG_dataset.csv loaded as resource stream.
- **Serialization**: Java Object streams for user data persistence.

## Common Patterns
- Singleton controllers accessed via static getters (e.g., `MainSceneController.getController()`).
- Static initialization for data parsing in `MainSceneController`.
- ObservableList conversions for serialization (OLtoAL method).
- Button removal handlers for dynamic UI elements.

## Examples
- Adding a new restriction: Update `obRestrictionsItems`, serialize via `Serialized.Serializer()`.
- Searching recipes: Filter `StaticGenericThings.getGenericRecipeArray()` based on pantry/restrictions criteria.
- Scene navigation: Use `SceneManager` to switch roots without losing state.