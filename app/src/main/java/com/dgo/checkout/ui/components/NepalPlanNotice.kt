package com.dgo.checkout.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dgo.checkout.data.NEPAL_ACTIVE_PLAN_MESSAGE
import com.dgo.checkout.ui.theme.White

@Composable
fun NepalPlanNotice() {
    val uriHandler = LocalUriHandler.current
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(NEPAL_ACTIVE_PLAN_MESSAGE, color = White.copy(0.70f), fontSize = 14.sp, lineHeight = 22.sp)
        Spacer(Modifier.height(4.dp))
        TextButton(onClick = { uriHandler.openUri("mailto:support@watchdgo.com") }) {
            Text("Contact support · support@watchdgo.com", color = White.copy(0.85f), fontSize = 12.sp)
        }
    }
}
