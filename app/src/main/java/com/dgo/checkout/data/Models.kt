package com.dgo.checkout.data

enum class PriceRegion(
    val code: String,
    val toggleLabel: String,
    val catalogLabel: String,
    val billedIn: String,
    val currency: Currency,
    val stripe: Boolean,
) {
    NEPAL("nepal", "NP", "Nepal", "Billed in NPR", Currency.NPR, stripe = false),
    ZONE_A("za", "ZA", "India & Middle East", "Billed in USD", Currency.USD, stripe = true),
    ZONE_B("zb", "ZB", "USA / Europe / AU / NZ", "Billed in USD", Currency.USD, stripe = true),
    ZONE_C("zc", "ZC", "South East Asia", "Billed in USD", Currency.USD, stripe = true),
}

enum class PlanTier { MOBILE, PLUS }

enum class PlanDuration { M01, M03, M12 }

enum class Currency { NPR, USD }

enum class Entitlement { EP_MOBILE, EP_PLUS }

enum class BillingMode { PREPAID, RECURRING }

enum class SessionStatus { ACTIVE, CANCELING }

enum class NepalPsp(
    val title: String,
    val tagline: String,
    val drawable: Int?,
    val emoji: String,
) {
    KHALTI("Khalti by IME", "Digital wallet", com.dgo.checkout.R.drawable.psp_khalti, "💜"),
    ESEWA("eSewa", "Digital wallet", com.dgo.checkout.R.drawable.psp_esewa, "💚"),
    CONNECTIPS("ConnectIPS", "Bank transfer", com.dgo.checkout.R.drawable.psp_connectips, "🔗"),
    FONEPAY("Fonepay", "QR payment", com.dgo.checkout.R.drawable.psp_fonepay, "📱"),
    GETPAY("GetPay", "Visa / Mastercard", null, "💳"),
}

data class SubscriptionSku(
    val id: String,
    val region: PriceRegion,
    val tier: PlanTier,
    val duration: PlanDuration,
    val price: Double,
    val currency: Currency,
    val entitlement: Entitlement,
    val liveSports: Boolean,
)

data class PendingPlan(
    val skuId: String,
    val tier: PlanTier,
    val duration: PlanDuration,
    val effectiveDate: String,
)

data class SubscriptionSession(
    val skuId: String,
    val tier: PlanTier,
    val duration: PlanDuration,
    val region: PriceRegion,
    val liveSports: Boolean,
    val entitlement: Entitlement,
    val billingMode: BillingMode,
    val status: SessionStatus,
    val paidThrough: String,
    val nextBillingDate: String?,
    val pendingPlan: PendingPlan? = null,
)

data class AppliedCoupon(val code: String, val percent: Int)

enum class PlanChangeKind {
    NEW,
    CURRENT,
    RENEWAL,
    FIXED_TIER_UPGRADE,
    IMMEDIATE_EXTENSION,
    PROVIDER_UPGRADE,
    PROVIDER_DOWNGRADE,
    DEFERRED,
}

data class PlanChange(
    val kind: PlanChangeKind,
    val amount: Double,
    val allowed: Boolean,
)

data class CardForm(
    val number: String = "",
    val name: String = "",
    val expiry: String = "",
    val cvc: String = "",
)

enum class Screen { HOME, ACCOUNT, CHECKOUT }
