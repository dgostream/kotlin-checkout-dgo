package com.dgo.checkout.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.R
import com.dgo.checkout.data.LandingCatalog
import com.dgo.checkout.data.LandingTab
import com.dgo.checkout.data.TIER_META
import com.dgo.checkout.data.TitleCard
import com.dgo.checkout.data.label
import com.dgo.checkout.ui.CheckoutViewModel
import com.dgo.checkout.ui.components.BrandButton
import com.dgo.checkout.ui.theme.BrandGradient
import com.dgo.checkout.ui.theme.BrandPink
import com.dgo.checkout.ui.theme.BrandPurple
import com.dgo.checkout.ui.theme.Ink
import com.dgo.checkout.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(vm: CheckoutViewModel) {
    val tab = LandingCatalog.tab(vm.tabId)
    Box(Modifier.fillMaxSize().background(if (tab.id == "junior") Color(0xFF0A1529) else Color.Black)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(onSearch = { vm.searchOpen = true }, onHome = { vm.selectTab("home") })
            TabStrip(selected = tab.id, onSelect = vm::selectTab)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp),
            ) {
                item { HeroBlock(tab, onOpen = { vm.detail = it }, onPlans = { vm.openCheckout(vm.session != null) }) }
                if (tab.id == "home") {
                    item { SubscribeDrive(vm) }
                } else if (tab.partnerTagline != null) {
                    item { PartnerBanner(tab) }
                }
                tab.rails.forEach { rail ->
                    item { RailBlock(rail.title, tab.color, rail.items) { vm.detail = it } }
                }
                item { FooterNote() }
            }
        }
        BottomBar(
            tabId = tab.id,
            onHome = { vm.selectTab("home") },
            onSearch = { vm.searchOpen = true },
            onBrowse = { vm.selectTab(if (tab.id == "home") "hotstar" else "home") },
            onAccount = vm::openAccount,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
        if (vm.searchOpen) SearchSheet(vm)
        vm.detail?.let { DetailSheet(it, vm.session != null, onClose = { vm.detail = null }, onPlans = { vm.detail = null; vm.openCheckout(vm.session != null) }) }
    }
}

