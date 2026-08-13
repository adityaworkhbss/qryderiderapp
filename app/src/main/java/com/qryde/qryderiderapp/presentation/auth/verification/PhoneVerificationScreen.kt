package com.qryde.qryderiderapp.presentation.auth.verification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qryde.qryderiderapp.core.designsystem.QrydeLink
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary
import com.qryde.qryderiderapp.presentation.components.AuthHeaderIcon
import com.qryde.qryderiderapp.presentation.components.QrydeTextField

@Composable
fun PhoneVerificationScreen(
    onBackToLogin: () -> Unit,
    onSendOtp: (contact: String) -> Unit,
    viewModel: PhoneVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PhoneVerificationContent(
        uiState = uiState,
        onContactChanged = viewModel::onContactChanged,
        onBackToLogin = onBackToLogin,
        onSendOtp = { onSendOtp(uiState.contact) }
    )
}

@Composable
private fun PhoneVerificationContent(
    uiState: PhoneVerificationUiState,
    onContactChanged: (String) -> Unit,
    onBackToLogin: () -> Unit,
    onSendOtp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthHeaderIcon(icon = Icons.Filled.Email)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Verification email or phone number",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = QrydePrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        QrydeTextField(
            value = uiState.contact,
            onValueChange = onContactChanged,
            label = "Email / Phone number",
            placeholder = "Add registered mail or number",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "← Back to Login page",
            color = QrydeLink,
            modifier = Modifier.clickable(onClick = onBackToLogin)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSendOtp,
            enabled = uiState.isSendEnabled,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Send OTP", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneVerificationContentPreview() {
    PhoneVerificationContent(
        uiState = PhoneVerificationUiState(),
        onContactChanged = {},
        onBackToLogin = {},
        onSendOtp = {}
    )
}
