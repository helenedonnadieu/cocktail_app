package fr.isen.donnadieu.thegreatestcocktailapp

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ── Palette (identique à CategoriesScreen) ───────────────────
private val BgPage   = Color(0xFFE8E0D5)
private val BgCard   = Color(0xFFFFFFFF)
private val Accent   = Color(0xFF8B7355)
private val TextMain = Color(0xFF2C2416)
private val TextSub  = Color(0xFFAA9E8F)
private val Stroke   = Color(0xFFE5DDD4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinksListScreen(category: String, onDrinkClick: (String) -> Unit, onBack: () -> Unit) {
    var drinks    by remember { mutableStateOf<List<Cocktail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(category) {
        try {
            val response = NetworkManager.apiService.getDrinksByCategory(category)
            drinks = response.drinks ?: emptyList()
        } catch (e: Exception) { e.printStackTrace() }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        // ── Header ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 24.dp, top = 52.dp, bottom = 20.dp)
        ) {
            // Bouton retour à gauche
            IconButton(
                onClick  = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector        = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint               = Accent
                )
            }

            // Titre centré
            Column(
                modifier            = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text          = "BOISSONS",
                    color         = Accent,
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text          = category,
                    color         = TextMain,
                    fontSize      = 26.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text      = "${drinks.size} cocktails",
                    color     = TextSub,
                    fontSize  = 13.sp
                )
            }
        }

        // Ligne décorative
        HorizontalDivider(
            modifier  = Modifier.padding(horizontal = 24.dp),
            thickness = 1.dp,
            color     = Stroke
        )
        Spacer(modifier = Modifier.height(12.dp))

        // ── Liste ─────────────────────────────────────────────
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent, strokeWidth = 2.dp)
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
                itemsIndexed(drinks) { index, drink ->
                    AnimatedDrinkCard(
                        drink   = drink,
                        index   = index,
                        onClick = { onDrinkClick(drink.id) }
                    )
                }
            }
        }
    }
}

// ── Carte boisson animée ──────────────────────────────────────
@Composable
fun AnimatedDrinkCard(drink: Cocktail, index: Int, onClick: () -> Unit) {
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
                // Photo arrondie
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