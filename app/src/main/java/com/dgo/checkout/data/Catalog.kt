package com.dgo.checkout.data

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * DGO Product & Entitlement Spec v3.0 — sellable SKUs.
 * Nepal NPR wallets + three international USD Stripe zones.
 */
object Catalog {
    val SKUS: List<SubscriptionSku> = listOf(
        sku("DGO-NP-MOB-01M", PriceRegion.NEPAL, PlanTier.MOBILE, PlanDuration.M01, 199.0, false),
        sku("DGO-NP-PLS-01M", PriceRegion.NEPAL, PlanTier.PLUS, PlanDuration.M01, 299.0, false),
        sku("DGO-NP-MOB-03M", PriceRegion.NEPAL, PlanTier.MOBILE, PlanDuration.M03, 549.0, true),
        sku("DGO-NP-PLS-03M", PriceRegion.NEPAL, PlanTier.PLUS, PlanDuration.M03, 799.0, true),
        sku("DGO-NP-MOB-12M", PriceRegion.NEPAL, PlanTier.MOBILE, PlanDuration.M12, 1799.0, true),
        sku("DGO-NP-PLS-12M", PriceRegion.NEPAL, PlanTier.PLUS, PlanDuration.M12, 2699.0, true),

        sku("DGO-ZA-MOB-01M", PriceRegion.ZONE_A, PlanTier.MOBILE, PlanDuration.M01, 3.99, false),
        sku("DGO-ZA-PLS-01M", PriceRegion.ZONE_A, PlanTier.PLUS, PlanDuration.M01, 5.99, false),
        sku("DGO-ZA-MOB-03M", PriceRegion.ZONE_A, PlanTier.MOBILE, PlanDuration.M03, 9.99, true),
        sku("DGO-ZA-PLS-03M", PriceRegion.ZONE_A, PlanTier.PLUS, PlanDuration.M03, 14.99, true),
        sku("DGO-ZA-MOB-12M", PriceRegion.ZONE_A, PlanTier.MOBILE, PlanDuration.M12, 35.99, true),
        sku("DGO-ZA-PLS-12M", PriceRegion.ZONE_A, PlanTier.PLUS, PlanDuration.M12, 50.99, true),

        sku("DGO-ZB-MOB-01M", PriceRegion.ZONE_B, PlanTier.MOBILE, PlanDuration.M01, 6.99, false),
        sku("DGO-ZB-PLS-01M", PriceRegion.ZONE_B, PlanTier.PLUS, PlanDuration.M01, 8.99, false),
        sku("DGO-ZB-MOB-03M", PriceRegion.ZONE_B, PlanTier.MOBILE, PlanDuration.M03, 18.99, true),
        sku("DGO-ZB-PLS-03M", PriceRegion.ZONE_B, PlanTier.PLUS, PlanDuration.M03, 29.99, true),
        sku("DGO-ZB-MOB-12M", PriceRegion.ZONE_B, PlanTier.MOBILE, PlanDuration.M12, 64.99, true),
        sku("DGO-ZB-PLS-12M", PriceRegion.ZONE_B, PlanTier.PLUS, PlanDuration.M12, 99.99, true),

        sku("DGO-ZC-MOB-01M", PriceRegion.ZONE_C, PlanTier.MOBILE, PlanDuration.M01, 4.99, false),
        sku("DGO-ZC-PLS-01M", PriceRegion.ZONE_C, PlanTier.PLUS, PlanDuration.M01, 6.99, false),
        sku("DGO-ZC-MOB-03M", PriceRegion.ZONE_C, PlanTier.MOBILE, PlanDuration.M03, 10.99, true),
        sku("DGO-ZC-PLS-03M", PriceRegion.ZONE_C, PlanTier.PLUS, PlanDuration.M03, 17.99, true),
        sku("DGO-ZC-MOB-12M", PriceRegion.ZONE_C, PlanTier.MOBILE, PlanDuration.M12, 44.99, true),
        sku("DGO-ZC-PLS-12M", PriceRegion.ZONE_C, PlanTier.PLUS, PlanDuration.M12, 65.99, true),
    )

    val COUPONS: Map<String, Int> = mapOf("DGO10" to 10, "DGO20" to 20)

    val DURATIONS: List<PlanDuration> = PlanDuration.entries
    val TIERS: List<PlanTier> = listOf(PlanTier.PLUS, PlanTier.MOBILE)

    fun findSku(region: PriceRegion, tier: PlanTier, duration: PlanDuration): SubscriptionSku? =
        SKUS.firstOrNull { it.region == region && it.tier == tier && it.duration == duration }

    fun findSku(id: String): SubscriptionSku? = SKUS.firstOrNull { it.id == id }

