package com.qryde.qryderiderapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBorder
import com.qryde.qryderiderapp.core.designsystem.QrydeHint
import com.qryde.qryderiderapp.core.designsystem.QrydeIconMuted
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

data class CountryCode(val dialCode: String, val flagEmoji: String)

val DefaultCountryCodes = listOf(
    CountryCode("+91", "🇮🇳"),
    CountryCode("+1", "🇺🇸"),
    CountryCode("+44", "🇬🇧"),
    CountryCode("+61", "🇦🇺"),
    CountryCode("+971", "🇦🇪")
)

private val FieldShape = RoundedCornerShape(14.dp)

@Composable
private fun FieldLabel(label: String, required: Boolean) {
    Text(
        text = if (required) "$label *" else label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold
    )
}

private val fieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = QrydeFieldBackground,
        unfocusedContainerColor = QrydeFieldBackground,
        disabledContainerColor = QrydeFieldBackground,
        focusedBorderColor = QrydePrimary,
        unfocusedBorderColor = QrydeFieldBorder,
        focusedLeadingIconColor = QrydePrimary,
        unfocusedLeadingIconColor = QrydeIconMuted
    )

@Composable
fun QrydeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    supportingText: String? = null,
    supportingTextColor: Color = QrydeHint,
    onFocusLost: (() -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            FieldLabel(label, required)
            Spacer(modifier = Modifier.height(6.dp))
        }
        var wasFocused by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = QrydeHint) },
            leadingIcon = leadingIcon?.let { icon -> { Icon(icon, contentDescription = null) } },
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = FieldShape,
            colors = fieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state ->
                    if (wasFocused && !state.isFocused) onFocusLost?.invoke()
                    wasFocused = state.isFocused
                    onFocusChanged?.invoke(state.isFocused)
                }
        )
        if (supportingText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = supportingText, color = supportingTextColor, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun QrydePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    isError: Boolean = false
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            FieldLabel(label, required)
            Spacer(modifier = Modifier.height(6.dp))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = QrydeHint) },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = FieldShape,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun QrydePhoneField(
    dialCode: String,
    onDialCodeChange: (String) -> Unit,
    number: String,
    onNumberChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    isError: Boolean = false,
    numberLeadingIcon: ImageVector? = null,
    countryCodes: List<CountryCode> = DefaultCountryCodes
) {
    Column(modifier = modifier) {
        FieldLabel(label, required)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            var isMenuExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.width(84.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(FieldShape)
                        .background(QrydeFieldBackground)
                        .border(BorderStroke(1.dp, QrydeFieldBorder), FieldShape)
                        .clickable { isMenuExpanded = true }
                        .padding(horizontal = 10.dp)
                ) {
                    Text(dialCode, fontWeight = FontWeight.Medium)
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = QrydeIconMuted)
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    countryCodes.forEach { country ->
                        DropdownMenuItem(
                            text = { Text("${country.flagEmoji}  ${country.dialCode}") },
                            onClick = {
                                onDialCodeChange(country.dialCode)
                                isMenuExpanded = false
                            }
                        )
                    }
                }
            }
            QrydeTextField(
                value = number,
                onValueChange = onNumberChange,
                label = "",
                placeholder = placeholder,
                leadingIcon = numberLeadingIcon,
                keyboardType = KeyboardType.Phone,
                isError = isError,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
