package com.dgo.checkout.data

import java.time.Instant

const val NEPAL_ACTIVE_PLAN_MESSAGE = "You already have an active subscription. Another plan cannot be purchased until your current plan ends. For upgrade-related queries, please contact customer support by email or through our official social channels."

fun SubscriptionSession.hasActiveNepalPlan(now: Instant = Instant.now()): Boolean =
    region == PriceRegion.NEPAL && runCatching { Instant.parse(paidThrough).isAfter(now) }.getOrDefault(true)

fun resolvePlanChange(
    current: SubscriptionSession?,
    target: SubscriptionSku?,
    manageMode: Boolean,
): PlanChange? {
    if (target == null) return null
    if (target.region == PriceRegion.NEPAL && current?.hasActiveNepalPlan() == true) {
        return PlanChange(
            if (current.skuId == target.id) PlanChangeKind.CURRENT else PlanChangeKind.SUPPORT_REQUIRED,
            amount = 0.0,
            allowed = false,
        )
    }
    if (!manageMode || current == null || current.region != target.region || target.region == PriceRegion.NEPAL) {
        return PlanChange(PlanChangeKind.NEW, target.price, allowed = true)
    }
    if (current.skuId == target.id) {
        return PlanChange(PlanChangeKind.CURRENT, target.price, allowed = false)
    }
    val downgrade =
        (current.tier == PlanTier.PLUS && target.tier == PlanTier.MOBILE) ||
            target.duration.months() < current.duration.months()
    return PlanChange(
        kind = if (downgrade) PlanChangeKind.PROVIDER_DOWNGRADE else PlanChangeKind.PROVIDER_UPGRADE,
        amount = target.price,
        allowed = true,
        intervalChange = target.duration != current.duration,
    )
}

fun planActionLabel(change: PlanChange?, currentPlan: Boolean): String = when (change?.kind) {
    PlanChangeKind.CURRENT -> "Current plan"
    PlanChangeKind.SUPPORT_REQUIRED -> "Plan unavailable"
    PlanChangeKind.PROVIDER_UPGRADE -> "Upgrade"
    PlanChangeKind.PROVIDER_DOWNGRADE -> "Schedule downgrade"
    else -> if (currentPlan) "Current plan" else "Continue"
}

fun planStateLabel(kind: PlanChangeKind?): String? = when (kind) {
    PlanChangeKind.CURRENT -> "Current plan"
    PlanChangeKind.SUPPORT_REQUIRED -> "Plan unavailable"
    PlanChangeKind.PROVIDER_UPGRADE -> "Upgrade"
    PlanChangeKind.PROVIDER_DOWNGRADE -> "Downgrade"
    else -> null
}

/** What the customer is asked to pay on this screen. Stripe proration is not calculated here. */
fun dueTodayCaption(change: PlanChange?, amount: Double, currency: Currency): String = when (change?.kind) {
    PlanChangeKind.PROVIDER_DOWNGRADE -> "No charge today"
    PlanChangeKind.PROVIDER_UPGRADE -> "Price difference"
    PlanChangeKind.CURRENT -> "Current plan"
    PlanChangeKind.SUPPORT_REQUIRED -> "Plan unavailable"
    else -> formatMoney(amount, currency)
}

fun lifecycleNote(current: SubscriptionSession, change: PlanChange?): String = when (change?.kind) {
    PlanChangeKind.SUPPORT_REQUIRED -> NEPAL_ACTIVE_PLAN_MESSAGE
    PlanChangeKind.PROVIDER_DOWNGRADE -> "Switches on ${formatRenewalDate(current.nextBillingDate ?: current.paidThrough)}. No charge today."
    PlanChangeKind.PROVIDER_UPGRADE -> if (change.intervalChange) {
        "Starts now. A new billing period begins today, minus credit for unused time. Stripe shows the exact amount."
    } else {
        "Starts now. You pay only for the days left until ${formatRenewalDate(current.nextBillingDate)}. Stripe shows the exact amount."
    }
    else -> if (current.billingMode == BillingMode.PREPAID) {
        "Access until ${formatRenewalDate(current.paidThrough)}."
    } else {
        "Renews ${formatRenewalDate(current.nextBillingDate)}."
    }
}
