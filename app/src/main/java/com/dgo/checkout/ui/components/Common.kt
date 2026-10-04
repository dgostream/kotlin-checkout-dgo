package com.dgo.checkout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.data.AppliedCoupon
import com.dgo.checkout.ui.theme.BrandGradient
import com.dgo.checkout.ui.theme.BrandPurple
import com.dgo.checkout.ui.theme.Danger
import com.dgo.checkout.ui.theme.Emerald
import com.dgo.checkout.ui.theme.White

@Composable
fun BrandButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (enabled) BrandGradient else Brush.linearGradient(listOf(Color(0x14FFFFFF), Color(0x14FFFFFF))))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(8.dp))
            }
            Text(
                label,
                color = if (enabled) White else White.copy(0.35f),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
            )
            if (trailing != null) {
                Spacer(Modifier.width(6.dp))
                trailing()
            }
        }
    }
}

@Composable
fun GhostField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    mono: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .border(1.dp, White.copy(0.10f), shape)
            .background(Color.Black.copy(0.40f))
            .padding(horizontal = 12.dp, vertical = 11.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(placeholder, color = White.copy(0.25f), fontSize = 14.sp)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    cursorBrush = SolidColor(BrandPurple),
                    textStyle = TextStyle(
                        color = White,
                        fontSize = 14.sp,
                        fontFamily = if (mono) FontFamily.Monospace else FontFamily.SansSerif,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (trailing != null) trailing()
        }
    }
}

@Composable
fun CouponField(
    coupon: AppliedCoupon?,
    onApply: (String) -> String?,
    onClear: () -> Unit,
    label: String = "COUPON",
) {
    var value by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.ConfirmationNumber, null, tint = White.copy(0.40f), modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, color = White.copy(0.40f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        }
        Spacer(Modifier.height(8.dp))
        if (coupon != null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Emerald.copy(0.25f), RoundedCornerShape(10.dp))
                    .background(Emerald.copy(0.08f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("${coupon.code} · ${coupon.percent}% off", color = Color(0xFFA7F3D0), fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Remove", color = White.copy(0.45f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onClear(); error = null })
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GhostField(
                    value = value,
                    onValueChange = {
                        value = it.uppercase().filter { ch -> ch.isLetterOrDigit() }.take(16)
                        error = null
                    },
                    placeholder = "Enter code",
                    modifier = Modifier.weight(1f),
                )
                Box(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, White.copy(0.12f), RoundedCornerShape(10.dp))
                        .background(White.copy(0.06f))
                        .clickable {
                            error = onApply(value)
                            if (error == null) value = ""
                        }
                        .padding(horizontal = 12.dp, vertical = 11.dp),
                ) {
                    Text("Apply", color = White.copy(0.80f), fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        if (error != null) {
            Spacer(Modifier.height(6.dp))
            Text(error!!, color = Danger.copy(0.80f), fontSize = 11.sp)
        }
    }
}

@Composable
fun StepDots(step: Int, labels: List<String>) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            labels.forEachIndexed { index, label ->
                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val bg = when {
                        index < step -> BrandGradient
                        index == step -> Brush.linearGradient(listOf(White, White))
                        else -> Brush.linearGradient(listOf(White.copy(0.10f), White.copy(0.10f)))
                    }
                    val fg = when {
                        index < step -> White
                        index == step -> Color.Black
                        else -> White.copy(0.35f)
                    }
                    Box(
                        Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(bg),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (index < step) {
                            Icon(Icons.Filled.Check, null, tint = White, modifier = Modifier.size(12.dp))
                        } else {
                            Text("${index + 1}", color = fg, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Text(
                        label,
                        color = if (index == step) White else White.copy(0.30f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(CircleShape)
                .background(White.copy(0.08f)),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(((step + 1f) / labels.size).coerceIn(0.08f, 1f))
                    .height(2.dp)
                    .clip(CircleShape)
                    .background(BrandGradient),
            )
        }
    }
}

@Composable
fun RadioDot(selected: Boolean, accent: Color, size: Dp = 16.dp) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (selected) Modifier.background(accent)
                else Modifier.border(1.dp, White.copy(0.25f), CircleShape),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) Icon(Icons.Filled.Check, null, tint = White, modifier = Modifier.size(size * 0.55f))
    }
}

@Composable
fun LockHint(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Lock, null, tint = Color(0xFF635BFF), modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, color = White.copy(0.35f), fontSize = 11.sp)
    }
}
