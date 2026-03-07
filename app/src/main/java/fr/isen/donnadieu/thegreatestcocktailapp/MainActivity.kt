package fr.isen.donnadieu.thegreatestcocktailapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.isen.donnadieu.thegreatestcocktailapp.ui.theme.TheGreatestCocktailAppTheme

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Random : Screen("random", "Random", Icons.Default.Refresh)
    object Categories : Screen("categories", "Categories", Icons.Default.List)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
}

val bottomNavItems = listOf(Screen.Random, Screen.Categories, Screen.Favorites)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "onCreate")
        enableEdgeToEdge()
        setContent {
            TheGreatestCocktailAppTheme {
                MainScreen()
            }
        }
    }

    override fun onResume() { super.onResume(); Log.d("Lifecycle", "onResume") }
    override fun onPause() { super.onPause(); Log.d("Lifecycle", "onPause") }
    override fun onStop() { super.onStop(); Log.d("Lifecycle", "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("Lifecycle", "onDestroy") }
    override fun onRestart() { super.onRestart(); Log.d("Lifecycle", "onRestart") }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { _ ->
        NavHost(navController = navController, startDestination = Screen.Random.route) {
            composable(Screen.Random.route) {
                DetailCocktailScreen()
            }
            composable(Screen.Categories.route) {
                CategoriesScreen(onCategoryClick = { category ->
                    val encoded = java.net.URLEncoder.encode(category, "UTF-8")
                    navController.navigate("drinks/$encoded")
                })
            }
            composable("drinks/{category}") { backStackEntry ->
                val category = java.net.URLDecoder.decode(
                    backStackEntry.arguments?.getString("category") ?: "", "UTF-8"
                )
                DrinksListScreen(
                    category = category,
                    onDrinkClick = { drinkId ->
                        navController.navigate("detail/$drinkId")
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("detail/{drinkId}") { backStackEntry ->
                val drinkId = backStackEntry.arguments?.getString("drinkId") ?: ""
                DetailCocktailScreen(
                    drinkId = drinkId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Favorites.route) {
                FavoriteScreen(onDrinkClick = { drinkId ->
                    navController.navigate("detail/$drinkId")
                })
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}