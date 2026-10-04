package com.dgo.checkout.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.data.NepalPsp
import com.dgo.checkout.data.PlanChangeKind
import com.dgo.checkout.data.TIER_META
import com.dgo.checkout.data.billingCadenceLabel
import com.dgo.checkout.data.dueTodayCaption
import com.dgo.checkout.data.formatMoney
import com.dgo.checkout.data.formatRenewalDate
import com.dgo.checkout.data.label
import com.dgo.checkout.data.lifecycleNote
import com.dgo.checkout.ui.CheckoutViewModel
import com.dgo.checkout.ui.components.BrandButton
import com.dgo.checkout.ui.components.CouponField
import com.dgo.checkout.ui.components.GhostField
import com.dgo.checkout.ui.components.RadioDot
import com.dgo.checkout.ui.theme.BrandPink
import com.dgo.checkout.ui.theme.Danger
import com.dgo.checkout.ui.theme.Ink
import com.dgo.checkout.ui.theme.StripePurple
import com.dgo.checkout.ui.theme.White

@Composable
fun PaymentScreen(vm: CheckoutViewModel) {
    val sku = vm.sku
    val title = sku?.let { "${TIER_META.getValue(it.tier).name} · ${it.duration.label()}" } ?: "DGO plan"
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 24.dp),
    ) {
        if (vm.region.stripe) {
            StripeHostedCheckout(vm, title)
        } else {
            NepalCheckout(vm, title)
        }
    }
}

@Composable
private fun PayCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, White.copy(0.12f), shape)
            .background(Ink)
            .background(
                Brush.verticalGradient(
                    0f to StripePurple.copy(0.16f),
                    0.35f to BrandPink.copy(0.04f),
                    1f to Color.Transparent,
                ),
            ),
    ) {
        Column(Modifier.padding(20.dp), content = content)
    }
}

