package com.qryde.qryderiderapp.presentation.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qryde.qryderiderapp.R
import com.qryde.qryderiderapp.core.designsystem.QrydeLink
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary
import com.qryde.qryderiderapp.presentation.components.QrydePasswordField
import com.qryde.qryderiderapp.presentation.components.QrydeTextField

@Composable
fun LoginScreen(
    onLoginSuccess: (userId: String, isoCode: String) -> Unit,
    onPasswordResetRequired: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.LoginSucceeded -> onLoginSuccess(event.userId, event.isoCode)
                LoginEvent.PasswordResetRequired -> onPasswordResetRequired()
                is LoginEvent.ShowError -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LoginContent(
        uiState = uiState,
        onUserIdChanged = viewModel::onUserIdChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onPasswordVisibilityToggled = viewModel::onPasswordVisibilityToggled,
        onLoginClick = viewModel::onLoginClicked,
        onSignUpClick = onSignUpClick,
        onForgotPasswordClick = onForgotPasswordClick
    )
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onUserIdChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggled: () -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.qryde_logo),
            contentDescription = "QRyde",
            modifier = Modifier
                .width(140.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("Welcome to ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "QRyde Rider",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = QrydePrimary
            )
        }
        Text(
            text = "Login in to book your next microtransit ride across the region.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF6B6B6B),
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        QrydeTextField(
            value = uiState.userId,
            onValueChange = onUserIdChanged,
            label = "User ID",
            placeholder = "Enter your used Id",
            leadingIcon = Icons.Filled.Person,
            required = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        QrydePasswordField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = "Password",
            placeholder = "Enter your password",
            isVisible = uiState.isPasswordVisible,
            onVisibilityToggle = onPasswordVisibilityToggled,
            required = true
        )

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
            Text(
                text = "Forgot password?",
                color = QrydeLink,
                modifier = Modifier.clickable(onClick = onForgotPasswordClick)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLoginClick,
            enabled = uiState.isLoginEnabled,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Login", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "Don't have an account?",
                color = Color(0xFF6B6B6B),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onSignUpClick,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Sign up", fontWeight = FontWeight.SemiBold, color = QrydePrimary)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    LoginContent(
        uiState = LoginUiState(),
        onUserIdChanged = {},
        onPasswordChanged = {},
        onPasswordVisibilityToggled = {},
        onLoginClick = {},
        onSignUpClick = {},
        onForgotPasswordClick = {}
    )
}
