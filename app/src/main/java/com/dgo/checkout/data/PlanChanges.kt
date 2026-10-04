package com.dgo.checkout.data

fun resolvePlanChange(
    current: SubscriptionSession?,
    target: SubscriptionSku?,
    manageMode: Boolean,
): PlanChange? {
    if (target == null) return null
    if (!manageMode || current == null || current.region != target.region) {
        return PlanChange(PlanChangeKind.NEW, target.price, allowed = true)
    }
    if (current.skuId == target.id) {
        return if (current.billingMode == BillingMode.RECURRING) {
            PlanChange(PlanChangeKind.CURRENT, target.price, allowed = false)
        } else {
            PlanChange(PlanChangeKind.RENEWAL, target.price, allowed = true)
        }
    }
    if (current.billingMode == BillingMode.RECURRING) {
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

    val currentMonths = current.duration.months()
    val targetMonths = target.duration.months()
    if (targetMonths < currentMonths || (current.tier == PlanTier.PLUS && target.tier == PlanTier.MOBILE)) {
        return PlanChange(PlanChangeKind.DEFERRED, target.price, allowed = false)
    }
    if (current.tier == PlanTier.MOBILE && target.tier == PlanTier.PLUS && current.duration == target.duration) {
        val currentSku = Catalog.findSku(current.region, current.tier, current.duration)
        val fee = (target.price - (currentSku?.price ?: 0.0)).coerceAtLeast(0.0)
        return PlanChange(PlanChangeKind.FIXED_TIER_UPGRADE, fee, allowed = true)
    }
    if (targetMonths > currentMonths) {
        return PlanChange(PlanChangeKind.IMMEDIATE_EXTENSION, target.price, allowed = true)
    }
    return PlanChange(PlanChangeKind.DEFERRED, target.price, allowed = false)
}

fun planActionLabel(change: PlanChange?, currentPlan: Boolean): String = when (change?.kind) {
    PlanChangeKind.CURRENT -> "Current plan"
    PlanChangeKind.FIXED_TIER_UPGRADE, PlanChangeKind.PROVIDER_UPGRADE -> "Upgrade"
    PlanChangeKind.PROVIDER_DOWNGRADE -> "Schedule downgrade"
    PlanChangeKind.DEFERRED -> "After this term"
    PlanChangeKind.RENEWAL, PlanChangeKind.IMMEDIATE_EXTENSION -> "Add time"
    else -> if (currentPlan) "Add time" else "Continue"
}

fun planStateLabel(kind: PlanChangeKind?): String? = when (kind) {
    PlanChangeKind.CURRENT -> "Current plan"
    PlanChangeKind.FIXED_TIER_UPGRADE, PlanChangeKind.PROVIDER_UPGRADE -> "Upgrade"
    PlanChangeKind.PROVIDER_DOWNGRADE -> "Downgrade"
    PlanChangeKind.DEFERRED -> "After this term"
    PlanChangeKind.RENEWAL -> "Add time"
    PlanChangeKind.IMMEDIATE_EXTENSION -> "Longer term"
    else -> null
}

/** What the customer is asked to pay on this screen. Stripe proration is not calculated here. */
fun dueTodayCaption(change: PlanChange?, amount: Double, currency: Currency): String = when (change?.kind) {
    PlanChangeKind.PROVIDER_DOWNGRADE -> "No charge today"
    PlanChangeKind.PROVIDER_UPGRADE -> "Price difference"
    PlanChangeKind.CURRENT -> "Current plan"
    PlanChangeKind.DEFERRED -> "Not available yet"
    else -> formatMoney(amount, currency)
}

fun lifecycleNote(
    current: SubscriptionSession,
    change: PlanChange?,
): String = when (change?.kind) {
    PlanChangeKind.FIXED_TIER_UPGRADE -> "Plus now. Still ends ${formatRenewalDate(current.paidThrough)}."
    PlanChangeKind.IMMEDIATE_EXTENSION -> "Added after ${formatRenewalDate(current.paidThrough)}. No auto-renew."
    PlanChangeKind.DEFERRED -> "Available after ${formatRenewalDate(current.paidThrough)}."
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
