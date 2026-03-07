package fr.isen.donnadieu.thegreatestcocktailapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun FavoriteScreen(onDrinkClick: (String) -> Unit) {
    val context = LocalContext.current
    var favorites by remember { mutableStateOf<List<Cocktail>>(emptyList()) }

    LaunchedEffect(Unit) {
        favorites = FavoritesManager.getFavorites(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Favoris ❤️") })
        }
    ) { innerPadding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun favori pour l'instant !", fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(favorites) { drink ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDrinkClick(drink.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = drink.thumbnail,
                            contentDescription = drink.name,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(drink.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}