    private fun sku(
        id: String,
        region: PriceRegion,
        tier: PlanTier,
        duration: PlanDuration,
        price: Double,
        liveSports: Boolean,
    ) = SubscriptionSku(
        id = id,
        region = region,
        tier = tier,
        duration = duration,
        price = price,
        currency = region.currency,
        entitlement = if (tier == PlanTier.PLUS) Entitlement.EP_PLUS else Entitlement.EP_MOBILE,
        liveSports = liveSports,
    )
}

data class TierMeta(
    val name: String,
    val shortName: String,
    val slogan: String,
    val quality: String,
    val facts: List<String>,
    val accent: Long,
    val border: Long,
)

val TIER_META: Map<PlanTier, TierMeta> = mapOf(
    PlanTier.MOBILE to TierMeta(
        name = "DGO Mobile",
        shortName = "Mobile",
        slogan = "Phones, tablets & mobile web.",
        quality = "720p HD · 1 stream",
        facts = listOf(
            "Phones & tablets · no TV",
            "720p · 1 stream",
            "1 profile",
        ),
        accent = 0xFF8A3FFC,
        border = 0x598A3FFC,
    ),
    PlanTier.PLUS to TierMeta(
        name = "DGO Plus",
        shortName = "Plus",
        slogan = "TV, casting & more screens.",
        quality = "1080p Full HD · 3 streams",
        facts = listOf(
            "TV, cast, desktop & phones",
            "1080p · 3 streams",
            "4 profiles",
        ),
        accent = 0xFFFF00BD,
        border = 0x59FF00BD,
    ),
)

fun PlanDuration.label(): String = when (this) {
    PlanDuration.M01 -> "1 month"
    PlanDuration.M03 -> "3 months"
    PlanDuration.M12 -> "12 months"
}

fun PlanDuration.months(): Int = when (this) {
    PlanDuration.M01 -> 1
    PlanDuration.M03 -> 3
    PlanDuration.M12 -> 12
}

fun formatMoney(amount: Double, currency: Currency): String = when (currency) {
    Currency.NPR -> "रू ${NumberFormat.getIntegerInstance(Locale.US).format(amount.roundToInt())}"
    Currency.USD -> "$" + String.format(Locale.US, "%.2f", amount)
}

fun formatMonthlyRate(sku: SubscriptionSku): String {
    val per = sku.price / sku.duration.months()
    return when (sku.currency) {
        Currency.NPR -> "रू ${per.roundToInt()}/mo"
        Currency.USD -> formatMoney(per, Currency.USD) + "/mo"
    }
}

fun billingCadenceLabel(duration: PlanDuration, region: PriceRegion): String {
    if (region == PriceRegion.NEPAL) return "One-time payment"
    return when (duration) {
        PlanDuration.M12 -> "Billed annually"
        PlanDuration.M03 -> "Monthly · 3 months"
        PlanDuration.M01 -> "Billed monthly"
    }
}

/** Stripe 3-month plans collect the discounted monthly rate today. */
fun checkoutPriceForSku(sku: SubscriptionSku): Double {
    return if (sku.region.stripe && sku.duration == PlanDuration.M03) {
        (sku.price / sku.duration.months()).let { (it * 100).roundToInt() / 100.0 }
    } else {
        sku.price
    }
}

data class Savings(val amount: Double, val percent: Int, val label: String)

fun savingsVsMonthly(region: PriceRegion, tier: PlanTier, duration: PlanDuration): Savings? {
    if (duration == PlanDuration.M01) return null
    val monthly = Catalog.findSku(region, tier, PlanDuration.M01) ?: return null
    val picked = Catalog.findSku(region, tier, duration) ?: return null
    val amount = monthly.price * duration.months() - picked.price
    if (amount <= 0) return null
    val percent = ((amount / (monthly.price * duration.months())) * 100).roundToInt()
    return Savings(amount, percent, formatMoney(amount, picked.currency))
}

fun compareAtPrice(region: PriceRegion, tier: PlanTier, duration: PlanDuration): Double {
    val monthly = Catalog.findSku(region, tier, PlanDuration.M01)?.price ?: 0.0
    return if (region.stripe && duration == PlanDuration.M03) monthly else monthly * duration.months()
}

fun applyCouponAmount(amount: Double, currency: Currency, coupon: AppliedCoupon?): Double {
    if (coupon == null) return amount
    val raw = amount * (1 - coupon.percent / 100.0)
    return if (currency == Currency.NPR) raw.roundToInt().toDouble() else (raw * 100).roundToInt() / 100.0
}

fun formatRenewalDate(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return try {
        val instant = java.time.Instant.parse(iso)
        val date = instant.atZone(java.time.ZoneOffset.UTC).toLocalDate()
        val fmt = java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
        date.format(fmt)
    } catch (_: Exception) {
        iso
    }
}
