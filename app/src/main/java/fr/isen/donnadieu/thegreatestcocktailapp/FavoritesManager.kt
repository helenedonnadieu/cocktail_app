package fr.isen.donnadieu.thegreatestcocktailapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritesManager {
    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorites"
    private val gson = Gson()

    fun saveFavorite(context: Context, cocktail: Cocktail) {
        val favorites = getFavorites(context).toMutableList()
        if (favorites.none { it.id == cocktail.id }) {
            favorites.add(cocktail)
        }
        val json = gson.toJson(favorites)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_FAVORITES, json).apply()
    }

    fun removeFavorite(context: Context, cocktailId: String) {
        val favorites = getFavorites(context).toMutableList()
        favorites.removeAll { it.id == cocktailId }
        val json = gson.toJson(favorites)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_FAVORITES, json).apply()
    }

    fun getFavorites(context: Context): List<Cocktail> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FAVORITES, null) ?: return emptyList()
        val type = object : TypeToken<List<Cocktail>>() {}.type
        return gson.fromJson(json, type)
    }

    fun isFavorite(context: Context, cocktailId: String): Boolean {
        return getFavorites(context).any { it.id == cocktailId }
    }
}