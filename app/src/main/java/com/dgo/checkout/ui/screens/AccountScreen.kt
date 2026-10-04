package com.dgo.checkout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.data.BillingMode
import com.dgo.checkout.data.Catalog
import com.dgo.checkout.data.SessionStatus
import com.dgo.checkout.data.TIER_META
import com.dgo.checkout.data.formatMoney
import com.dgo.checkout.data.formatRenewalDate
import com.dgo.checkout.data.label
import com.dgo.checkout.ui.CheckoutViewModel
import com.dgo.checkout.ui.components.BrandButton
import com.dgo.checkout.ui.components.GhostField
import com.dgo.checkout.ui.theme.BrandPurple
import com.dgo.checkout.ui.theme.Danger
import com.dgo.checkout.ui.theme.Emerald
import com.dgo.checkout.ui.theme.White

private val CANCEL_REASONS = listOf(
    "Too expensive",
    "Not enough to watch",
    "Technical issues",
    "Only needed it temporarily",
    "Other",
)

@Composable
fun AccountScreen(vm: CheckoutViewModel) {
    val session = vm.session
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
    ) {
        Box(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 18.dp)) {
            Chip(Icons.Filled.People, "1", Modifier.align(Alignment.CenterStart))
            Box(
                Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(BrandPurple)
                    .border(2.dp, BrandPurple.copy(0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("U", color = White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }
            Chip(Icons.Filled.Notifications, "2", Modifier.align(Alignment.CenterEnd), dot = true)
        }
        Text("Premium User", color = White, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color(0x66DA2128), RoundedCornerShape(4.dp))
                .background(Color(0x26DA2128))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(if (session != null) "ACTIVE" else "GUEST", color = Color(0xFFDA2128), fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 0.8.sp)
        }
        Text(
            "user@dgo.global",
            color = White.copy(0.40f),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
        )

        Spacer(Modifier.height(18.dp))
        if (session == null) {
            BrandButton("Subscribe now", onClick = { vm.openCheckout(false) }, modifier = Modifier.fillMaxWidth())
        } else {
            Group {
                Column(Modifier.padding(14.dp)) {
                    Text("ACTIVE PLAN", color = White.copy(0.35f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("${TIER_META.getValue(session.tier).name} · ${session.duration.label()}", color = White, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Group {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { vm.plansOpen = !vm.plansOpen }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.LiveTv, null, tint = BrandPurple.copy(0.70f), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(12.dp))
                Text("My Plans", color = White.copy(0.85f), modifier = Modifier.weight(1f))
                Icon(
                    Icons.Filled.ExpandMore,
                    null,
                    tint = White.copy(0.25f),
                    modifier = Modifier.rotate(if (vm.plansOpen) 180f else 0f),
                )
            }
            if (vm.plansOpen) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(White.copy(0.08f)))
                if (session == null) {
                    Text("No active plans on this account", color = White.copy(0.40f), fontSize = 12.sp, modifier = Modifier.padding(16.dp))
                } else {
                    PlanDetails(vm)
                }
            }
            MenuRow(Icons.Outlined.Cast, "TV pairing code") { vm.accountNotice = "TV pairing is in the full app." }
            MenuRow(Icons.Outlined.Settings, "App settings") { vm.accountNotice = "Settings are in the full app." }
            MenuRow(Icons.Outlined.Language, "Language") { vm.accountNotice = "Language follows the device for this prototype." }
        }

        Spacer(Modifier.height(12.dp))
        Group {
            MenuRow(Icons.Outlined.ConfirmationNumber, "Support tickets") { vm.accountNotice = "No open tickets." }
            MenuRow(Icons.Outlined.HelpOutline, "Help") { vm.accountNotice = "Help centre is in the full app." }
            MenuRow(Icons.Outlined.Info, "About") { vm.accountNotice = "DGO checkout prototype · entitlement spec v3.0." }
        }

        vm.accountNotice?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = White.copy(0.55f), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp))
        }

        Spacer(Modifier.height(16.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, White.copy(0.10f), RoundedCornerShape(12.dp))
                .clickable(onClick = vm::signOut)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = White.copy(0.55f), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("SIGN OUT", color = White.copy(0.55f), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun PlanDetails(vm: CheckoutViewModel) {
    val session = vm.session ?: return
    val sku = Catalog.findSku(session.region, session.tier, session.duration)
    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val card = RoundedCornerShape(12.dp)
        Column(
            Modifier
                .fillMaxWidth()
                .clip(card)
                .border(1.dp, White.copy(0.08f), card)
                .background(White.copy(0.03f))
                .padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("${TIER_META.getValue(session.tier).name} · ${session.duration.label()}", color = White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    if (session.billingMode == BillingMode.RECURRING) {
                        Text(
                            if (session.status == SessionStatus.CANCELING) "Cancellation scheduled" else "Auto-renewal on",
                            color = White.copy(0.40f),
                            fontSize = 11.sp,
                        )
                    }
                }
                val ending = session.status == SessionStatus.CANCELING
                Text(
                    if (ending) "ENDS SOON" else "ACTIVE",
                    color = if (ending) Color(0xFFFCD34D) else Emerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background((if (ending) Color(0xFFFCD34D) else Emerald).copy(0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CalendarMonth, null, tint = BrandPurple, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    when {
                        session.billingMode == BillingMode.PREPAID -> "Access until ${formatRenewalDate(session.paidThrough)}"
                        session.status == SessionStatus.CANCELING -> "Access until ${formatRenewalDate(session.nextBillingDate ?: session.paidThrough)}"
                        else -> "Next bill ${formatRenewalDate(session.nextBillingDate)}"
                    },
                    color = White.copy(0.50f),
                    fontSize = 11.sp,
                )
            }
            session.pendingPlan?.let { pending ->
                Spacer(Modifier.height(8.dp))
                Text(
                    "Changes to ${TIER_META.getValue(pending.tier).name} · ${pending.duration.label()} on ${formatRenewalDate(pending.effectiveDate)}",
                    color = White.copy(0.55f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandPurple.copy(0.10f))
                        .padding(10.dp),
                )
            }
        }

        when {
            session.billingMode == BillingMode.PREPAID -> {
                BrandButton(
                    "Add time or upgrade",
                    onClick = { vm.openCheckout(true) },
                    modifier = Modifier.fillMaxWidth(),
                    leading = { Icon(Icons.Outlined.Refresh, null, tint = White, modifier = Modifier.size(14.dp)) },
                )
            }
            session.status == SessionStatus.CANCELING && vm.confirmResume -> {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Emerald.copy(0.20f), RoundedCornerShape(12.dp))
                        .background(Emerald.copy(0.06f))
                        .padding(12.dp),
                ) {
                    Text("Resume auto-renewal?", color = White, fontWeight = FontWeight.Black)
                    Text(
                        "Next charge ${sku?.let { formatMoney(it.price, it.currency) + " " } ?: ""}on ${formatRenewalDate(session.nextBillingDate ?: session.paidThrough)}.",
                        color = White.copy(0.50f),
                        fontSize = 12.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlineButton("Not now", Modifier.weight(1f)) { vm.confirmResume = false }
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF10B981))
                                .clickable(onClick = vm::resumeRenewal)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) { Text("Resume renewal", color = White, fontSize = 12.sp, fontWeight = FontWeight.Black) }
                    }
                }
            }
            session.status == SessionStatus.CANCELING -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Emerald.copy(0.25f), RoundedCornerShape(12.dp))
                        .background(Emerald.copy(0.08f))
                        .clickable { vm.confirmResume = true }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Resume auto-renewal", color = Color(0xFFA7F3D0), fontWeight = FontWeight.Black, fontSize = 12.sp) }
            }
            vm.cancelStep > 0 -> CancelFlow(vm)
            else -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlineButton("Manage plan", Modifier.weight(1f), Icons.Outlined.CreditCard) { vm.openCheckout(true) }
                    OutlineButton("Cancel renewal", Modifier.weight(1f)) { vm.cancelStep = 1 }
                }
            }
        }
    }
}

