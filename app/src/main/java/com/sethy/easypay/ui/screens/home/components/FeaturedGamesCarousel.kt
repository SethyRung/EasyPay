package com.sethy.easypay.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sethy.easypay.data.model.FeaturedGame
import com.sethy.easypay.design.EasyPayRadius
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Hairline
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.OnDark
import com.sethy.easypay.design.Primary

@Composable
fun FeaturedGamesCarousel(
    games: List<FeaturedGame>,
    onGameClick: (FeaturedGame) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = EasyPaySpacing.md),
        horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.md)
    ) {
        items(games, key = { it.id }) { game ->
            FeaturedGameCard(game = game, onClick = { onGameClick(game) })
        }
    }
}

@Composable
private fun FeaturedGameCard(
    game: FeaturedGame,
    onClick: () -> Unit
) {
    val coverColor = runCatching { Color(android.graphics.Color.parseColor(game.coverColorHex)) }
        .getOrDefault(Muted)

    Surface(
        modifier = Modifier
            .width(240.dp)
            .clip(RoundedCornerShape(EasyPayRadius.lg))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(EasyPayRadius.lg),
        border = androidx.compose.foundation.BorderStroke(1.dp, Hairline),
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(coverColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = game.name.firstOrNull()?.uppercase() ?: "?",
                    style = EasyPayTypography.displayLG,
                    color = OnDark
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(EasyPaySpacing.md)
            ) {
                Text(
                    text = game.name,
                    style = EasyPayTypography.titleSM.copy(fontWeight = FontWeight.SemiBold),
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = game.category,
                        style = EasyPayTypography.caption,
                        color = Muted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$%.2f".format(game.priceMajor),
                        style = EasyPayTypography.titleSM,
                        color = Primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun FeaturedGamesCarouselPreview() {
    EasyPayTheme {
        FeaturedGamesCarousel(
            games = listOf(
                FeaturedGame("1", "Elden Ring", 59.99, "USD", "Action RPG", "#5A4A2E"),
                FeaturedGame("2", "Hades", 24.99, "USD", "Roguelike", "#5C2E2E")
            ),
            onGameClick = {}
        )
    }
}
