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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.data.Catalog
import com.dgo.checkout.data.PlanChangeKind
import com.dgo.checkout.data.SessionStatus
import com.dgo.checkout.data.PlanDuration
import com.dgo.checkout.data.PlanTier
import com.dgo.checkout.data.TIER_META
import com.dgo.checkout.data.billingCadenceLabel
import com.dgo.checkout.data.compareAtPrice
import com.dgo.checkout.data.dueTodayCaption
import com.dgo.checkout.data.formatMoney
import com.dgo.checkout.data.formatMonthlyRate
import com.dgo.checkout.data.label
import com.dgo.checkout.data.lifecycleNote
import com.dgo.checkout.data.months
import com.dgo.checkout.data.planActionLabel
import com.dgo.checkout.data.planStateLabel
import com.dgo.checkout.data.resolvePlanChange
import com.dgo.checkout.data.savingsVsMonthly
import com.dgo.checkout.data.offerSavings
import com.dgo.checkout.ui.CheckoutViewModel
import com.dgo.checkout.ui.components.BrandButton
import com.dgo.checkout.ui.components.NepalPlanNotice
import com.dgo.checkout.data.formatRenewalDate
import com.dgo.checkout.ui.theme.BrandGradient
import com.dgo.checkout.ui.theme.BrandPurple
import com.dgo.checkout.ui.theme.Emerald
import com.dgo.checkout.data.BillingMode
import com.dgo.checkout.ui.theme.White
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
fun ChoosePlanScreen(vm: CheckoutViewModel) {
    val selected = vm.sku
    val change = vm.planChange
    val selectedCurrent = (vm.manageMode || vm.nepalPlanLocked) && selected != null && vm.session?.skuId == selected.id
    var showNepalNotice by rememberSaveable(vm.region, vm.session?.skuId) { mutableStateOf(false) }

    if (showNepalNotice) {
        AlertDialog(
            onDismissRequest = { showNepalNotice = false },
            title = { Text("You have an active plan") },
            text = { NepalPlanNotice() },
            confirmButton = {
                TextButton(onClick = { showNepalNotice = false }) { Text("Keep browsing") }
            },
            containerColor = com.dgo.checkout.ui.theme.Ink,
        )
    }

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            val badge = RoundedCornerShape(50)
            Box(
                Modifier
                    .clip(badge)
                    .border(1.dp, White.copy(0.10f), badge)
                    .background(White.copy(0.04f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(vm.region.billedIn, color = White.copy(0.50f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            if (vm.exclusiveEnabled) {
                Spacer(Modifier.height(14.dp))
                CatalogTabs(vm.catalogTab) { vm.catalogTab = it }
            }
            if (vm.buyingEvent) {
                ExclusiveCatalog(vm)
                return@Column
            }
            Spacer(Modifier.height(14.dp))
            Text(
                when {
                    vm.nepalPlanLocked -> "Explore plans"
                    vm.manageMode && vm.session != null -> "Change your plan"
                    vm.manageMode -> "Choose your plan"
                    else -> "Choose your plan"
                },
                color = White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.7).sp,
                lineHeight = 38.sp,
            )
            Spacer(Modifier.height(8.dp))
            if ((vm.manageMode || vm.nepalPlanLocked) && vm.session != null) {
                Text(
                    "Current: ${TIER_META.getValue(vm.session!!.tier).name} · ${vm.session!!.duration.label()}",
                    color = White.copy(0.50f),
                    fontSize = 14.sp,
                )
            } else {
                Text("Mobile for phones. Plus adds TV.", color = White.copy(0.50f), fontSize = 14.sp)
            }

            Spacer(Modifier.height(22.dp))
            DurationTabs(
                region = vm.region,
                tier = vm.tier,
                selected = vm.duration,
                onSelect = { vm.duration = it },
            )
            Spacer(Modifier.height(18.dp))
            PlanPager(vm)

            if (vm.manageMode && vm.session?.billingMode == BillingMode.RECURRING && vm.session?.status == SessionStatus.CANCELING) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Renewal is off. A new plan turns it back on.",
                    color = Color(0xFFFCD34D),
                    fontSize = 12.sp,
                )
            }
            if (vm.manageMode && vm.session != null) {
                val shape = RoundedCornerShape(16.dp)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .border(1.dp, White.copy(0.10f), shape)
                        .background(White.copy(0.04f))
                        .padding(14.dp),
                ) {
                    Icon(Icons.Outlined.Info, null, tint = BrandPurple, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (vm.nepalPlanLocked) "Your current plan is active until ${formatRenewalDate(vm.session!!.paidThrough)}."
                        else lifecycleNote(vm.session!!, change),
                        color = White.copy(0.50f), fontSize = 12.sp, lineHeight = 18.sp,
                    )
                }
            }
        }

        if (vm.buyingEvent) {
            EventBottomBar(vm)
            return@Column
        }
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
                Text(
                    selected?.let { TIER_META.getValue(it.tier).name } ?: "Choose a plan",
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Text(
                    selected?.let { sku ->
                        if (vm.nepalPlanLocked) return@let "${sku.duration.label()} · ${formatMoney(sku.price, sku.currency)}"
                        val due = dueTodayCaption(change, vm.amount, sku.currency)
                        val showDue = change?.kind in setOf(
                            PlanChangeKind.PROVIDER_UPGRADE,
                            PlanChangeKind.PROVIDER_DOWNGRADE,
                            PlanChangeKind.SUPPORT_REQUIRED,
                            PlanChangeKind.CURRENT,
                        )
                        if (showDue) "${sku.duration.label()} · $due" else "${sku.duration.label()} · ${formatMoney(vm.amount, sku.currency)}"
                    } ?: "Select a package",
                    color = White.copy(0.40f),
                    fontSize = 11.sp,
                )
            }
            BrandButton(
                label = if (vm.nepalPlanLocked && !selectedCurrent) "Continue" else planActionLabel(change, selectedCurrent),
                onClick = {
                    if (vm.nepalPlanLocked) showNepalNotice = true else vm.nextFromPlan()
                },
                enabled = if (vm.nepalPlanLocked) !selectedCurrent else vm.canAdvanceFromPlan,
            )
        }
    }
}

