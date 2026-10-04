package com.dgo.checkout.ui.components

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

@Composable
fun DevGeoToggle(
    region: PriceRegion,
    subscribed: Boolean,
    onRegion: (PriceRegion) -> Unit,
    onSubscribed: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(4.dp)
    Column(
        modifier
            .clip(shape)
            .border(1.dp, DevLime.copy(0.40f), shape)
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
