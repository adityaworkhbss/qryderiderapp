package com.qryde.qryderiderapp.presentation.navigation

import kotlinx.serialization.Serializable

// Top-level destination, not part of any nested graph.
@Serializable
data object SplashRoute

// Nested-graph marker - never carries business data, only marks where the graph starts.
@Serializable
data object MainGraphRoute

// Main graph destinations
@Serializable
data object HomeRoute
