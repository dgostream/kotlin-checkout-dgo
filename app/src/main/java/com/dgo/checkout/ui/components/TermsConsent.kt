package com.dgo.checkout.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dgo.checkout.data.Terms
import com.dgo.checkout.ui.CheckoutViewModel
import com.dgo.checkout.ui.theme.StripePurple
import com.dgo.checkout.ui.theme.White

private val TermsAccent = Color(0xFFC4B5FD)

@Composable
fun TermsConsent(vm: CheckoutViewModel) {
    val document = vm.terms.getValue(vm.region.stripe)
    Column {
        Row(
            Modifier.fillMaxWidth()
                .toggleable(value = vm.termsAccepted, role = Role.Checkbox, onValueChange = vm::acceptTerms)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(checked = vm.termsAccepted, onCheckedChange = null)
            Spacer(Modifier.width(10.dp))
            Text(document.acknowledgement, color = White.copy(0.75f), fontSize = 12.sp, lineHeight = 18.sp)
        }
        TermsLink(vm.region.stripe)
    }
}

@Composable
fun TermsLink(international: Boolean, label: String = "Read ${if (international) "International" else "Nepal"} Terms & Conditions") {
    var open by remember { mutableStateOf(false) }
    TextButton(onClick = { open = true }) {
        Text(label, color = StripePurple, fontSize = 12.sp)
    }
    if (open) TermsReader(international, onDismiss = { open = false })
}

@Composable
private fun TermsReader(initialInternational: Boolean, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val documents = remember(context) { Terms.load(context) }
    var international by remember { mutableStateOf(initialInternational) }
    val document = documents.getValue(international)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize().safeDrawingPadding().padding(12.dp), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().fillMaxHeight(0.97f),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, White.copy(0.12f)),
            color = Color(0xFF121017),
        ) {
            Column {
                Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Description, null, tint = TermsAccent, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Terms & Conditions", color = White, fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.Bold)
                        Text("Subscription policy", color = White.copy(0.50f), fontSize = 12.sp, lineHeight = 18.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close terms", tint = White.copy(0.70f))
                    }
                }
                Row(Modifier.padding(horizontal = 20.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(0.30f)).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(false to "Nepal", true to "International").forEach { (value, label) ->
                        val selected = international == value
                        Surface(
                            onClick = { international = value },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(9.dp),
                            color = if (selected) Color(0xFF34254D) else Color.Transparent,
                        ) {
                            Box(Modifier.heightIn(min = 44.dp).padding(8.dp), contentAlignment = Alignment.Center) {
                                Text(label, color = if (selected) White else White.copy(0.55f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = White.copy(0.08f))
                key(international) {
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 24.dp)) {
                        Text(if (international) "International subscription" else "Nepal subscription", color = White, fontSize = 25.sp, lineHeight = 31.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Effective November 2026", color = TermsAccent, fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        Text(document.scope, color = White.copy(0.55f), fontSize = 12.sp, lineHeight = 19.sp)
                        Spacer(Modifier.height(20.dp))
                        Text(document.preamble, color = White.copy(0.85f), fontSize = 14.sp, lineHeight = 23.sp)
                        document.sections.forEachIndexed { index, section ->
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider(color = White.copy(0.08f))
                            Spacer(Modifier.height(20.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Box(Modifier.size(28.dp).clip(CircleShape).background(TermsAccent.copy(0.12f)), contentAlignment = Alignment.Center) {
                                    Text("${index + 1}", color = TermsAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    if (index == document.sections.lastIndex) "Subscriber acknowledgement" else section.title.substringAfter(". "),
                                    color = White, fontSize = 16.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            section.body.split(Regex("\\s+(?=\\d+\\.\\d+\\s|•\\s)")).forEach { paragraph ->
                                Spacer(Modifier.height(12.dp))
                                Row {
                                    if (paragraph.startsWith("•")) {
                                        Text("•", color = TermsAccent, modifier = Modifier.width(16.dp), fontSize = 14.sp, lineHeight = 23.sp)
                                    }
                                    Text(paragraph.removePrefix("•").trimStart(), color = White.copy(0.72f), fontSize = 14.sp, lineHeight = 23.sp, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Questions about your subscription?", color = White.copy(0.60f), fontSize = 13.sp, lineHeight = 20.sp)
                        Text("support@watchdgo.com", color = TermsAccent, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
                HorizontalDivider(color = White.copy(0.08f))
                Box(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = RoundedCornerShape(12.dp)) {
                        Text("Done", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        }
    }
}
