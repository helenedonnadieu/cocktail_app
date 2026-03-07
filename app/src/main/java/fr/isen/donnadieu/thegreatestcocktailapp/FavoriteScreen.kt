package fr.isen.donnadieu.thegreatestcocktailapp

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ── Palette (identique aux autres écrans) ─────────────────────
private val BgPage   = Color(0xFFE8E0D5)
private val BgCard   = Color(0xFFFFFFFF)
private val Accent   = Color(0xFF8B7355)
private val TextMain = Color(0xFF2C2416)
private val TextSub  = Color(0xFFAA9E8F)
private val Stroke   = Color(0xFFE5DDD4)

@Composable
fun FavoriteScreen(onDrinkClick: (String) -> Unit) {
    val context   = LocalContext.current
    var favorites by remember { mutableStateOf<List<Cocktail>>(emptyList()) }

    // Recharge les favoris à chaque fois que l'écran devient visible
    LaunchedEffect(Unit) {
        favorites = FavoritesManager.getFavorites(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        // ── Header centré ─────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 52.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text          = "MA SÉLECTION",
                color         = Accent,
                fontSize      = 10.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text          = "Favoris",
                color         = TextMain,
                fontSize      = 32.sp,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = "${favorites.size} cocktails sauvegardés",
                color    = TextSub,
                fontSize = 13.sp
            )
        }

        HorizontalDivider(
            modifier  = Modifier.padding(horizontal = 24.dp),
            thickness = 1.dp,
            color     = Stroke
        )
        Spacer(modifier = Modifier.height(12.dp))

        // ── Liste vide ────────────────────────────────────────
        if (favorites.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🍸", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text       = "Aucun favori pour l'instant",
                        color      = TextSub,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign  = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text      = "Ajoutez des cocktails depuis\nleur page de détail",
                        color     = TextSub.copy(alpha = 0.6f),
                        fontSize  = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start  = 16.dp,
                    end    = 16.dp,
                    bottom = 24.dp + WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(favorites) { index, drink ->
                    AnimatedFavoriteCard(
                        drink   = drink,
                        index   = index,
                        onClick = { onDrinkClick(drink.id) }
                    )
                }
            }
        }
    }
}

// ── Carte favori animée ───────────────────────────────────────
@Composable
fun AnimatedFavoriteCard(drink: Cocktail, index: Int, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(250)) + slideInVertically(
            animationSpec  = tween(250, easing = EaseOutCubic),
            initialOffsetY = { it / 4 }
        )
    ) {
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { onClick() },
            shape     = RoundedCornerShape(14.dp),
            colors    = CardDefaults.cardColors(containerColor = BgCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model              = drink.thumbnail,
                    contentDescription = drink.name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text       = drink.name,
                    color      = TextMain,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.weight(1f),
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    text       = "›",
                    color      = Accent,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}