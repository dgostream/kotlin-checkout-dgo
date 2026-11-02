package com.dgo.checkout.data

import java.time.Instant
import org.junit.Assert.*
import org.junit.Test

class PlanChangesTest {
    private fun session(sku: SubscriptionSku, paidThrough: String = "2099-01-01T00:00:00Z") = SubscriptionSession(
        skuId = sku.id, tier = sku.tier, duration = sku.duration, region = sku.region,
        liveSports = sku.liveSports, entitlement = sku.entitlement,
        billingMode = if (sku.region.stripe) BillingMode.RECURRING else BillingMode.PREPAID,
        status = SessionStatus.ACTIVE, paidThrough = paidThrough,
        nextBillingDate = if (sku.region.stripe) paidThrough else null,
    )

    @Test fun `active Nepal plan blocks every tier and duration even outside manage mode`() {
        val nepal = Catalog.SKUS.filter { it.region == PriceRegion.NEPAL }
        for (owned in nepal) for (target in nepal) for (manage in listOf(false, true)) {
            val result = resolvePlanChange(session(owned), target, manage)!!
            assertFalse("${owned.id} to ${target.id}, manage=$manage", result.allowed)
            assertEquals(0.0, result.amount, 0.0)
            assertEquals(if (owned.id == target.id) PlanChangeKind.CURRENT else PlanChangeKind.SUPPORT_REQUIRED, result.kind)
        }
    }

    @Test fun `Nepal purchase after expiry is a fresh term for every target`() {
        val current = session(Catalog.findSku("DGO-NP-PLS-12M")!!, "2020-01-01T00:00:00Z")
        for (target in Catalog.SKUS.filter { it.region == PriceRegion.NEPAL }) {
            val result = resolvePlanChange(current, target, manageMode = true)!!
            assertTrue(result.allowed)
            assertEquals(PlanChangeKind.NEW, result.kind)
            assertEquals(target.price, result.amount, 0.0)
        }
    }

    @Test fun `first Nepal subscription remains purchasable`() {
        for (target in Catalog.SKUS.filter { it.region == PriceRegion.NEPAL }) {
            assertTrue(resolvePlanChange(null, target, manageMode = false)!!.allowed)
        }
    }

    @Test fun `Nepal lock ends exactly at paid through time`() {
        val current = session(Catalog.findSku("DGO-NP-MOB-03M")!!, "2026-11-02T12:00:00Z")
        assertTrue(current.hasActiveNepalPlan(Instant.parse("2026-11-02T11:59:59Z")))
        assertFalse(current.hasActiveNepalPlan(Instant.parse(current.paidThrough)))
    }

    @Test fun `Stripe upgrade and downgrade rules remain available in every zone`() {
        for (region in PriceRegion.entries.filter { it.stripe }) {
            val mobile = Catalog.findSku(region, PlanTier.MOBILE, PlanDuration.M03)!!
            val plus = Catalog.findSku(region, PlanTier.PLUS, PlanDuration.M03)!!
            val upgrade = resolvePlanChange(session(mobile), plus, manageMode = true)!!
            val downgrade = resolvePlanChange(session(plus), mobile, manageMode = true)!!
            assertEquals(PlanChangeKind.PROVIDER_UPGRADE, upgrade.kind)
            assertEquals(PlanChangeKind.PROVIDER_DOWNGRADE, downgrade.kind)
            assertTrue(upgrade.allowed)
            assertTrue(downgrade.allowed)
            assertFalse(resolvePlanChange(session(plus), plus, manageMode = true)!!.allowed)
        }
    }
}
