package com.qryde.qryderiderapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

private val AvailableLanguages = listOf("English", "Spanish", "French", "Chinese")

@Composable
fun SelectLanguageDialog(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                AvailableLanguages.forEach { language ->
                    val selected = language == selectedLanguage
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) QrydeFieldBackground else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable {
                                onLanguageSelected(language)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Text(language, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                        if (selected) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = QrydePrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = QrydePrimary) }
        }
    )
}