@Composable
private fun NepalCheckout(vm: CheckoutViewModel, title: String) {
    val sku = vm.sku
    val price = formatMoney(vm.dueAmount, sku?.currency ?: vm.region.currency)
    val list = formatMoney(vm.amount, sku?.currency ?: vm.region.currency)
    PayCard {
        Text("DUE TODAY", color = White.copy(0.35f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.8.sp)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(price, color = White, fontSize = 30.sp, fontWeight = FontWeight.Black)
            if (vm.coupon != null) {
                Spacer(Modifier.width(8.dp))
                Text(list, color = White.copy(0.30f), fontSize = 14.sp, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.LineThrough)
            }
        }
        Text(title, color = White.copy(0.80f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(
            "Local wallets" + (sku?.let { " · ${billingCadenceLabel(it.duration, it.region)}" } ?: ""),
            color = White.copy(0.40f),
            fontSize = 12.sp,
        )
        Spacer(Modifier.height(16.dp))
        CouponField(vm.coupon, vm::applyCoupon, vm::clearCoupon)
        Spacer(Modifier.height(20.dp))
        Text("PAYMENT METHOD", color = White.copy(0.35f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.8.sp)
        Spacer(Modifier.height(8.dp))
        val listShape = RoundedCornerShape(12.dp)
        Column(
            Modifier
                .clip(listShape)
                .border(1.dp, White.copy(0.10f), listShape)
                .background(White.copy(0.03f)),
        ) {
            NepalPsp.entries.forEachIndexed { index, method ->
                if (index > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(White.copy(0.08f)))
                NepalMethodRow(method, vm)
            }
        }
        vm.paymentError?.let {
            Spacer(Modifier.height(10.dp))
            ErrorBanner(it)
        }
        Spacer(Modifier.height(14.dp))
        BrandButton(
            label = if (vm.nepalPsp != null) "Pay $price via ${vm.nepalPsp!!.title}" else "Pay $price",
            onClick = { vm.submitNepalPayment() },
            enabled = vm.nepalPsp != null,
            modifier = Modifier.fillMaxWidth(),
            leading = { Icon(Icons.Outlined.Lock, null, tint = White, modifier = Modifier.size(14.dp)) },
        )
        Spacer(Modifier.height(10.dp))
        Text("Prototype · no live charge", color = White.copy(0.30f), fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun NepalMethodRow(method: NepalPsp, vm: CheckoutViewModel) {
    val open = vm.nepalPsp == method
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .background(if (open) StripePurple.copy(0.12f) else Color.Transparent)
                .clickable {
                    vm.nepalPsp = method
                    vm.paymentError = null
                }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioDot(open, StripePurple)
            Spacer(Modifier.width(10.dp))
            PspMark(method)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(method.title, color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(method.tagline, color = White.copy(0.40f), fontSize = 11.sp)
            }
            if (method == NepalPsp.GETPAY) {
                Badge("VISA")
                Spacer(Modifier.width(4.dp))
                Badge("MC")
            }
        }
        if (open && method != NepalPsp.GETPAY) {
            Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                GhostField(
                    value = vm.mobileNumber,
                    onValueChange = {
                        vm.mobileNumber = if (method == NepalPsp.CONNECTIPS) {
                            it.filter { ch -> ch.isLetterOrDigit() || ch == '/' || ch == '_' || ch == '-' }.take(24)
                        } else {
                            it.filter { ch -> ch.isDigit() }.take(14)
                        }
                        vm.paymentError = null
                    },
                    placeholder = if (method == NepalPsp.CONNECTIPS) "Account / Customer ID" else "98XXXXXXXX",
                    keyboardType = if (method == NepalPsp.CONNECTIPS) KeyboardType.Text else KeyboardType.Number,
                )
                Spacer(Modifier.height(8.dp))
                Text("Continues in ${method.title}", color = White.copy(0.40f), fontSize = 11.sp)
            }
        }
        if (open && method == NepalPsp.GETPAY) {
            CardFields(
                number = vm.cardForm.number,
                expiry = vm.cardForm.expiry,
                cvc = vm.cardForm.cvc,
                name = vm.cardForm.name,
                onNumber = { vm.cardForm = vm.cardForm.copy(number = vm.formatCard(it)) },
                onExpiry = { vm.cardForm = vm.cardForm.copy(expiry = vm.formatExpiry(it)) },
                onCvc = { vm.cardForm = vm.cardForm.copy(cvc = it.filter { ch -> ch.isDigit() }.take(3)) },
                onName = { vm.cardForm = vm.cardForm.copy(name = it) },
            )
        }
    }
}

@Composable
private fun PspMark(method: NepalPsp) {
    val box = Modifier
        .size(width = 40.dp, height = 24.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(White.copy(0.05f))
        .border(1.dp, White.copy(0.10f), RoundedCornerShape(6.dp))
    if (method.drawable != null) {
        Image(painterResource(method.drawable), null, modifier = box.padding(3.dp))
    } else {
        Box(box, contentAlignment = Alignment.Center) {
            Text(method.emoji, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StripeHostedCheckout(vm: CheckoutViewModel, title: String) {
    val sku = vm.sku
    val currency = sku?.currency ?: vm.region.currency
    val due = dueTodayCaption(vm.planChange, vm.dueAmount, currency)
    val current = vm.session
    val kind = vm.planChange?.kind
    val newSubscription = kind == null || kind == PlanChangeKind.NEW
    PayCard {
        Text("DUE TODAY", color = White.copy(0.35f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.8.sp)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(due, color = White, fontSize = if (due.startsWith("$") || due.startsWith("रू")) 30.sp else 22.sp, fontWeight = FontWeight.Black)
            if (newSubscription && vm.coupon != null) {
                Spacer(Modifier.width(8.dp))
                Text(
                    formatMoney(vm.amount, currency),
                    color = White.copy(0.30f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.LineThrough,
                )
            }
        }
        Text(title, color = White.copy(0.80f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(
            sku?.let { billingCadenceLabel(it.duration, it.region) } ?: "One-time payment",
            color = White.copy(0.40f),
            fontSize = 12.sp,
        )
        if (current != null && kind != null && kind != PlanChangeKind.NEW) {
            Spacer(Modifier.height(12.dp))
            Text(
                lifecycleNote(current, vm.planChange),
                color = White.copy(0.50f),
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
        if (newSubscription) {
            Spacer(Modifier.height(16.dp))
            CouponField(vm.coupon, vm::applyCoupon, vm::clearCoupon, label = "PROMO CODE")
            if (vm.coupon == null) {
                Spacer(Modifier.height(6.dp))
                Text("Optional — or add it on Stripe.", color = White.copy(0.35f), fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("PAY WITH", color = White.copy(0.35f), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.8.sp)
        Spacer(Modifier.height(8.dp))
        val rowShape = RoundedCornerShape(12.dp)
        Row(
            Modifier
                .fillMaxWidth()
                .clip(rowShape)
                .border(1.dp, StripePurple.copy(0.45f), rowShape)
                .background(StripePurple.copy(0.12f))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioDot(true, StripePurple)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Stripe Checkout", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (newSubscription) "Card, Apple Pay, Google Pay & more" else "Confirm the change on Stripe",
                    color = White.copy(0.45f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                )
            }
            Spacer(Modifier.width(8.dp))
            Badge("stripe")
        }
        vm.paymentError?.let {
            Spacer(Modifier.height(10.dp))
            ErrorBanner(it)
        }
        Spacer(Modifier.height(14.dp))
        val cta = when (kind) {
            PlanChangeKind.PROVIDER_DOWNGRADE -> "Schedule in Stripe"
            PlanChangeKind.PROVIDER_UPGRADE -> "Continue to Stripe"
            else -> "Continue to Stripe · $due"
        }
        BrandButton(
            label = cta,
            onClick = { vm.submitStripePayment() },
            modifier = Modifier.fillMaxWidth(),
            leading = { Icon(Icons.Outlined.Lock, null, tint = White, modifier = Modifier.size(14.dp)) },
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Prototype · no live charge",
            color = White.copy(0.30f),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun CardFields(
    number: String,
    expiry: String,
    cvc: String,
    name: String,
    onNumber: (String) -> Unit,
    onExpiry: (String) -> Unit,
    onCvc: (String) -> Unit,
    onName: (String) -> Unit,
) {
    Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        val wrap = RoundedCornerShape(12.dp)
        Column(
            Modifier
                .clip(wrap)
                .border(1.dp, White.copy(0.10f), wrap)
                .background(Color.Black.copy(0.35f)),
        ) {
            Box {
                GhostField(
                    value = number,
                    onValueChange = onNumber,
                    placeholder = "1234 1234 1234 1234",
                    keyboardType = KeyboardType.Number,
                    mono = true,
                    trailing = { Icon(Icons.Outlined.CreditCard, null, tint = White.copy(0.30f), modifier = Modifier.size(16.dp)) },
                )
            }
            Row {
                GhostField(
                    value = expiry,
                    onValueChange = onExpiry,
                    placeholder = "MM / YY",
                    keyboardType = KeyboardType.Number,
                    mono = true,
                    modifier = Modifier.weight(1f),
                )
                GhostField(
                    value = cvc,
                    onValueChange = onCvc,
                    placeholder = "CVC",
                    keyboardType = KeyboardType.Number,
                    mono = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        GhostField(value = name, onValueChange = onName, placeholder = "Name on card")
    }
}

@Composable
private fun Badge(text: String) {
    Box(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(White.copy(0.10f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(text, color = White.copy(0.70f), fontSize = 9.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun ErrorBanner(text: String) {
    val shape = RoundedCornerShape(10.dp)
    Text(
        text,
        color = Color(0xFFFECACA),
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, Danger.copy(0.20f), shape)
            .background(Danger.copy(0.08f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}
