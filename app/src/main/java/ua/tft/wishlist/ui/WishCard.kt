package ua.tft.wishlist.ui

import android.graphics.Paint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ua.tft.wishlist.model.WishItem
import ua.tft.wishlist.util.formatPrice

@Composable
fun WishCard(
    item: WishItem,
    onClick: () -> Unit,
    onToggleFulfilled: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = holidayThemeFor(item.holiday)
    val cardShape = RoundedCornerShape(22.dp)

    val borderColor by animateColorAsState(
        targetValue = if (item.isFulfilled) WishColors.SuccessGreen.copy(alpha = 0.5f) else theme.border,
        label = "borderColor"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (item.isFulfilled) 0.55f else 1f,
        label = "contentAlpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .clickable { onClick() },
        shape = cardShape,
        color = theme.cardBg,
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Анімований тайловий фон з прозорих емодзі
            HolidayEmojiTileBackground(
                emojis = theme.emojis,
                motion = theme.motion,
                modifier = Modifier.matchParentSize()
            )

            // Контент картки
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(contentAlpha),
                    verticalAlignment = Alignment.Top
                ) {
                    // Фото товару
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(WishColors.Background)
                            .border(1.dp, theme.accent.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.imageUri.isNotBlank()) {
                            AsyncImage(
                                model = item.imageUri,
                                contentDescription = item.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.CardGiftcard,
                                contentDescription = null,
                                tint = theme.accent,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Назва + опис
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.holiday,
                                color = theme.accent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = item.title,
                            color = WishColors.TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (item.isFulfilled) TextDecoration.LineThrough else TextDecoration.None
                        )

                        if (item.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                color = WishColors.TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Нижня панель: ціна + кнопки дій
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Ціна в контрастному блоці
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(WishColors.Background.copy(alpha = 0.75f))
                            .border(
                                1.dp,
                                (if (item.isFulfilled) WishColors.SuccessGreen else theme.accent).copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = formatPrice(item.price),
                            color = if (item.isFulfilled) WishColors.SuccessGreen else theme.accent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Кнопка виконання
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (item.isFulfilled) WishColors.SuccessGreen
                                    else WishColors.Background.copy(alpha = 0.75f)
                                )
                                .border(
                                    1.dp,
                                    if (item.isFulfilled) WishColors.SuccessGreen else WishColors.BorderSubtle,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onToggleFulfilled() }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Виконати",
                                tint = if (item.isFulfilled) WishColors.Background else WishColors.TextPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (item.isFulfilled) "Збулося" else "Виконати",
                                color = if (item.isFulfilled) WishColors.Background else WishColors.TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Кнопка видалення
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WishColors.Background.copy(alpha = 0.75f))
                                .border(1.dp, WishColors.BorderSubtle, RoundedCornerShape(10.dp))
                                .clickable { onDelete() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "Видалити",
                                tint = WishColors.DangerRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HolidayEmojiTileBackground(
    emojis: List<String>,
    motion: HolidayMotion,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "holidayBg")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (motion == HolidayMotion.PULSE) 1600 else 9000,
                easing = if (motion == HolidayMotion.PULSE) FastOutSlowInEasing else LinearEasing
            ),
            repeatMode = if (motion == HolidayMotion.PULSE) RepeatMode.Reverse else RepeatMode.Restart
        ),
        label = "tileProgress"
    )

    val density = LocalDensity.current
    val emojiSizePx = with(density) { 22.dp.toPx() }
    val stepPx = with(density) { 54.dp.toPx() }

    val textPaint = remember(emojiSizePx) {
        Paint().apply {
            textSize = emojiSizePx
            isAntiAlias = true
        }
    }

    Canvas(modifier = modifier) {
        val shiftX = when (motion) {
            HolidayMotion.DRIFT -> progress * stepPx
            else -> 0f
        }
        val shiftY = when (motion) {
            HolidayMotion.FALL_DOWN -> progress * stepPx
            HolidayMotion.FLOAT_UP -> -progress * stepPx
            HolidayMotion.DRIFT -> progress * stepPx * 0.5f
            HolidayMotion.PULSE -> 0f
        }

        val cols = (size.width / stepPx).toInt() + 3
        val rows = (size.height / stepPx).toInt() + 3

        drawIntoCanvas { canvas ->
            for (row in -1..rows) {
                val stagger = if (row % 2 == 0) 0f else stepPx / 2f
                for (col in -1..cols) {
                    val emoji = emojis[((row + 10) * 3 + (col + 10)) % emojis.size]
                    val x = col * stepPx + stagger + shiftX - stepPx
                    val y = row * stepPx + shiftY

                    if (motion == HolidayMotion.PULSE) {
                        val localProgress = if ((row + col) % 2 == 0) progress else 1f - progress
                        textPaint.alpha = (14 + (localProgress * 76)).toInt() // ~5% .. 35%
                        textPaint.textSize = emojiSizePx * (0.85f + localProgress * 0.35f)
                    } else {
                        textPaint.alpha = 32
                        textPaint.textSize = emojiSizePx
                    }

                    canvas.nativeCanvas.drawText(emoji, x, y, textPaint)
                }
            }
        }
    }
}