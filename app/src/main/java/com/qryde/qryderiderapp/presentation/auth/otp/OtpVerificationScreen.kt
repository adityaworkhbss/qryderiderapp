package com.qryde.qryderiderapp.presentation.auth.otp

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.qryde.qryderiderapp.core.designsystem.QrydeError
import com.qryde.qryderiderapp.core.designsystem.QrydeLink
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary
import com.qryde.qryderiderapp.presentation.components.AuthHeaderIcon
import com.qryde.qryderiderapp.presentation.components.OtpInputField

@Composable
fun OtpVerificationScreen(
    contact: String,
    expectedCode: String,
    onBack: () -> Unit,
    onVerified: () -> Unit,
    viewModel: OtpVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                OtpVerificationEvent.Verified -> onVerified()
            }
        }
    }

    OtpVerificationContent(
        contact = contact,
        uiState = uiState,
        onOtpChanged = viewModel::onOtpChanged,
        onResendClicked = viewModel::onResendClicked,
        onBack = onBack,
        onVerifyClicked = { viewModel.onVerifyClicked(expectedCode) }
    )
}

@Composable
private fun OtpVerificationContent(
    contact: String,
    uiState: OtpVerificationUiState,
    onOtpChanged: (String) -> Unit,
    onResendClicked: () -> Unit,
    onBack: () -> Unit,
    onVerifyClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            AuthHeaderIcon(icon = Icons.Filled.Lock)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Verify Your Number",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = QrydePrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter the 6-digit code sent to ${contact.ifBlank { "your registered contact" }}",
                color = Color(0xFF6B6B6B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            OtpInputField(
                value = uiState.otp,
                onValueChange = onOtpChanged,
                length = OtpVerificationUiState.OTP_LENGTH,
                modifier = Modifier.fillMaxWidth()
            )

            uiState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = message, color = QrydeError, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.canResend) {
                Text(
                    text = "Resend code",
                    color = QrydeLink,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onResendClicked)
                )
            } else {
                Text(
                    text = "Resend code in %02d:%02d".format(
                        uiState.remainingSeconds / 60,
                        uiState.remainingSeconds % 60
                    ),
                    color = Color(0xFF6B6B6B)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onVerifyClicked,
                enabled = uiState.isVerifyEnabled,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Verify", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpVerificationContentPreview() {
    OtpVerificationContent(
        contact = "+1 (XXX) XXX-XXXX",
        uiState = OtpVerificationUiState(otp = "91"),
        onOtpChanged = {},
        onResendClicked = {},
        onBack = {},
        onVerifyClicked = {}
    )
}
