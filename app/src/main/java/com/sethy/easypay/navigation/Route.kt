package com.sethy.easypay.navigation

sealed class Route(val route: String) {
    data object Onboarding : Route("onboarding")
    data object Login : Route("login")
    data object Signup : Route("signup")
    data object Home : Route("home")
    data object Notifications : Route("notifications")
    data object Profile : Route("profile")

    data object SendMoney : Route("sendMoney?recipientName={recipientName}") {
        fun create(recipientName: String = "Nayantara V") =
            "sendMoney?recipientName=$recipientName"
    }

    data object TransferSuccess :
        Route("transferSuccess?recipientName={recipientName}&amount={amount}") {
        fun create(recipientName: String, amount: Double) =
            "transferSuccess?recipientName=$recipientName&amount=$amount"
    }

    data object TransactionDetail : Route("transactionDetail/{id}") {
        fun create(id: String) = "transactionDetail/$id"
    }
}
