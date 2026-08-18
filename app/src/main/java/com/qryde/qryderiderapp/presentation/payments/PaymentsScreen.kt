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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

@Composable
fun PaymentsScreen(
    onAddFunds: () -> Unit,
    viewModel: PaymentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PaymentsContent(
        uiState = uiState,
        purchases = viewModel.purchases,
        onTabSelected = viewModel::onTabSelected,
        onAddFunds = onAddFunds,
        onFilterSheetRequested = viewModel::onFilterSheetRequested,
        onFilterSheetDismissed = viewModel::onFilterSheetDismissed,
        onQuickFilterSelected = viewModel::onQuickFilterSelected,
        onFilterCleared = viewModel::onFilterCleared,
        onFilterApplied = viewModel::onFilterApplied
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentsContent(
    uiState: PaymentsUiState,
    purchases: List<PurchaseItem>,
    onTabSelected: (PaymentsTab) -> Unit,
    onAddFunds: () -> Unit,
    onFilterSheetRequested: () -> Unit,
    onFilterSheetDismissed: () -> Unit,
    onQuickFilterSelected: (String) -> Unit,
    onFilterCleared: () -> Unit,
    onFilterApplied: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(
            "Payments",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(listOf(QrydePrimary, Color(0xFF164712)))
                )
                .padding(16.dp)
        ) {
            Text("AVAILABLE BALANCE", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$${"%.2f".format(uiState.availableFunds)}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = onAddFunds,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.height(16.dp))
                    Text(" Add Funds", modifier = Modifier.padding(start = 2.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(QrydeFieldBackground)
        ) {
            listOf(
                PaymentsTab.MY_QCARD to "My QCard",
                PaymentsTab.TRANSACTIONS to "Transactions",
                PaymentsTab.PURCHASES to "Purchases"
            ).forEach { (tab, label) ->
                val selected = tab == uiState.selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (selected) QrydePrimary else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (selected) Color.White else Color(0xFF6B6B6B), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState.selectedTab) {
            PaymentsTab.MY_QCARD -> MyQCardTab()
            PaymentsTab.TRANSACTIONS -> ListTab(
                title = "Transactions",
                onFilterClicked = onFilterSheetRequested
            ) {
                if (uiState.transactions.isEmpty()) {
                    Text(
                        "No transactions yet.",
                        color = Color(0xFF9AA0A6),
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    uiState.transactions.groupBy { it.dateGroup }.forEach { (date, items) ->
                        DateGroupHeader(date)
                        items.forEach { TransactionRow(it) }
                    }
                }
            }
            PaymentsTab.PURCHASES -> ListTab(
                title = "Purchases",
                onFilterClicked = onFilterSheetRequested
            ) {
                purchases.groupBy { it.dateGroup }.forEach { (date, items) ->
                    DateGroupHeader(date)
                    items.forEach { PurchaseRow(it) }
                }
            }
        }
    }

    if (uiState.showFilterSheet) {
        FilterByDateSheet(
            uiState = uiState,
            onDismiss = onFilterSheetDismissed,
            onQuickFilterSelected = onQuickFilterSelected,
            onClear = onFilterCleared,
            onApply = onFilterApplied
        )
    }
}

@Composable
private fun MyQCardTab() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No QCard linked yet.", color = Color(0xFF6B6B6B))
    }
}

@Composable
private fun ListTab(
    title: String,
    onFilterClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = onFilterClicked) {
            Icon(Icons.Filled.FilterAlt, contentDescription = "Filter", tint = QrydePrimary)
        }
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        content()
    }
}

@Composable
private fun DateGroupHeader(date: String) {
    Text(date, color = Color(0xFF6B6B6B), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}

@Composable
private fun PurchaseRow(item: PurchaseItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(item.title, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Ticket Type", style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
                Text(item.ticketType, color = Color(0xFF6B6B6B))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Discount Price", style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
                Text(item.price, fontWeight = FontWeight.SemiBold)
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun TransactionRow(item: TransactionItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(item.title, fontWeight = FontWeight.SemiBold)
                Text(item.time, style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
            }
            Text(item.paidAmount, color = QrydePrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Transaction ID", style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
            Text(item.transactionId, style = MaterialTheme.typography.labelLarge)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Deposit Amount", style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
            Text(item.depositAmount, style = MaterialTheme.typography.labelLarge)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterByDateSheet(
    uiState: PaymentsUiState,
    onDismiss: () -> Unit,
    onQuickFilterSelected: (String) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Filter by Date", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Today", "This Week", "This Month", "3M").forEach { filter ->
                    val selected = filter == uiState.selectedQuickFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) QrydePrimary else QrydeFieldBackground)
                            .clickable { onQuickFilterSelected(filter) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(filter, color = if (selected) Color.White else Color(0xFF6B6B6B), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("START DATE", style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
                    OutlinedTextField(
                        value = uiState.startDateLabel,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("END DATE", style = MaterialTheme.typography.labelLarge, color = Color(0xFF9AA0A6))
                    OutlinedTextField(
                        value = uiState.endDateLabel,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onClear) {
                    Text("Clear Filter", color = Color(0xFF6B6B6B))
                }
                Button(
                    onClick = onApply,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary)
                ) {
                    Text("Apply")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentsContentPreview() {
    PaymentsContent(
        uiState = PaymentsUiState(),
        purchases = SamplePurchases,
        onTabSelected = {},
        onAddFunds = {},
        onFilterSheetRequested = {},
        onFilterSheetDismissed = {},
        onQuickFilterSelected = {},
        onFilterCleared = {},
        onFilterApplied = {}
    )
}
