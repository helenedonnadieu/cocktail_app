package fr.isen.donnadieu.thegreatestcocktailapp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ── Palette (identique aux autres écrans) ─────────────────────
private val BgPage    = Color(0xFFE8E0D5)
private val BgCard    = Color(0xFFFFFFFF)
private val BgSection = Color(0xFFF0EAE1)
private val Accent    = Color(0xFF8B7355)
private val TextMain  = Color(0xFF2C2416)
private val TextSub   = Color(0xFFAA9E8F)
private val Stroke    = Color(0xFFE5DDD4)

@Composable
fun DetailCocktailScreen(drinkId: String = "", onBack: () -> Unit = {}) {
    val context   = LocalContext.current
    var cocktail  by remember { mutableStateOf<Cocktail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(drinkId) {
        try {
            val response = if (drinkId.isEmpty()) NetworkManager.apiService.getRandomCocktail()
            else NetworkManager.apiService.getCocktailById(drinkId)
            cocktail = response.drinks?.firstOrNull()
            cocktail?.let { isFavorite = FavoritesManager.isFavorite(context, it.id) }
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show()
        }
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent, strokeWidth = 2.dp)
            }
        } else {
            cocktail?.let { c ->
                Box(modifier = Modifier.fillMaxSize()) {

                    // ── 1. IMAGE ──────────────────────────────
                    AsyncImage(
                        model              = c.thumbnail,
                        contentDescription = c.name,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    )

                    // Dégradé haut pour boutons lisibles
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(0.25f), Color.Transparent)
                                )
                            )
                    )

                    // ── 2. CONTENU SCROLLABLE ─────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(360.dp))

                        // Panneau beige arrondi
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                            color    = BgPage
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {

                                // Titre + sous-titre
                                Text(
                                    text          = "BOISSONS",
                                    color         = Accent,
                                    fontSize      = 10.sp,
                                    fontWeight    = FontWeight.Bold,
                                    letterSpacing = 3.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text          = c.name,
                                    fontSize      = 28.sp,
                                    fontWeight    = FontWeight.ExtraBold,
                                    color         = TextMain,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text     = "${c.category ?: ""} • ${c.glass ?: ""}",
                                    fontSize = 13.sp,
                                    color    = TextSub
                                )

                                HorizontalDivider(
                                    modifier  = Modifier.padding(vertical = 24.dp),
                                    thickness = 1.dp,
                                    color     = Stroke
                                )

                                // ── Ingrédients ───────────────
                                Text(
                                    text       = "INGRÉDIENTS",
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Accent,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Card(
                                    modifier  = Modifier.fillMaxWidth(),
                                    shape     = RoundedCornerShape(16.dp),
                                    colors    = CardDefaults.cardColors(containerColor = BgCard),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        val ingredients = listOf(
                                            c.ingredient1 to c.measure1,
                                            c.ingredient2 to c.measure2,
                                            c.ingredient3 to c.measure3,
                                            c.ingredient4 to c.measure4,
                                            c.ingredient5 to c.measure5
                                        ).filter { !it.first.isNullOrEmpty() }

                                        ingredients.forEachIndexed { index, (ing, mes) ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 14.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment     = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text       = ing!!,
                                                    fontWeight = FontWeight.Medium,
                                                    color      = TextMain,
                                                    fontSize   = 15.sp
                                                )
                                                Text(
                                                    text     = mes ?: "",
                                                    color    = TextSub,
                                                    fontSize = 13.sp
                                                )
                                            }
                                            if (index < ingredients.lastIndex) {
                                                HorizontalDivider(
                                                    thickness = 1.dp,
                                                    color     = Stroke
                                                )
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(
                                    modifier  = Modifier.padding(vertical = 24.dp),
                                    thickness = 1.dp,
                                    color     = Stroke
                                )

                                // ── Recette ───────────────────
                                Text(
                                    text          = "RECETTE",
                                    fontSize      = 11.sp,
                                    fontWeight    = FontWeight.Bold,
                                    color         = Accent,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text       = c.instructions ?: "",
                                    fontSize   = 15.sp,
                                    lineHeight  = 26.sp,
                                    color      = TextMain.copy(alpha = 0.85f)
                                )

                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }

                    // ── 3. BOUTONS FLOTTANTS ──────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick  = onBack,
                            modifier = Modifier
                                .background(BgPage.copy(alpha = 0.85f), CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, "Retour", tint = TextMain)
                        }

                        IconButton(
                            onClick = {
                                cocktail?.let { drink ->
                                    if (isFavorite) {
                                        FavoritesManager.removeFavorite(context, drink.id)
                                        isFavorite = false
                                        Toast.makeText(context, "Retiré des favoris", Toast.LENGTH_SHORT).show()
                                    } else {
                                        FavoritesManager.saveFavorite(context, drink)
                                        isFavorite = true
                                        Toast.makeText(context, "Ajouté aux favoris !", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .background(BgPage.copy(alpha = 0.85f), CircleShape)
                        ) {
                            Icon(
                                imageVector        = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favoris",
                                tint               = if (isFavorite) Accent else TextMain
                            )
                        }
                    }
                }
            }
        }
    }
}