@Composable
private fun CancelFlow(vm: CheckoutViewModel) {
    val session = vm.session
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Danger.copy(0.20f), RoundedCornerShape(12.dp))
            .background(Danger.copy(0.06f))
            .padding(12.dp),
    ) {
        Row {
            Text("CANCEL RENEWAL", color = Color(0xFFFECACA), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
            Text("Step ${vm.cancelStep} of 3", color = White.copy(0.30f), fontSize = 10.sp)
        }
        Spacer(Modifier.height(8.dp))
        when (vm.cancelStep) {
            1 -> {
                Text("Before you cancel", color = White, fontWeight = FontWeight.Black)
                Text("Access until ${formatRenewalDate(session?.nextBillingDate)}. Resume anytime before.", color = White.copy(0.55f), fontSize = 12.sp)
            }
            2 -> {
                Text("Why are you leaving?", color = White, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                CANCEL_REASONS.forEach { reason ->
                    val on = vm.cancelReason == reason
                    Text(
                        reason,
                        color = if (on) White else White.copy(0.50f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, if (on) BrandPurple.copy(0.50f) else White.copy(0.08f), RoundedCornerShape(8.dp))
                            .background(if (on) BrandPurple.copy(0.15f) else Color.Black.copy(0.15f))
                            .clickable { vm.cancelReason = reason }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }
            else -> {
                Text("Final confirmation", color = White, fontWeight = FontWeight.Black)
                Text("Type CANCEL to confirm.", color = White.copy(0.50f), fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                GhostField(
                    value = vm.cancelPhrase,
                    onValueChange = { vm.cancelPhrase = it.uppercase().filter { ch -> ch.isLetter() }.take(8) },
                    placeholder = "Type CANCEL",
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlineButton(if (vm.cancelStep == 1) "Keep plan" else "Back", Modifier.weight(1f)) {
                if (vm.cancelStep == 1) {
                    vm.cancelStep = 0
                    vm.cancelReason = ""
                    vm.cancelPhrase = ""
                } else {
                    vm.cancelStep -= 1
                }
            }
            val canContinue = vm.cancelStep != 2 || vm.cancelReason.isNotBlank()
            val canConfirm = vm.cancelPhrase.trim() == "CANCEL"
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (vm.cancelStep < 3) White.copy(if (canContinue) 0.12f else 0.04f)
                        else Color(0xFFEF4444).copy(if (canConfirm) 0.85f else 0.25f),
                    )
                    .clickable(enabled = if (vm.cancelStep < 3) canContinue else canConfirm) {
                        if (vm.cancelStep < 3) vm.cancelStep += 1 else vm.cancelRenewal()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (vm.cancelStep < 3) "Continue" else "Turn off renewal",
                    color = White.copy(if ((vm.cancelStep < 3 && canContinue) || canConfirm) 1f else 0.35f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
private fun Group(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, White.copy(0.10f), RoundedCornerShape(16.dp))
            .background(White.copy(0.04f)),
    ) { content() }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(1.dp).background(White.copy(0.08f)))
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = BrandPurple.copy(0.70f), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, color = White.copy(0.85f), modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, null, tint = White.copy(0.20f), modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun Chip(icon: ImageVector, count: String, modifier: Modifier, dot: Boolean = false) {
    Row(
        modifier
            .clip(CircleShape)
            .border(1.dp, White.copy(0.10f), CircleShape)
            .background(White.copy(0.05f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Icon(icon, null, tint = BrandPurple.copy(0.80f), modifier = Modifier.size(14.dp))
            if (dot) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDA2128)),
                )
            }
        }
        Spacer(Modifier.width(4.dp))
        Text(count, color = White.copy(0.55f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OutlineButton(label: String, modifier: Modifier, icon: ImageVector? = null, onClick: () -> Unit) {
    Row(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, White.copy(0.10f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = White.copy(0.65f), modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(label, color = White.copy(0.65f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
