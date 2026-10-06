package com.dgo.checkout.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.data.PlanChangeKind
import com.dgo.checkout.data.TIER_META
import com.dgo.checkout.data.billingCadenceLabel
import com.dgo.checkout.data.formatMoney
import com.dgo.checkout.data.formatRenewalDate
import com.dgo.checkout.data.label
import com.dgo.checkout.ui.CheckoutViewModel
import com.dgo.checkout.ui.components.BrandButton
import com.dgo.checkout.ui.theme.BrandGradient
import com.dgo.checkout.ui.theme.BrandPink
import com.dgo.checkout.ui.theme.BrandPurple
import com.dgo.checkout.ui.theme.Surface
import com.dgo.checkout.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun ConfirmationScreen(vm: CheckoutViewModel) {
    var seconds by remember { mutableIntStateOf(10) }
    LaunchedEffect(Unit) {
        repeat(10) {
            delay(1000)
            seconds -= 1
        }
        vm.goHome()
    }
    val pass = vm.completedEvent
    val sku = if (pass == null) vm.sku else null
    val kind = vm.completedKind
    val title = when {
        pass != null -> "Pass unlocked"
        kind == PlanChangeKind.PROVIDER_DOWNGRADE -> "Plan change scheduled"
        kind == PlanChangeKind.PROVIDER_UPGRADE || kind == PlanChangeKind.FIXED_TIER_UPGRADE -> "Plan updated"
        kind == PlanChangeKind.RENEWAL || kind == PlanChangeKind.IMMEDIATE_EXTENSION -> "Time added"
        else -> "You're in"
    }
    val detail = when {
        pass != null -> "${pass.title} · ${pass.subtitle}"
        sku != null -> "${TIER_META.getValue(sku.tier).name} · ${sku.duration.label()}"
        else -> ""
    }
    val progress by animateFloatAsState(seconds / 10f, animationSpec = tween(900), label = "ring")

    Box(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .align(Alignment.TopStart)
                .size(80.dp)
                .background(BrandPurple.copy(0.18f), CircleShape),
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(70.dp)
                .background(BrandPink.copy(0.10f), CircleShape),
        )
        val card = RoundedCornerShape(24.dp)
        Column(
            Modifier
                .fillMaxWidth()
                .clip(card)
                .border(1.dp, White.copy(0.10f), card)
                .background(Surface)
                .padding(horizontal = 22.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(72.dp)) {
                    drawCircle(color = White.copy(0.08f), style = Stroke(width = 4.dp.toPx()))
                    drawArc(
                        brush = BrandGradient,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BrandGradient),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Check, null, tint = White, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(title, color = White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text(detail, color = White.copy(0.50f), fontSize = 14.sp)
            if (kind == PlanChangeKind.PROVIDER_DOWNGRADE && vm.session != null) {
                Text(
                    "Starts ${formatRenewalDate(vm.session!!.nextBillingDate ?: vm.session!!.paidThrough)}",
                    color = White.copy(0.35f),
                    fontSize = 12.sp,
                )
            }
            Spacer(Modifier.height(20.dp))
            val box = RoundedCornerShape(12.dp)
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(box)
                    .border(1.dp, White.copy(0.08f), box)
                    .background(White.copy(0.03f)),
            ) {
                ConfirmRow(
                    "Paid today",
                    when (kind) {
                        PlanChangeKind.PROVIDER_DOWNGRADE -> "No charge today"
                        PlanChangeKind.PROVIDER_UPGRADE -> "Price difference"
                        else -> formatMoney(vm.dueAmount, sku?.currency ?: vm.region.currency)
                    },
                )
                Box(Modifier.fillMaxWidth().height(1.dp).background(White.copy(0.08f)))
                ConfirmRow("Payment", vm.lastPaymentLabel)
                if (pass != null) {
                    Box(Modifier.fillMaxWidth().height(1.dp).background(White.copy(0.08f)))
                    ConfirmRow("Access", "Until ${formatRenewalDate(pass.accessUntil)} · no renewal")
                }
                if (sku != null) {
                    Box(Modifier.fillMaxWidth().height(1.dp).background(White.copy(0.08f)))
                    ConfirmRow(
                        if (sku.region == com.dgo.checkout.data.PriceRegion.NEPAL) "Access" else "Schedule",
                        when (kind) {
                            PlanChangeKind.FIXED_TIER_UPGRADE -> "Access date unchanged"
                            PlanChangeKind.PROVIDER_DOWNGRADE -> "Starts on the next bill"
                            PlanChangeKind.PROVIDER_UPGRADE -> "Next bill ${formatRenewalDate(vm.session?.nextBillingDate)}"
                            PlanChangeKind.RENEWAL, PlanChangeKind.IMMEDIATE_EXTENSION -> "${sku.duration.label()} added after this term"
                            else -> if (sku.region == com.dgo.checkout.data.PriceRegion.NEPAL) {
                                "${sku.duration.label()} of access"
                            } else {
                                billingCadenceLabel(sku.duration, sku.region)
                            }
                        },
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                vm.orderRef,
                color = White.copy(0.30f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, White.copy(0.08f), RoundedCornerShape(50))
                    .background(White.copy(0.04f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(24.dp))
            BrandButton(
                label = "Go to home",
                onClick = vm::goHome,
                modifier = Modifier.fillMaxWidth(),
                leading = { Icon(Icons.Filled.PlayArrow, null, tint = White, modifier = Modifier.size(16.dp)) },
            )
            Spacer(Modifier.height(10.dp))
            Text("Home in ${seconds}s", color = White.copy(0.35f), fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ConfirmRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(label, color = White.copy(0.40f), fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(value, color = White.copy(0.80f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
