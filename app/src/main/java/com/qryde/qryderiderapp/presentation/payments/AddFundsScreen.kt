package com.qryde.qryderiderapp.presentation.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

private val QuickAmounts = listOf("$10", "$20", "$50", "$100")

private data class FundsPaymentMethod(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val FundsPaymentMethods = listOf(
    FundsPaymentMethod("Add Credit/Debit Card", Icons.Filled.CreditCard),
    FundsPaymentMethod("PayPal", Icons.Filled.AccountBalanceWallet),
    FundsPaymentMethod("Upi/Google Pay", Icons.Filled.AccountBalanceWallet)
)

@Composable
fun AddFundsScreen(
    onBack: () -> Unit,
    onFundsAdded: (amount: String, method: String) -> Unit
) {
    var selectedQuickAmount by remember { mutableStateOf<String?>(null) }
    var customAmount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf(FundsPaymentMethods.first().label) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Add Funds", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("QUICK AMOUNTS", style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAmounts.forEach { amount ->
                val selected = amount == selectedQuickAmount
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) QrydePrimary else QrydeFieldBackground)
                        .clickable {
                            selectedQuickAmount = amount
                            customAmount = ""
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(amount, color = if (selected) Color.White else Color(0xFF3C3C3C), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("CUSTOM AMOUNT", style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = customAmount,
            onValueChange = {
                customAmount = it
                selectedQuickAmount = null
            },
            placeholder = { Text("Enter amount") },
            leadingIcon = { Text("$", fontWeight = FontWeight.Bold) },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("SELECT PAYMENT METHOD", style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
        Spacer(modifier = Modifier.height(8.dp))

        FundsPaymentMethods.forEach { method ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { selectedMethod = method.label }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(QrydeFieldBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(method.icon, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(18.dp))
                }
                Text(method.label, modifier = Modifier.weight(1f).padding(start = 12.dp))
                RadioButton(
                    selected = method.label == selectedMethod,
                    onClick = { selectedMethod = method.label },
                    colors = RadioButtonDefaults.colors(selectedColor = QrydePrimary)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        val amountToAdd = selectedQuickAmount ?: customAmount.takeIf { it.isNotBlank() }?.let { "$$it" }
        Button(
            onClick = { amountToAdd?.let { onFundsAdded(it, selectedMethod) } },
            enabled = amountToAdd != null,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Add Funds", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun AddFundsScreenPreview() {
    AddFundsScreen(onBack = {}, onFundsAdded = { _, _ -> })
}