@Composable
private fun TopBar(onSearch: () -> Unit, onHome: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painterResource(R.drawable.dgo_logo),
            "DGO",
            modifier = Modifier.height(32.dp).clickable(onClick = onHome),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.weight(1f))
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, White.copy(0.10f), CircleShape)
                .clickable(onClick = onSearch),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Search, "Search", tint = White.copy(0.80f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun TabStrip(selected: String, onSelect: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LandingCatalog.tabs.forEach { tab ->
            val on = tab.id == selected
            val shape = RoundedCornerShape(50)
            Text(
                tab.label,
                color = if (on) Color.Black else White.copy(0.70f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(shape)
                    .background(if (on) Color(tab.color) else White.copy(0.06f))
                    .clickable { onSelect(tab.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun HeroBlock(tab: LandingTab, onOpen: (TitleCard) -> Unit, onPlans: () -> Unit) {
    var index by remember(tab.id) { mutableIntStateOf(0) }
    val slides = tab.hero
    LaunchedEffect(tab.id, slides.size) {
        if (slides.size <= 1) return@LaunchedEffect
        while (true) {
            delay(7000)
            index = (index + 1) % slides.size
        }
    }
    val slide = slides.getOrNull(index) ?: return
    Box(
        Modifier
            .fillMaxWidth()
            .height(460.dp)
            .clickable { onOpen(slide) },
    ) {
        val art = slide.hero ?: slide.poster
        if (art != null) {
            Image(painterResource(art), slide.title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(tab.color), Color(tab.secondary), Color.Black))))
        }
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Black.copy(0.15f), Color.Transparent, Color.Black.copy(0.92f))),
            ),
        )
        Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Text(slide.tag.uppercase(), color = Color(tab.secondary), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
            Text(slide.title, color = White, fontSize = 34.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp)
            if (slide.subtitle.isNotBlank()) {
                Text(slide.subtitle, color = White.copy(0.70f), fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                listOfNotNull(
                    slide.year.takeIf { it.isNotBlank() },
                    slide.rating.takeIf { it.isNotBlank() }?.let { "★ $it" },
                    slide.duration.takeIf { it.isNotBlank() },
                ).joinToString("  ·  "),
                color = White.copy(0.45f),
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BrandButton(
                    label = "Watch",
                    onClick = { onOpen(slide) },
                    leading = { Icon(Icons.Filled.PlayArrow, null, tint = White, modifier = Modifier.size(16.dp)) },
                )
                Box(
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, White.copy(0.16f), RoundedCornerShape(16.dp))
                        .clickable(onClick = onPlans)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Text("See plans", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            if (slides.size > 1) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    slides.indices.forEach { i ->
                        Box(
                            Modifier
                                .height(3.dp)
                                .width(if (i == index) 18.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (i == index) White else White.copy(0.30f)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscribeDrive(vm: CheckoutViewModel) {
    val session = vm.session
    val shape = RoundedCornerShape(28.dp)
    Column(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(shape)
            .border(1.dp, White.copy(0.12f), shape)
            .background(Ink)
            .padding(18.dp),
    ) {
        if (session != null) {
            Text("YOUR PLAN", color = White.copy(0.35f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.6.sp)
            Spacer(Modifier.height(6.dp))
            Text(TIER_META.getValue(session.tier).name, color = White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(session.duration.label(), color = White.copy(0.45f), fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            BrandButton(
                label = if (session.billingMode == com.dgo.checkout.data.BillingMode.RECURRING) "Manage plan" else "Add time or upgrade",
                onClick = { vm.openCheckout(true) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text("ONE MEMBERSHIP", color = White.copy(0.40f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.8.sp)
            Spacer(Modifier.height(8.dp))
            Text("Two houses.", color = White, fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 32.sp)
            Text("The whole catalogue.", color = BrandPink, fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "JioHotstar Specials and movies. OSR Digital's Nepali film library. Pick Mobile or Plus — then just watch.",
                color = White.copy(0.50f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
            Spacer(Modifier.height(14.dp))
            BrandButton(label = "See plans", onClick = { vm.openCheckout(false) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            PlanTeaser("DGO Mobile", "720p · phones & tablets", "From रू 199 / $3.99", BrandPurple) { vm.openCheckout(false) }
            Spacer(Modifier.height(8.dp))
            PlanTeaser("DGO Plus", "1080p · TV & 3 streams", "From रू 299 / $5.99", BrandPink) { vm.openCheckout(false) }
        }
    }
}

@Composable
private fun PlanTeaser(title: String, line: String, price: String, accent: Color, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, accent.copy(0.28f), shape)
            .background(accent.copy(0.08f))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Text(title.uppercase(), color = accent, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        Text(line, color = White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(price, color = White.copy(0.40f), fontSize = 12.sp)
    }
}

@Composable
private fun PartnerBanner(tab: LandingTab) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.linearGradient(listOf(Color(tab.color).copy(0.35f), Color(tab.secondary).copy(0.18f))))
            .padding(16.dp),
    ) {
        Text(tab.partnerEyebrow?.uppercase() ?: "PARTNER", color = White.copy(0.55f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
        Text(tab.label, color = White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text(tab.partnerTagline.orEmpty(), color = White.copy(0.70f), fontSize = 13.sp)
    }
}

@Composable
private fun RailBlock(title: String, accent: Long, items: List<TitleCard>, onOpen: (TitleCard) -> Unit) {
    Column(Modifier.padding(top = 18.dp)) {
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(3.dp).height(16.dp).clip(CircleShape).background(BrandGradient))
            Spacer(Modifier.width(8.dp))
            Text(title.uppercase(), color = White, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 1.2.sp)
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items, key = { "${title}-${it.title}" }) { item ->
                PosterCard(item, Color(accent), onClick = { onOpen(item) })
            }
        }
    }
}

@Composable
private fun PosterCard(item: TitleCard, accent: Color, onClick: () -> Unit) {
    Column(Modifier.width(132.dp).clickable(onClick = onClick)) {
        val shape = RoundedCornerShape(16.dp)
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(accent.copy(0.55f), Color(0xFF111111)))),
        ) {
            val art = item.poster ?: item.hero
            if (art != null) {
                Image(painterResource(art), item.title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.72f)))))
            if (art == null) {
                Text(
                    item.title,
                    color = White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.BottomStart).padding(10.dp),
                    maxLines = 3,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(item.title, color = White.copy(0.85f), fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(item.tag, color = White.copy(0.35f), fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
private fun FooterNote() {
    Text(
        "© 2026 DGO GLOBAL  ·  THE WORLD IS WATCHING",
        color = White.copy(0.28f),
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 28.dp),
    )
}

@Composable
private fun BottomBar(
    tabId: String,
    onHome: () -> Unit,
    onSearch: () -> Unit,
    onBrowse: () -> Unit,
    onAccount: () -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(Color.Black.copy(0.92f))
            .navigationBarsPadding()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        NavItem("Home", Icons.Filled.Home, tabId == "home", onHome)
        NavItem("Search", Icons.Filled.Search, false, onSearch)
        NavItem("Browse", Icons.Outlined.GridView, tabId != "home", onBrowse)
        NavItem("Account", Icons.Outlined.Person, false, onAccount)
    }
}

@Composable
private fun NavItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, active: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, label, tint = if (active) BrandPurple else White.copy(0.55f), modifier = Modifier.size(22.dp))
        Text(label, color = if (active) White else White.copy(0.45f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SearchSheet(vm: CheckoutViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.94f))
            .statusBarsPadding()
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) {
                if (vm.searchQuery.isEmpty()) Text("Movies, series, sports…", color = White.copy(0.30f))
                BasicTextField(
                    value = vm.searchQuery,
                    onValueChange = { vm.searchQuery = it },
                    textStyle = TextStyle(color = White, fontSize = 16.sp),
                    cursorBrush = SolidColor(BrandPurple),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Icon(Icons.Filled.Close, "Close", tint = White, modifier = Modifier.clickable {
                vm.searchOpen = false
                vm.searchQuery = ""
            })
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.searchResults, key = { it.title }) { item ->
                Text(
                    "${item.title}  ·  ${item.tag}",
                    color = White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.searchOpen = false
                            vm.searchQuery = ""
                            vm.detail = item
                        }
                        .padding(vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun DetailSheet(item: TitleCard, subscribed: Boolean, onClose: () -> Unit, onPlans: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.72f)).clickable(onClick = onClose)) {
        val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(shape)
                .background(Ink)
                .clickable(onClick = {})
                .navigationBarsPadding()
                .padding(20.dp),
        ) {
            val art = item.hero ?: item.poster
            if (art != null) {
                Image(
                    painterResource(art),
                    item.title,
                    Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.height(12.dp))
            }
            Text(item.tag.uppercase(), color = BrandPink, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
            Text(item.title, color = White, fontSize = 26.sp, fontWeight = FontWeight.Black)
            if (item.subtitle.isNotBlank()) Text(item.subtitle, color = White.copy(0.60f), fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                listOf(item.year, item.rating, item.duration).filter { it.isNotBlank() }.joinToString(" · "),
                color = White.copy(0.40f),
                fontSize = 12.sp,
            )
            if (item.desc.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(item.desc, color = White.copy(0.65f), fontSize = 13.sp, lineHeight = 18.sp)
            }
            Spacer(Modifier.height(16.dp))
            BrandButton(
                label = if (subscribed) "You're in · see plan" else "Subscribe to watch",
                onClick = onPlans,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Text("Close", color = White.copy(0.40f), modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = onClose).padding(8.dp))
        }
    }
}
