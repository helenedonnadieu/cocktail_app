package fr.isen.donnadieu.thegreatestcocktailapp

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Palette neutre / beige ────────────────────────────────────
private val BgPage   = Color(0xFFE8E0D5)
private val BgCard   = Color(0xFFFFFFFF)
private val Accent   = Color(0xFF8B7355)
private val TextMain = Color(0xFF2C2416)
private val TextSub  = Color(0xFFAA9E8F)
private val Stroke   = Color(0xFFE5DDD4)

// ── Écran principal ───────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(onCategoryClick: (String) -> Unit) {
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading  by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = NetworkManager.apiService.getCategories()
            categories = response.drinks ?: emptyList()
            Log.d("DEBUG", "Categories loaded: ${categories.size}")
        } catch (e: Exception) { e.printStackTrace() }
        isLoading = false
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
                text = "BOISSONS",
                color = Accent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Catégories",
                color = TextMain,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${categories.size} types disponibles",
                color = TextSub,
                fontSize = 13.sp
            )
        }

        // Ligne décorative fine
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
                itemsIndexed(categories) { index, category ->
                    AnimatedCategoryCard(
                        name    = category.name,
                        index   = index,
                        onClick = {
                            Log.d("DEBUG", "Clicked: ${category.name}")
                            onCategoryClick(category.name)
                        }
                    )
                }
            }
        }
    }
}

// ── Carte animée ──────────────────────────────────────────────
@Composable
fun AnimatedCategoryCard(name: String, index: Int, onClick: () -> Unit) {
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
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = name,
                    color      = TextMain,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign  = TextAlign.Center,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
            }
        }
    }
}