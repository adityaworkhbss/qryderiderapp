package com.qryde.qryderiderapp.presentation.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.qryde.qryderiderapp.presentation.home.AdditionalInformationScreen
import com.qryde.qryderiderapp.presentation.home.BookRideViewModel
import com.qryde.qryderiderapp.presentation.home.HomeScreen
import com.qryde.qryderiderapp.presentation.payments.AddFundsScreen
import com.qryde.qryderiderapp.presentation.payments.PaymentsScreen
import com.qryde.qryderiderapp.presentation.profile.EditProfileScreen
import com.qryde.qryderiderapp.presentation.profile.HelpScreen
import com.qryde.qryderiderapp.presentation.profile.ProfileScreen
import com.qryde.qryderiderapp.presentation.trips.CancelTripScreen
import com.qryde.qryderiderapp.presentation.trips.ReceiptScreen
import com.qryde.qryderiderapp.presentation.trips.TripsScreen
import com.qryde.qryderiderapp.presentation.vui.VuiScreen

fun NavGraphBuilder.mainTabNavGraph(navController: NavController) {
    navigation<HomeGraphRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> { entry ->
            val parentEntry = remember(entry) { navController.getBackStackEntry(HomeGraphRoute) }
            val viewModel: BookRideViewModel = hiltViewModel(parentEntry)
            HomeScreen(
                onOpenAdditionalInformation = { navController.navigate(AdditionalInformationRoute) },
                viewModel = viewModel
            )
        }
        composable<AdditionalInformationRoute> { entry ->
            val parentEntry = remember(entry) { navController.getBackStackEntry(HomeGraphRoute) }
            val viewModel: BookRideViewModel = hiltViewModel(parentEntry)
            AdditionalInformationScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }

    composable<TripsRoute> {
        TripsScreen(
            onViewReceipt = { tripId -> navController.navigate(ReceiptRoute(tripId)) },
            onCancelTrip = { tripId -> navController.navigate(CancelTripRoute(tripId)) },
            onBookAgain = { navController.navigate(HomeGraphRoute) },
            onNewTrip = { navController.navigate(HomeGraphRoute) }
        )
    }
    composable<ReceiptRoute> { entry ->
        val route = entry.toRoute<ReceiptRoute>()
        ReceiptScreen(
            tripId = route.tripId,
            onBack = { navController.popBackStack() }
        )
    }
    composable<CancelTripRoute> { entry ->
        val route = entry.toRoute<CancelTripRoute>()
        CancelTripScreen(
            tripId = route.tripId,
            onBack = { navController.popBackStack() },
            onCancelConfirmed = { _, _, _ -> navController.popBackStack() }
        )
    }

    composable<PaymentsRoute> {
        PaymentsScreen(onAddFunds = { navController.navigate(AddFundsRoute) })
    }
    composable<AddFundsRoute> {
        AddFundsScreen(
            onBack = { navController.popBackStack() },
            onFundsAdded = { _, _ -> navController.popBackStack() }
        )
    }

    composable<ProfileRoute> {
        ProfileScreen(
            onEditProfile = { navController.navigate(EditProfileRoute) },
            onHelp = { navController.navigate(HelpRoute) },
            onLogout = {}
        )
    }
    composable<EditProfileRoute> {
        EditProfileScreen(
            onBack = { navController.popBackStack() },
            onSaved = { navController.popBackStack() }
        )
    }
    composable<HelpRoute> {
        HelpScreen(onBack = { navController.popBackStack() })
    }

    composable<VuiRoute> {
        VuiScreen()
    }
}
