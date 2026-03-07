package fr.isen.donnadieu.thegreatestcocktailapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCocktailScreen(drinkId: String = "", onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var cocktail by remember { mutableStateOf<Cocktail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(drinkId) {
        try {
            val response = if (drinkId.isEmpty()) {
                NetworkManager.apiService.getRandomCocktail()
            } else {
                NetworkManager.apiService.getCocktailById(drinkId)
            }
            cocktail = response.drinks?.firstOrNull()
            cocktail?.let {
                isFavorite = FavoritesManager.isFavorite(context, it.id)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show()
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cocktail?.name ?: "Cocktail") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        cocktail?.let { c ->
                            if (isFavorite) {
                                FavoritesManager.removeFavorite(context, c.id)
                                isFavorite = false
                                Toast.makeText(context, "Retiré des favoris", Toast.LENGTH_SHORT).show()
                            } else {
                                FavoritesManager.saveFavorite(context, c)
                                isFavorite = true
                                Toast.makeText(context, "Ajouté aux favoris !", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoris",
                            tint = if (isFavorite) androidx.compose.ui.graphics.Color.Red
                            else androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            cocktail?.let { c ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = c.thumbnail,
                        contentDescription = c.name,
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(c.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        c.category?.let { AssistChip(onClick = {}, label = { Text(it) }) }
                        c.alcoholic?.let { AssistChip(onClick = {}, label = { Text(it) }) }
                    }
                    c.glass?.let { Text("🥂 $it", fontSize = 14.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Ingrédients", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            listOf(
                                c.ingredient1 to c.measure1,
                                c.ingredient2 to c.measure2,
                                c.ingredient3 to c.measure3,
                                c.ingredient4 to c.measure4,
                                c.ingredient5 to c.measure5
                            ).filter { it.first != null }.forEach { (ing, mes) ->
                                IngredientRow(ing!!, mes ?: "")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Recette", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(c.instructions ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientRow(name: String, quantity: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("• $name")
        Text(quantity)
    }
}