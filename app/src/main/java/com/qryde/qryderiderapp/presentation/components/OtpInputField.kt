package com.qryde.qryderiderapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicTextField
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBorder
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

@Composable
fun OtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    requestFocusOnAppear: Boolean = true
) {
    val focusRequester = remember { FocusRequester() }

    Box(modifier = modifier) {
        BasicTextField(
            value = TextFieldValue(text = value, selection = TextRange(value.length)),
            onValueChange = { field ->
                val digitsOnly = field.text.filter(Char::isDigit).take(length)
                if (digitsOnly != value) onValueChange(digitsOnly)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .alpha(0f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(length) { index ->
                val digit = value.getOrNull(index)?.toString().orEmpty()
                val isNextToFill = index == value.length
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(QrydeFieldBackground)
                        .border(
                            width = if (digit.isNotEmpty() || isNextToFill) 1.5.dp else 1.dp,
                            color = if (digit.isNotEmpty() || isNextToFill) QrydePrimary else QrydeFieldBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { focusRequester.requestFocus() }
                ) {
                    Text(digit, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (requestFocusOnAppear) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}
