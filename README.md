# DGO Checkout (Kotlin)

Mobile-first Jetpack Compose port of the DGO `/join` checkout from the Next.js prototype, using **Product & Entitlement Spec v3.0**.

The app opens on a mobile landing (home, JioHotstar, OSR, sports, entertainment, specials, junior), then the checkout. There is no player and no live payment.

## What is included

- Plan picker with the same duration tabs (1 / 3 / 12 months) and Mobile / Plus cards
- Nepal wallet accordion (Khalti, eSewa, ConnectIPS, Fonepay, GetPay)
- Stripe-only checkout for international zones: a hand-off to hosted Stripe Checkout (WebView), no card entry in the app
- Plan changes (upgrade, downgrade, renewal, extension) and Stripe cancel / resume on the Account screen
- Confirmation screen, then a subscribed home
- Prototype coupons `DGO10` / `DGO20` on Nepal checkout
- Dev overlay, same idea as the web prototype:

  `DEV · GEO / STATE` → **NP | ZA | ZB | ZC** and **OFF | SUB**

## SKUs (v3.0)

| Zone | Toggle | Currency | Sample Plus 3M |
| --- | --- | --- | --- |
| Nepal | NP | NPR | रू 799 · wallets · one-time |
| India & Middle East | ZA | USD | $14.99 · Stripe |
| USA / Europe / AU / NZ | ZB | USD | $29.99 · Stripe |
| South East Asia | ZC | USD | $17.99 · Stripe |

1-month plans have no live sports. 3-month Stripe plans collect the discounted monthly rate today and bill monthly for 3 months.

For the partner implementation guide, see [docs/mobiotics-implementation-guide.md](docs/mobiotics-implementation-guide.md).

## Run it

1. Install [Android Studio](https://developer.android.com/studio) (includes the JDK and Android SDK).
2. Open this folder as a project.
3. Let Gradle sync, then run the `app` configuration on an emulator or phone.

The checkout is a prototype: no live Stripe or Nepal wallet charge. Test decline card: `4000 0000 0000 0002`.