@Composable
private fun PlanPager(vm: CheckoutViewModel) {
    val tiers = Catalog.TIERS
    val start = tiers.indexOf(vm.tier).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = start, pageCount = { tiers.size })
    val scope = rememberCoroutineScope()
    LaunchedEffect(vm.tier) {
        val page = tiers.indexOf(vm.tier)
        if (page >= 0 && pagerState.currentPage != page) {
            pagerState.scrollToPage(page)
        }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .drop(1)
            .collect { page ->
                val next = tiers[page]
                if (vm.tier != next) vm.tier = next
            }
    }
    val nextPage = if (pagerState.currentPage == 0) 1 else 0
    val nextTier = tiers[nextPage]
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 8.dp),
        pageSpacing = 12.dp,
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth(),
    ) { page ->
        TierCard(id = tiers[page], vm = vm, active = pagerState.currentPage == page)
    }
    Spacer(Modifier.height(10.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        tiers.forEachIndexed { index, tier ->
            val on = pagerState.currentPage == index
            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (on) 8.dp else 6.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(if (on) Color(TIER_META.getValue(tier).accent) else White.copy(0.25f))
                    .clickable { scope.launch { pagerState.animateScrollToPage(index) } },
            )
        }
    }
    Row(
        Modifier.fillMaxWidth().padding(top = 6.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            Modifier.clickable { scope.launch { pagerState.animateScrollToPage(nextPage) } }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (nextPage < pagerState.currentPage) {
                Icon(Icons.Filled.ChevronLeft, null, tint = White.copy(0.85f), modifier = Modifier.size(18.dp))
            }
            Text(
                "Swipe for ${TIER_META.getValue(nextTier).shortName}",
                color = White.copy(0.85f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (nextPage > pagerState.currentPage) {
                Icon(Icons.Filled.ChevronRight, null, tint = White.copy(0.85f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun DurationTabs(
    region: com.dgo.checkout.data.PriceRegion,
    tier: PlanTier,
    selected: PlanDuration,
    onSelect: (PlanDuration) -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, White.copy(0.10f), shape)
            .background(Color(0xFF121212))
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Catalog.DURATIONS.forEach { duration ->
            val active = selected == duration
            val sample = Catalog.findSku(region, tier, duration)
            val save = savingsVsMonthly(region, tier, duration)
            val sports = sample?.liveSports ?: (duration != PlanDuration.M01)
            val tabShape = RoundedCornerShape(18.dp)
            Column(
                Modifier
                    .weight(1f)
                    .clip(tabShape)
                    .background(if (active) BrandGradient else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
                    .clickable { onSelect(duration) }
                    .padding(vertical = 14.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    duration.label(),
                    color = if (active) White else White.copy(0.72f),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                )
                Text(
                    buildString {
                        append(sample?.let { formatMonthlyRate(it) } ?: "—")
                        if (duration == PlanDuration.M12 && save != null) append(" · −${save.percent}%")
                    },
                    color = if (active) White.copy(0.82f) else White.copy(0.38f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    if (sports) "Live sports" else "No live sports",
                    color = if (active) White else White.copy(if (sports) 0.55f else 0.32f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun TierCard(
    id: PlanTier,
    vm: CheckoutViewModel,
    active: Boolean,
) {
    val meta = TIER_META.getValue(id)
    val row = Catalog.findSku(vm.region, id, vm.duration)
    val currentPlan = (vm.manageMode || vm.nepalPlanLocked) && row != null && vm.session?.skuId == row.id
    val rowChange = resolvePlanChange(vm.session, row, vm.manageMode)
    val save = if (row != null) {
        offerSavings(vm.region, id, vm.duration)
    } else null
    val compareAt = if (save != null) compareAtPrice(vm.region, id, vm.duration) else 0.0
    val displayAmount = when {
        row == null -> 0.0
        else -> row.price
    }
    val accent = Color(meta.accent)
    val shape = RoundedCornerShape(28.dp)
    val state = if (vm.nepalPlanLocked) null else planStateLabel(rowChange?.kind)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, if (active) Color(meta.border) else White.copy(0.08f), shape)
            .background(
                Brush.verticalGradient(
                    listOf(accent.copy(if (active) 0.16f else 0.05f), Color(0xFF09090B)),
                ),
            )
            .padding(22.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(meta.shortName, color = White, fontSize = 22.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            if (currentPlan || state != null) {
                val pill = RoundedCornerShape(50)
                val chip = if (currentPlan) "Current" else state.orEmpty()
                Box(
                    Modifier
                        .clip(pill)
                        .background(if (currentPlan) Emerald.copy(0.14f) else accent.copy(0.16f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(chip, color = if (currentPlan) Color(0xFF6EE7B7) else accent, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        if (save != null && compareAt > 0 && row != null) {
            Text(
                formatMoney(compareAt, row.currency),
                color = White.copy(0.30f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                row?.let { formatMoney(displayAmount, it.currency) } ?: "—",
                color = White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.8).sp,
            )
            if (save != null) {
                Spacer(Modifier.width(8.dp))
                val pill = RoundedCornerShape(50)
                Box(
                    Modifier
                        .padding(bottom = 8.dp)
                        .clip(pill)
                        .background(Emerald.copy(0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text("−${save.percent}%", color = Color(0xFF6EE7B7), fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Text(
            when (rowChange?.kind) {
                PlanChangeKind.PROVIDER_UPGRADE -> "Starts now · pay only the difference"
                PlanChangeKind.PROVIDER_DOWNGRADE -> "Starts next bill · \$0 today"
                else -> listOfNotNull(
                    row?.takeIf { it.duration != PlanDuration.M01 }?.let { formatMonthlyRate(it) },
                    billingCadenceLabel(vm.duration, vm.region),
                ).joinToString(" · ")
            },
            color = White.copy(0.45f),
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(16.dp))
        meta.facts.forEach { fact ->
            FactRow(fact, accent)
        }
        FactRow(
            if (row?.liveSports == true) "Live sports included" else "No live sports",
            accent,
            dim = row?.liveSports != true,
        )
    }
}

@Composable
private fun FactRow(text: String, accent: Color, dim: Boolean = false) {
    Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.Check,
            null,
            tint = if (dim) accent.copy(0.35f) else accent,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = if (dim) White.copy(0.40f) else White.copy(0.70f), fontSize = 13.sp)
    }
}
