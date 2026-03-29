#  TheGreatestCocktailApp

An Android cocktail application built with Jetpack Compose.

##  Overview

TheGreatestCocktailApp lets you discover, explore and save cocktails
using the [TheCocktailDB](https://www.thecocktaildb.com/) API.

##  Features

-  Discover a new random cocktail every time
-  Browse cocktails by category (Beer, Cocktail, Coffee...)
- Save your favorite cocktails locally
-  Photo, ingredients, glass type and full recipe

##  Tech Stack

- Kotlin- Main language
- Jetpack Compose - UI framework
- Retrofit2 - Network requests
- Gson - JSON parsing
- Coil - Image loading
- Navigation Compose - Screen navigation
- SharedPreferences - Local favorites storage

##  Project Structure
```
app/src/main/java/fr/isen/donnadieu/thegreatestcocktailapp/
├── MainActivity.kt          # Entry point + navigation
├── ApiService.kt            # Retrofit + data models
├── DetailCocktailScreen.kt  # Cocktail detail screen
├── CategoriesScreen.kt      # Categories list screen
├── DrinksListScreen.kt      # Drinks list by category
├── FavoriteScreen.kt        # Favorites screen
└── FavoritesManager.kt      # Favorites manager (SharedPreferences)
```

##  API

[TheCocktailDB](https://www.thecocktaildb.com/api/json/v1/1/)

| Endpoint | Description |
|----------|-------------|
| `random.php` | Random cocktail |
| `list.php?c=list` | List of categories |
| `filter.php?c=CATEGORY` | Drinks by category |
| `lookup.php?i=ID` | Cocktail detail |

##  Getting Started

1. Clone the repository
```bash
git clone https://github.com/donnadieu/thegreatestcocktailapp.git
```
2. Open the project in **Android Studio**
3. Run the app on an emulator or Android device (API 24+)

##  Author

Helene Donnadieu- ISEN 2026
