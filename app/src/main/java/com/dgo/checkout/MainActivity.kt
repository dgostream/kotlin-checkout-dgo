package com.dgo.checkout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.dgo.checkout.ui.CheckoutViewModel
import com.dgo.checkout.ui.DgoCheckoutApp
import com.dgo.checkout.ui.theme.DgoCheckoutTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CheckoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        setContent {
            DgoCheckoutTheme {
                DgoCheckoutApp(viewModel)
            }
        }
    }
}
