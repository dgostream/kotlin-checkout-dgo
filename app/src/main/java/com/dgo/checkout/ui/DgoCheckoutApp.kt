package com.dgo.checkout.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.R
import com.dgo.checkout.data.Screen
import com.dgo.checkout.ui.components.DevGeoToggle
import com.dgo.checkout.ui.screens.AccountScreen
import com.dgo.checkout.ui.screens.ChoosePlanScreen
import com.dgo.checkout.ui.screens.ConfirmationScreen
import com.dgo.checkout.ui.screens.LandingScreen
import com.dgo.checkout.ui.screens.PaymentScreen
import com.dgo.checkout.ui.theme.White

@Composable
fun DgoCheckoutApp(vm: CheckoutViewModel) {
    BackHandler(enabled = vm.screen != Screen.HOME || vm.detail != null || vm.searchOpen) {
        when {
            vm.detail != null -> vm.detail = null
            vm.searchOpen -> vm.searchOpen = false
            else -> vm.back()
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Column(Modifier.fillMaxSize().then(if (vm.screen == Screen.HOME) Modifier else Modifier.statusBarsPadding())) {
            if (vm.screen == Screen.CHECKOUT) {
                CheckoutHeader(vm)
            }
            AnimatedContent(
                targetState = vm.screen to vm.step,
                transitionSpec = {
                    (slideInHorizontally { it / 4 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 4 } + fadeOut())
                },
                label = "checkout-step",
                modifier = Modifier.weight(1f),
            ) { (screen, step) ->
                when {
                    screen == Screen.HOME -> LandingScreen(vm)
                    screen == Screen.ACCOUNT -> AccountScreen(vm)
                    step == 0 -> ChoosePlanScreen(vm)
                    step == 1 -> PaymentScreen(vm)
                    else -> ConfirmationScreen(vm)
                }
            }
        }

        DevGeoToggle(
            region = vm.region,
            subscribed = vm.session != null,
            onRegion = vm::setDevRegion,
            onSubscribed = vm::setSubscribed,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(
                    bottom = when {
                        vm.screen == Screen.HOME -> 78.dp
                        vm.screen == Screen.CHECKOUT && vm.step < 2 -> 84.dp
                        else -> 12.dp
                    },
                ),
        )
    }
}

@Composable
private fun CheckoutHeader(vm: CheckoutViewModel) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(0.80f))
            .border(width = 0.dp, color = Color.Transparent)
            .padding(bottom = 4.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier.clickable(onClick = vm::back),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.ArrowBack, null, tint = White.copy(0.50f), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Back", color = White.copy(0.50f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                when (vm.step) {
                    0 -> if (vm.manageMode) "Change plan" else "Choose a plan"
                    1 -> "Payment"
                    else -> "Confirmed"
                },
                color = White.copy(0.80f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            )
            Image(
                painterResource(R.drawable.dgo_logo),
                contentDescription = "DGO",
                modifier = Modifier.height(28.dp),
                contentScale = ContentScale.Fit,
            )
        }
        if (vm.step < 2) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(White.copy(0.08f)),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth((vm.step + 1) / 3f)
                        .height(3.dp)
                        .background(com.dgo.checkout.ui.theme.BrandGradient),
                )
            }
        }
    }
}
