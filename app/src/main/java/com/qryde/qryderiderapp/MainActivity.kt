package com.qryde.qryderiderapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.qryde.qryderiderapp.core.braintree.BraintreeDropInBridge
import com.qryde.qryderiderapp.core.designsystem.QrydeRiderTheme
import com.qryde.qryderiderapp.presentation.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var braintreeDropInBridge: BraintreeDropInBridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        braintreeDropInBridge.attach(this)
        setContent {
            QrydeRiderTheme {
                AppNavGraph()
            }
        }
    }

    override fun onDestroy() {
        braintreeDropInBridge.detach()
        super.onDestroy()
    }
}
