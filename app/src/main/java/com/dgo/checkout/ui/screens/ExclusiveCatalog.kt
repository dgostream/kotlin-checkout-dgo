package com.dgo.checkout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.data.CatalogTab
import com.dgo.checkout.data.EventPass
import com.dgo.checkout.data.Events
import com.dgo.checkout.data.PassOwnership
import com.dgo.checkout.data.formatMoney
import com.dgo.checkout.data.formatRenewalDate
import com.dgo.checkout.data.passOwnership
import com.dgo.checkout.ui.CheckoutViewModel
import com.dgo.checkout.ui.components.BrandButton
import com.dgo.checkout.ui.theme.Emerald
import com.dgo.checkout.ui.theme.White
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
internal fun CatalogTabs(selected: CatalogTab, onSelect: (CatalogTab) -> Unit) {
    val shape = RoundedCornerShape(50)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, White.copy(0.10f), shape)
            .background(Color(0xFF121212))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TabCell("Plans", Icons.Outlined.LiveTv, selected == CatalogTab.PLANS, Modifier.weight(1f)) { onSelect(CatalogTab.PLANS) }
        TabCell("Exclusive", Icons.Outlined.ConfirmationNumber, selected == CatalogTab.EXCLUSIVE, Modifier.weight(1f)) {
            onSelect(CatalogTab.EXCLUSIVE)
        }
    }
}

@Composable
private fun TabCell(label: String, icon: ImageVector, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier
            .clip(shape)
            .background(if (active) White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = if (active) Color.Black else White.copy(0.55f), modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = if (active) Color.Black else White.copy(0.65f), fontSize = 13.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
internal fun ExclusiveCatalog(vm: CheckoutViewModel) {
    Spacer(Modifier.height(18.dp))
    Text(
        "Exclusive events",
        color = White,
        fontSize = 34.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.7).sp,
        lineHeight = 38.sp,
    )
    Spacer(Modifier.height(8.dp))
    Text("One-time passes. No subscription needed.", color = White.copy(0.50f), fontSize = 14.sp)
    Spacer(Modifier.height(20.dp))

    val events = Events.ALL
    val start = events.indexOfFirst { it.key == vm.eventKey }.coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = start, pageCount = { events.size })
    val scope = rememberCoroutineScope()
    LaunchedEffect(vm.eventKey) {
        val page = events.indexOfFirst { it.key == vm.eventKey }
        if (page >= 0 && pagerState.currentPage != page) pagerState.scrollToPage(page)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .drop(1)
            .collect { page ->
                val next = events[page].key
                if (vm.eventKey != next) vm.eventKey = next
            }
    }
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 8.dp),
        pageSpacing = 12.dp,
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth(),
    ) { page ->
        EventCard(events[page], vm, active = pagerState.currentPage == page)
    }
    Spacer(Modifier.height(10.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        events.forEachIndexed { index, event ->
            val on = pagerState.currentPage == index
            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (on) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (on) Color(event.accent) else White.copy(0.25f))
                    .clickable { scope.launch { pagerState.animateScrollToPage(index) } },
            )
        }
    }
    Text(
        "Works with or without a plan",
        color = White.copy(0.35f),
        fontSize = 11.sp,
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun EventCard(event: EventPass, vm: CheckoutViewModel, active: Boolean) {
    val accent = Color(event.accent)
    val ownership = passOwnership(event, vm.ownedPasses)
    val shape = RoundedCornerShape(28.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, if (active) accent.copy(0.55f) else White.copy(0.08f), shape)
            .background(Color(0xFF09090B)),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    Brush.linearGradient(
                        listOf(accent.copy(if (active) 0.70f else 0.35f), Color(0xFF1A0B2E), Color(0xFF09090B)),
                    ),
                ),
        ) {
            Icon(
                Icons.Filled.EmojiEvents,
                null,
                tint = White.copy(0.12f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 18.dp)
                    .size(150.dp),
            )
            Row(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Pill(event.league, White.copy(0.16f), White)
                Pill("LIVE EVENT", Color.Black.copy(0.35f), White.copy(0.85f))
            }
            if (ownership != null) {
                Pill(
                    if (ownership == PassOwnership.OWNED) "Owned" else "Included",
                    Emerald.copy(0.20f),
                    Color(0xFF6EE7B7),
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                )
            }
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 18.dp, vertical = 16.dp),
            ) {
                Text(event.title, color = White, fontSize = 30.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.8).sp)
                Text(event.subtitle, color = White.copy(0.80f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column(Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    formatMoney(event.price(vm.region), vm.region.currency),
                    color = White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.8).sp,
                )
                Spacer(Modifier.width(8.dp))
                Pill("One-time", White.copy(0.08f), White.copy(0.70f), Modifier.padding(bottom = 8.dp))
            }
            Text(
                "${event.window} · Access until ${formatRenewalDate(event.accessUntil)}",
                color = White.copy(0.45f),
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(14.dp))
            event.includes.forEach { line ->
                Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Check, null, tint = accent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(line, color = White.copy(0.70f), fontSize = 13.sp)
                }
            }
            Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Check, null, tint = accent.copy(0.35f), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(8.dp))
                Text("No renewal, no subscription", color = White.copy(0.40f), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun Pill(text: String, background: Color, color: Color, modifier: Modifier = Modifier) {
    Text(
        text,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.8.sp,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
internal fun EventBottomBar(vm: CheckoutViewModel) {
    val event = vm.event
    val ownership = event?.let { passOwnership(it, vm.ownedPasses) }
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(0.90f))
            .border(1.dp, White.copy(0.10f))
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(event?.title ?: "Choose an event", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(
                event?.let { "${it.subtitle} · ${formatMoney(it.price(vm.region), vm.region.currency)}" } ?: "",
                color = White.copy(0.40f),
                fontSize = 11.sp,
                maxLines = 1,
            )
        }
        BrandButton(
            label = when (ownership) {
                PassOwnership.OWNED -> "Owned"
                PassOwnership.INCLUDED -> "Included"
                null -> "Buy pass"
            },
            onClick = vm::nextFromPlan,
            enabled = vm.canAdvanceFromPlan,
        )
    }
}
