package com.dgo.checkout.data

/**
 * One-time event passes (pay-per-view). Bought without a subscription and never renewed.
 * Prices are placeholders until the event commercial sheet is approved.
 */
data class EventPass(
    val key: String,
    val league: String,
    val title: String,
    val subtitle: String,
    val window: String,
    val accessUntil: String,
    val includes: List<String>,
    val prices: Map<PriceRegion, Double>,
    val accent: Long,
    val coveredBy: String? = null,
) {
    fun skuId(region: PriceRegion): String = "PPV-${region.toggleLabel}-$key"
    fun price(region: PriceRegion): Double = prices.getValue(region)
}

object Events {
    val ALL: List<EventPass> = listOf(
        EventPass(
            key = "EURO28-ALL",
            league = "UEFA",
            title = "EURO 2028",
            subtitle = "Full tournament pass",
            window = "Jun – Jul 2028",
            accessUntil = "2028-07-31T23:59:59Z",
            includes = listOf("All 51 matches live", "Replays & highlights", "Phone, TV & web"),
            prices = mapOf(
                PriceRegion.NEPAL to 999.0,
                PriceRegion.ZONE_A to 9.99,
                PriceRegion.ZONE_B to 24.99,
                PriceRegion.ZONE_C to 12.99,
            ),
            accent = 0xFF3B82F6,
        ),
        EventPass(
            key = "EURO28-KO",
            league = "UEFA",
            title = "EURO 2028",
            subtitle = "Knockout stage pass",
            window = "Round of 16 to the final",
            accessUntil = "2028-07-31T23:59:59Z",
            includes = listOf("15 knockout matches live", "Replays & highlights", "Phone, TV & web"),
            prices = mapOf(
                PriceRegion.NEPAL to 499.0,
                PriceRegion.ZONE_A to 5.99,
                PriceRegion.ZONE_B to 14.99,
                PriceRegion.ZONE_C to 7.99,
            ),
            accent = 0xFFFF00BD,
            coveredBy = "EURO28-ALL",
        ),
    )

    fun find(key: String): EventPass? = ALL.firstOrNull { it.key == key }
}

enum class PassOwnership { OWNED, INCLUDED }

fun passOwnership(event: EventPass, owned: Set<String>): PassOwnership? = when {
    event.key in owned -> PassOwnership.OWNED
    event.coveredBy != null && event.coveredBy in owned -> PassOwnership.INCLUDED
    else -> null
}
