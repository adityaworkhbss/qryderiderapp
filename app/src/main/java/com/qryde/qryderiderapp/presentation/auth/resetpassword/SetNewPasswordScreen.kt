package com.qryde.qryderiderapp.presentation.auth.resetpassword

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary
import com.qryde.qryderiderapp.presentation.components.AuthHeaderIcon
import com.qryde.qryderiderapp.presentation.components.QrydePasswordField

@Composable
fun SetNewPasswordScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: SetNewPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SetNewPasswordContent(
        uiState = uiState,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onPasswordVisibilityToggled = viewModel::onPasswordVisibilityToggled,
        onConfirmPasswordVisibilityToggled = viewModel::onConfirmPasswordVisibilityToggled,
        onBack = onBack,
        onSaveClicked = onSaved
    )
}

@Composable
private fun SetNewPasswordContent(
    uiState: SetNewPasswordUiState,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggled: () -> Unit,
    onConfirmPasswordVisibilityToggled: () -> Unit,
    onBack: () -> Unit,
    onSaveClicked: () -> Unit
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
                text = "Set New password",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = QrydePrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Set your new password",
                color = Color(0xFF6B6B6B)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        QrydePasswordField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = "",
            placeholder = "Enter Your New Password",
            isVisible = uiState.isPasswordVisible,
            onVisibilityToggle = onPasswordVisibilityToggled
        )

        Spacer(modifier = Modifier.height(16.dp))

        QrydePasswordField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChanged,
            label = "",
            placeholder = "Confirm Password",
            isVisible = uiState.isConfirmPasswordVisible,
            onVisibilityToggle = onConfirmPasswordVisibilityToggled,
            isError = uiState.confirmPassword.isNotBlank() && !uiState.passwordsMatch
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSaveClicked,
            enabled = uiState.isSaveEnabled,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Save", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SetNewPasswordContentPreview() {
    SetNewPasswordContent(
        uiState = SetNewPasswordUiState(),
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onPasswordVisibilityToggled = {},
        onConfirmPasswordVisibilityToggled = {},
        onBack = {},
        onSaveClicked = {}
    )
}
