package com.qryde.qryderiderapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary
import com.qryde.qryderiderapp.presentation.components.QrydeTextField

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var fullName by remember { mutableStateOf("Jordan Smith") }
    var email by remember { mutableStateOf("jordan.smith@email.com") }
    var phone by remember { mutableStateOf("9876543210") }
    var homeAddress by remember { mutableStateOf("123 Park Ave, New York") }
    var officeAddress by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Edit Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD9D9D9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(QrydePrimary)
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Change photo", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        QrydeTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            placeholder = "Full Name",
            leadingIcon = Icons.Filled.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        QrydeTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "name@email.com",
            leadingIcon = Icons.Filled.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        QrydeTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            placeholder = "Phone Number",
            leadingIcon = Icons.Filled.Phone
        )

        Spacer(modifier = Modifier.height(16.dp))

        QrydeTextField(
            value = homeAddress,
            onValueChange = { homeAddress = it },
            label = "Home Address",
            placeholder = "Home Address",
            leadingIcon = Icons.Filled.Home
        )

        Spacer(modifier = Modifier.height(16.dp))

        QrydeTextField(
            value = officeAddress,
            onValueChange = { officeAddress = it },
            label = "Office Address",
            placeholder = "Office Address",
            leadingIcon = Icons.Filled.Work
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSaved,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QrydePrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Save", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    EditProfileScreen(onBack = {}, onSaved = {})
}
