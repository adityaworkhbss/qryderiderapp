package com.qryde.qryderiderapp.presentation.profile

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qryde.qryderiderapp.core.designsystem.QrydeFieldBackground
import com.qryde.qryderiderapp.core.designsystem.QrydePrimary

private data class ProfileOption(val label: String, val icon: ImageVector)

private val ProfileOptions = listOf(
    ProfileOption("Community", Icons.Filled.Groups),
    ProfileOption("Language", Icons.Filled.Language),
    ProfileOption("App Guide", Icons.Filled.MenuBook),
    ProfileOption("Help", Icons.Filled.HelpOutline),
    ProfileOption("About", Icons.Filled.Info)
)

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onHelp: () -> Unit,
    onLogout: () -> Unit
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCommunityDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedCommunity by remember { mutableStateOf("Downtown Community") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(
            "Profile",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(onClick = onEditProfile)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(QrydeFieldBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(28.dp))
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text("Jordan Smith", fontWeight = FontWeight.Bold)
                Text("jordan.smith@email.com", color = Color(0xFF6B6B6B), style = MaterialTheme.typography.labelLarge)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Edit Profile", tint = Color(0xFF9AA0A6))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            ProfileOptions.forEachIndexed { index, option ->
                ProfileOptionRow(
                    option = option,
                    trailingText = when (option.label) {
                        "Language" -> selectedLanguage
                        "Community" -> selectedCommunity
                        else -> null
                    },
                    onClick = {
                        when (option.label) {
                            "Language" -> showLanguageDialog = true
                            "Community" -> showCommunityDialog = true
                            "Help" -> onHelp()
                            else -> {}
                        }
                    }
                )
                if (index != ProfileOptions.lastIndex) {
                    HorizontalDivider(color = QrydeFieldBackground)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(onClick = onLogout)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Logout, contentDescription = null, tint = Color(0xFFE53935))
            Text("Log Out", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 12.dp))
        }
    }

    if (showLanguageDialog) {
        SelectLanguageDialog(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showCommunityDialog) {
        SelectCommunityDialog(
            selectedCommunity = selectedCommunity,
            onCommunitySelected = { selectedCommunity = it },
            onDismiss = { showCommunityDialog = false }
        )
    }
}

@Composable
private fun ProfileOptionRow(
    option: ProfileOption,
    trailingText: String?,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(option.icon, contentDescription = null, tint = QrydePrimary, modifier = Modifier.size(22.dp))
        Text(option.label, modifier = Modifier.weight(1f).padding(start = 14.dp))
        trailingText?.let {
            Text(it, color = Color(0xFF9AA0A6), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(end = 6.dp))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF9AA0A6))
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(onEditProfile = {}, onHelp = {}, onLogout = {})
}
