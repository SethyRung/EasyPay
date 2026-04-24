package com.sethy.easypay.navigation

/**
 * Navigation routes for the app
 */
sealed class Route(val route: String) {
    object Onboarding : Route("onboarding")
    object Login : Route("login")
    object Signup : Route("signup")
    object Home : Route("home")

    object SendMoney : Route("sendMoney?recipientName={recipientName}") {
        fun create(recipientName: String = "Nayantara V") =
            "sendMoney?recipientName=$recipientName"
    }

    object TransferSuccess : Route("transferSuccess?recipientName={recipientName}&amount={amount}") {
        fun create(recipientName: String, amount: Double) =
            "transferSuccess?recipientName=$recipientName&amount=$amount"
    }

    object Profile : Route("profile")
}
