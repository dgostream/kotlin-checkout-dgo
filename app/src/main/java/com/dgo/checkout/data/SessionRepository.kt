package com.dgo.checkout.data

import android.content.Context
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class SessionRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getRegion(): PriceRegion {
        val stored = prefs.getString(KEY_REGION, PriceRegion.NEPAL.code)
        return PriceRegion.entries.firstOrNull { it.code == stored } ?: PriceRegion.NEPAL
    }

    fun setRegion(region: PriceRegion) {
        prefs.edit().putString(KEY_REGION, region.code).apply()
    }

    fun getSession(): SubscriptionSession? {
        val raw = prefs.getString(KEY_SESSION, null) ?: return null
        return runCatching { parseSession(raw) }.getOrNull()
    }

    fun cancelAtPeriodEnd() {
        val current = getSession() ?: return
        if (current.billingMode != BillingMode.RECURRING) return
        setSession(current.copy(status = SessionStatus.CANCELING, pendingPlan = null))
    }

    fun resumeSubscription() {
        val current = getSession() ?: return
        if (current.billingMode != BillingMode.RECURRING) return
        setSession(current.copy(status = SessionStatus.ACTIVE))
    }

    fun setSession(session: SubscriptionSession?) {
        if (session == null) {
            prefs.edit().remove(KEY_SESSION).apply()
        } else {
            prefs.edit().putString(KEY_SESSION, writeSession(session)).apply()
        }
    }

    fun sessionFromSku(sku: SubscriptionSku): SubscriptionSession {
        val billingMode = if (sku.region.stripe) BillingMode.RECURRING else BillingMode.PREPAID
        val paidThrough = addUtcMonths(sku.duration.months())
        return SubscriptionSession(
            skuId = sku.id,
            tier = sku.tier,
            duration = sku.duration,
            region = sku.region,
            liveSports = sku.liveSports,
            entitlement = sku.entitlement,
            billingMode = billingMode,
            status = SessionStatus.ACTIVE,
            paidThrough = paidThrough,
            nextBillingDate = if (billingMode == BillingMode.RECURRING) paidThrough else null,
        )
    }

    fun persistPurchase(
        sku: SubscriptionSku,
        current: SubscriptionSession?,
        kind: PlanChangeKind,
    ) {
        if (current?.billingMode == BillingMode.RECURRING && kind == PlanChangeKind.PROVIDER_DOWNGRADE) {
            setSession(
                current.copy(
                    status = SessionStatus.ACTIVE,
                    pendingPlan = PendingPlan(
                        skuId = sku.id,
                        tier = sku.tier,
                        duration = sku.duration,
                        effectiveDate = current.nextBillingDate ?: current.paidThrough,
                    ),
                ),
            )
            return
        }
        val next = sessionFromSku(sku)
        val updated = when {
            current?.billingMode == BillingMode.PREPAID && kind == PlanChangeKind.FIXED_TIER_UPGRADE ->
                next.copy(paidThrough = current.paidThrough, nextBillingDate = null)
            current?.billingMode == BillingMode.PREPAID &&
                kind in setOf(PlanChangeKind.RENEWAL, PlanChangeKind.IMMEDIATE_EXTENSION) ->
                next.copy(paidThrough = addMonthsToIso(current.paidThrough, sku.duration.months()))
            current?.billingMode == BillingMode.RECURRING && kind == PlanChangeKind.PROVIDER_UPGRADE &&
                current.duration == sku.duration ->
                next.copy(
                    paidThrough = current.paidThrough,
                    nextBillingDate = current.nextBillingDate,
                )
            else -> next
        }
        setSession(updated)
    }

    fun generateOrderRef(): String {
        val hex = List(8) { "0123456789ABCDEF".random() }.joinToString("")
        return "DGO-${hex.take(4)}-${hex.drop(4)}"
    }

    private fun parseSession(raw: String): SubscriptionSession? {
        val json = JSONObject(raw)
        val skuId = json.optString("skuId").ifBlank { return null }
        val tier = PlanTier.valueOf(json.getString("tier"))
        val duration = PlanDuration.valueOf(json.getString("duration"))
        val region = PriceRegion.entries.firstOrNull { it.code == json.optString("region") } ?: getRegion()
        val fallback = Catalog.findSku(region, tier, duration)?.let { sessionFromSku(it) }
        val pending = json.optJSONObject("pendingPlan")?.let {
            PendingPlan(
                skuId = it.getString("skuId"),
                tier = PlanTier.valueOf(it.getString("tier")),
                duration = PlanDuration.valueOf(it.getString("duration")),
                effectiveDate = it.getString("effectiveDate"),
            )
        }
        return SubscriptionSession(
            skuId = skuId,
            tier = tier,
            duration = duration,
            region = region,
            liveSports = json.optBoolean("liveSports", fallback?.liveSports ?: false),
            entitlement = runCatching { Entitlement.valueOf(json.getString("entitlement")) }.getOrElse {
                fallback?.entitlement ?: Entitlement.EP_PLUS
            },
            billingMode = runCatching { BillingMode.valueOf(json.getString("billingMode")) }.getOrElse {
                if (region.stripe) BillingMode.RECURRING else BillingMode.PREPAID
            },
            status = runCatching { SessionStatus.valueOf(json.optString("status")) }.getOrDefault(SessionStatus.ACTIVE),
            paidThrough = json.optString("paidThrough").ifBlank { fallback?.paidThrough.orEmpty() },
            nextBillingDate = json.optString("nextBillingDate").ifBlank { fallback?.nextBillingDate },
            pendingPlan = pending,
        )
    }

    private fun writeSession(session: SubscriptionSession): String {
        val json = JSONObject()
            .put("skuId", session.skuId)
            .put("tier", session.tier.name)
            .put("duration", session.duration.name)
            .put("region", session.region.code)
            .put("liveSports", session.liveSports)
            .put("entitlement", session.entitlement.name)
            .put("billingMode", session.billingMode.name)
            .put("status", session.status.name)
            .put("paidThrough", session.paidThrough)
            .put("nextBillingDate", session.nextBillingDate)
        session.pendingPlan?.let {
            json.put(
                "pendingPlan",
                JSONObject()
                    .put("skuId", it.skuId)
                    .put("tier", it.tier.name)
                    .put("duration", it.duration.name)
                    .put("effectiveDate", it.effectiveDate),
            )
        }
        return json.toString()
    }

    companion object {
        private const val PREFS = "dgo_checkout"
        private const val KEY_REGION = "dgo_dev_region"
        private const val KEY_SESSION = "dgo_unlock_subscription"

        fun addUtcMonths(months: Int): String {
            return ZonedDateTime.now(ZoneOffset.UTC).plusMonths(months.toLong()).toInstant().toString()
        }

        fun addMonthsToIso(value: String, months: Int): String {
            return runCatching {
                Instant.parse(value).atZone(ZoneOffset.UTC).plusMonths(months.toLong()).toInstant().toString()
            }.getOrElse { addUtcMonths(months) }
        }
    }
}
