package com.qryde.qryderiderapp.presentation.navigation

import kotlinx.serialization.Serializable

// Nested-graph markers - never carry business data, only mark where a graph starts.
@Serializable
data object AuthGraphRoute

@Serializable
data object MainGraphRoute

// Auth graph destinations
@Serializable
data object LoginRoute

// Main graph destinations
@Serializable
data object HomeRoute
