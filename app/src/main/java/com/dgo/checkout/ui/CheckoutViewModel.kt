package com.dgo.checkout.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.dgo.checkout.data.AppliedCoupon
import com.dgo.checkout.data.CardForm
import com.dgo.checkout.data.Catalog
import com.dgo.checkout.data.LandingCatalog
import com.dgo.checkout.data.NepalPsp
import com.dgo.checkout.data.TitleCard
import com.dgo.checkout.data.PlanChangeKind
import com.dgo.checkout.data.PlanDuration
import com.dgo.checkout.data.PlanTier
import com.dgo.checkout.data.PriceRegion
import com.dgo.checkout.data.Screen
import com.dgo.checkout.data.SessionRepository
import com.dgo.checkout.data.SubscriptionSession
import com.dgo.checkout.data.SubscriptionSku
import com.dgo.checkout.data.applyCouponAmount
import com.dgo.checkout.data.resolvePlanChange

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = SessionRepository(application)

    var screen by mutableStateOf(Screen.HOME)
        private set
    var tabId by mutableStateOf("home")
    var detail by mutableStateOf<TitleCard?>(null)
    var searchOpen by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var plansOpen by mutableStateOf(true)
    var cancelStep by mutableIntStateOf(0)
    var cancelReason by mutableStateOf("")
    var cancelPhrase by mutableStateOf("")
    var confirmResume by mutableStateOf(false)
    var accountNotice by mutableStateOf<String?>(null)
    var step by mutableIntStateOf(0)
        private set
    var region by mutableStateOf(repo.getRegion())
        private set
    var tier by mutableStateOf(PlanTier.PLUS)
    var duration by mutableStateOf(PlanDuration.M03)
    var session by mutableStateOf(repo.getSession())
        private set
    var manageMode by mutableStateOf(false)
        private set

    var nepalPsp by mutableStateOf<NepalPsp?>(null)
    var mobileNumber by mutableStateOf("")
    var cardForm by mutableStateOf(CardForm())
    var paymentError by mutableStateOf<String?>(null)
    var coupon by mutableStateOf<AppliedCoupon?>(null)
    var orderRef by mutableStateOf(repo.generateOrderRef())
        private set
    var lastPaymentLabel by mutableStateOf("Payment method")
        private set
    var completedKind by mutableStateOf(PlanChangeKind.NEW)
        private set

    val sku: SubscriptionSku?
        get() = Catalog.findSku(region, tier, duration)

    val planChange
        get() = resolvePlanChange(session, sku, manageMode)

    val amount: Double
        get() {
            val selected = sku ?: return 0.0
            val change = planChange
            return change?.amount ?: selected.price
        }

    val dueAmount: Double
        get() = applyCouponAmount(amount, sku?.currency ?: region.currency, coupon)

    val canAdvanceFromPlan: Boolean
        get() = sku != null && (planChange?.allowed ?: true)

    fun setDevRegion(next: PriceRegion) {
        region = next
        repo.setRegion(next)
        val current = repo.getSession()
        if (current != null) {
            val seeded = Catalog.findSku(next, current.tier, current.duration)?.let { repo.sessionFromSku(it) }
            if (seeded != null) {
                repo.setSession(seeded)
                session = seeded
                tier = seeded.tier
                duration = seeded.duration
            }
        }
        nepalPsp = null
        paymentError = null
        coupon = null
    }

    fun setSubscribed(on: Boolean) {
        if (on) {
            val seeded = Catalog.findSku(region, session?.tier ?: PlanTier.PLUS, session?.duration ?: PlanDuration.M03)
                ?: Catalog.findSku(region, PlanTier.PLUS, PlanDuration.M03)
            if (seeded != null) {
                val next = repo.sessionFromSku(seeded)
                repo.setSession(next)
                session = next
            }
        } else {
            repo.setSession(null)
            session = null
            manageMode = false
        }
    }

    fun openCheckout(manage: Boolean = false) {
        manageMode = manage && session != null
        if (manageMode) {
            session?.let {
                region = it.region
                tier = it.tier
                duration = it.duration
            }
        } else {
            region = repo.getRegion()
        }
        step = 0
        nepalPsp = null
        paymentError = null
        coupon = null
        orderRef = repo.generateOrderRef()
        screen = Screen.CHECKOUT
    }

    fun goHome() {
        session = repo.getSession()
        screen = Screen.HOME
        step = 0
        manageMode = false
    }

    fun openAccount() {
        session = repo.getSession()
        screen = Screen.ACCOUNT
        searchOpen = false
        detail = null
        cancelStep = 0
        cancelReason = ""
        cancelPhrase = ""
        confirmResume = false
        accountNotice = null
    }

    fun signOut() {
        setSubscribed(false)
        screen = Screen.HOME
        accountNotice = null
    }

    fun refreshSession() {
        session = repo.getSession()
    }

    fun cancelRenewal() {
        repo.cancelAtPeriodEnd()
        refreshSession()
        cancelStep = 0
        cancelReason = ""
        cancelPhrase = ""
    }

    fun resumeRenewal() {
        repo.resumeSubscription()
        refreshSession()
        confirmResume = false
        cancelStep = 0
    }

    fun selectTab(id: String) {
        tabId = id
        searchOpen = false
        detail = null
    }

    val searchResults: List<TitleCard>
        get() = LandingCatalog.search(searchQuery)

    fun back() {
        when {
            screen == Screen.HOME -> return
            screen == Screen.ACCOUNT -> goHome()
            step == 0 -> goHome()
            else -> step -= 1
        }
    }

    fun nextFromPlan() {
        if (!canAdvanceFromPlan) return
        step = 1
    }

    fun applyCoupon(raw: String): String? {
        val code = raw.trim().uppercase()
        val percent = Catalog.COUPONS[code] ?: return "That code isn’t valid."
        coupon = AppliedCoupon(code, percent)
        return null
    }

    fun clearCoupon() {
        coupon = null
    }

    fun submitNepalPayment(): Boolean {
        paymentError = null
        val method = nepalPsp
        if (method == null) {
            paymentError = "Choose a payment method."
            return false
        }
        if (method == NepalPsp.GETPAY) {
            if (
                cardForm.number.filter { it.isDigit() }.length < 12 ||
                cardForm.expiry.length < 5 ||
                cardForm.cvc.length < 3 ||
                cardForm.name.isBlank()
            ) {
                paymentError = "Complete all card details."
                return false
            }
            if (cardForm.number.filter { it.isDigit() } == DECLINE_CARD) {
                paymentError = "Card declined. Try another."
                return false
            }
        } else if (mobileNumber.trim().length < 5) {
            paymentError = if (method == NepalPsp.CONNECTIPS) {
                "Enter a valid account or customer ID."
            } else {
                "Enter a valid mobile number."
            }
            return false
        }
        lastPaymentLabel = method.title
        completePurchase()
        return true
    }

    /**
     * Hosted Stripe Checkout opens in a WebView later; no card data is collected in the app.
     * A backend creates the session: with `discounts = [promotion_code]` when [coupon] is set,
     * otherwise with `allow_promotion_codes = true` (Stripe rejects both on one session).
     */
    fun submitStripePayment(): Boolean {
        paymentError = null
        if (planChange?.kind != PlanChangeKind.NEW) coupon = null
        lastPaymentLabel = "Stripe Checkout"
        completePurchase()
        return true
    }

    private fun completePurchase() {
        val selected = sku ?: return
        val kind = planChange?.kind ?: PlanChangeKind.NEW
        completedKind = kind
        repo.persistPurchase(selected, session, kind)
        session = repo.getSession()
        step = 2
    }

    fun formatCard(raw: String): String =
        raw.filter { it.isDigit() }.take(16).chunked(4).joinToString(" ")

    fun formatExpiry(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(4)
        return if (digits.length >= 3) "${digits.take(2)}/${digits.drop(2)}" else digits
    }

    companion object {
        private const val DECLINE_CARD = "4000000000000002"
    }
}

