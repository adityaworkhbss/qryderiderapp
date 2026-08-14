package com.qryde.qryderiderapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPaymentMethodSheet(
    onDismiss: () -> Unit,
    onSelected: (payWithCard: Boolean) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            "Select Payment Method",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Other",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B6B6B),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        PaymentMethodRow(
            icon = Icons.Filled.CreditCard,
            label = "Add Credit or Debit Card",
            onClick = { onSelected(true) }
        )
        PaymentMethodRow(
            icon = Icons.Filled.AccountBalanceWallet,
            label = "PayPal",
            onClick = { onSelected(true) }
        )
        Text(
            "Upi",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B6B6B),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        PaymentMethodRow(
            icon = Icons.Filled.CreditCard,
            label = "Google Pay",
            onClick = { onSelected(true) }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PaymentMethodRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(QrydeFieldBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(16.dp))
        }
        Text(label, modifier = Modifier.padding(start = 12.dp))
    }
}
