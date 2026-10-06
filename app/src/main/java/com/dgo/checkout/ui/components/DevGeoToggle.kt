package com.dgo.checkout.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.data.PriceRegion
import com.dgo.checkout.ui.theme.Black
import com.dgo.checkout.ui.theme.DevLime
import com.dgo.checkout.ui.theme.White
import kotlinx.coroutines.delay

private const val AUTO_HIDE_MS = 4000L

@Composable
fun DevGeoToggle(
    region: PriceRegion,
    subscribed: Boolean,
    exclusive: Boolean,
    onRegion: (PriceRegion) -> Unit,
    onSubscribed: (Boolean) -> Unit,
    onExclusive: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var touches by remember { mutableIntStateOf(0) }
    LaunchedEffect(expanded, touches) {
        if (expanded) {
            delay(AUTO_HIDE_MS)
            expanded = false
        }
    }

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(
            visible = expanded,
            enter = expandHorizontally(tween(220), expandFrom = Alignment.Start) + fadeIn(tween(220)),
            exit = shrinkHorizontally(tween(200), shrinkTowards = Alignment.Start) + fadeOut(tween(160)),
        ) {
            Panel(
                region = region,
                subscribed = subscribed,
                exclusive = exclusive,
                onRegion = { touches++; onRegion(it) },
                onSubscribed = { touches++; onSubscribed(it) },
                onExclusive = { touches++; onExclusive(it) },
            )
        }
        Handle(region, subscribed, exclusive, expanded) { expanded = !expanded }
    }
}

@Composable
private fun Handle(region: PriceRegion, subscribed: Boolean, exclusive: Boolean, expanded: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
    Column(
        Modifier
            .clip(shape)
            .border(1.dp, DevLime.copy(0.40f), shape)
            .background(Black.copy(0.85f))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(if (expanded) "‹" else "›", color = DevLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        if (!expanded) {
            Text(region.toggleLabel, color = DevLime.copy(0.90f), fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(if (subscribed) "SUB" else "OFF", color = DevLime.copy(0.60f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            if (exclusive) {
                Text("PPV", color = DevLime.copy(0.60f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun Panel(
    region: PriceRegion,
    subscribed: Boolean,
    exclusive: Boolean,
    onRegion: (PriceRegion) -> Unit,
    onSubscribed: (Boolean) -> Unit,
    onExclusive: (Boolean) -> Unit,
) {
    Column(
        Modifier
            .border(1.dp, DevLime.copy(0.40f))
            .background(Black.copy(0.85f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(DevLime),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "DEV · GEO / STATE",
                color = DevLime.copy(0.90f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp,
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PriceRegion.entries.forEach { option ->
                DevChip(option.toggleLabel, selected = region == option) { onRegion(option) }
            }
            Box(Modifier.width(1.dp).height(12.dp).background(DevLime.copy(0.25f)))
            DevChip("OFF", selected = !subscribed) { onSubscribed(false) }
            DevChip("SUB", selected = subscribed) { onSubscribed(true) }
            Box(Modifier.width(1.dp).height(12.dp).background(DevLime.copy(0.25f)))
            DevChip("PPV", selected = exclusive) { onExclusive(!exclusive) }
        }
    }
}

@Composable
private fun DevChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        Modifier
            .clip(shape)
            .background(if (selected) DevLime else White.copy(0.05f))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (selected) Color.Black else DevLime.copy(0.70f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.6.sp,
        )
    }
}
