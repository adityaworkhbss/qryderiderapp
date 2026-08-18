package com.qryde.qryderiderapp.presentation.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.qryde.qryderiderapp.presentation.navigation.HomeGraphRoute
import com.qryde.qryderiderapp.presentation.navigation.PaymentsRoute
import com.qryde.qryderiderapp.presentation.navigation.ProfileRoute
import com.qryde.qryderiderapp.presentation.navigation.TripsRoute
import com.qryde.qryderiderapp.presentation.navigation.VuiRoute
import com.qryde.qryderiderapp.presentation.navigation.mainTabNavGraph

private data class BottomNavTab(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val matches: (NavDestination) -> Boolean
)

private val bottomNavTabs = listOf(
    BottomNavTab(HomeGraphRoute, "Home", Icons.Filled.Home) { it.hasRoute<HomeGraphRoute>() },
    BottomNavTab(TripsRoute, "Trips", Icons.Filled.CalendarMonth) { it.hasRoute<TripsRoute>() },
    BottomNavTab(PaymentsRoute, "Payments", Icons.Filled.CreditCard) { it.hasRoute<PaymentsRoute>() },
    BottomNavTab(ProfileRoute, "Profile", Icons.Filled.Person) { it.hasRoute<ProfileRoute>() },
    BottomNavTab(VuiRoute, "Vui", Icons.Filled.GraphicEq) { it.hasRoute<VuiRoute>() }
)

@Composable
fun MainScaffold() {
    hiltViewModel<BraintreeViewModel>()

    val communitySelectionViewModel = hiltViewModel<CommunitySelectionViewModel>()
    val communitySelectionState by communitySelectionViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        communitySelectionViewModel.errorEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar {
                bottomNavTabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any(tab.matches) == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeGraphRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            mainTabNavGraph(navController)
        }
    }

    when (val state = communitySelectionState) {
        CommunitySelectionUiState.Hidden -> Unit
        CommunitySelectionUiState.EnableLocationPrompt -> EnableLocationDialog(
            onUseMyLocation = communitySelectionViewModel::onUseMyLocationClicked,
            onSkip = communitySelectionViewModel::onSkipped
        )
        CommunitySelectionUiState.ResolvingLocation -> ResolvingLocationDialog()
        is CommunitySelectionUiState.PickCommunity -> SelectStateCommunityDialog(
            communities = state.communities,
            nearestCommunityId = state.nearestCommunityId,
            onCommunitySelected = communitySelectionViewModel::onCommunitySelected,
            onDismiss = communitySelectionViewModel::onSkipped
        )
    }
}

@Composable
private fun ResolvingLocationDialog() {
    Dialog(onDismissRequest = {}) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
            Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
