package com.qryde.qryderiderapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val AddressSuggestionIconColor = Color(0xFF9AA0A6)
private val AddressSuggestionSubtitleColor = Color(0xFF6B6B6B)

@Composable
fun AddressSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    // Mirrors ivClearPickup/ivClearDestination from the legacy screen: clearing
    // the query also hands focus back to the field, matching requestFocus().
    val focusRequester = remember { FocusRequester() }

    QrydeTextField(
        value = value,
        onValueChange = onValueChange,
        label = "",
        placeholder = "Search your destination",
        leadingIcon = Icons.Filled.Search,
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = {
                    onValueChange("")
                    focusRequester.requestFocus()
                }) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear search")
                }
            }
        } else null,
        focusRequester = focusRequester,
        onFocusChanged = onFocusChanged,
        modifier = modifier
    )
}

@Composable
fun <T> AddressSuggestionsCard(
    items: List<T>,
    title: (T) -> String,
    subtitle: (T) -> String,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String? = null
) {
    if (items.isEmpty() && emptyMessage == null) return

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            if (items.isEmpty()) {
                Text(
                    emptyMessage.orEmpty(),
                    color = AddressSuggestionIconColor,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                )
            } else {
                items.forEach { item ->
                    AddressSuggestionRow(
                        title = title(item),
                        subtitle = subtitle(item),
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun AddressSuggestionRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = AddressSuggestionIconColor)
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(title, fontWeight = FontWeight.Medium)
            if (subtitle.isNotBlank()) {
                Text(subtitle, style = MaterialTheme.typography.labelLarge, color = AddressSuggestionSubtitleColor)
            }
        }
    }
}
