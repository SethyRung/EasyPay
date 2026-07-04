package com.sethy.easypay.data.model

data class FeaturedGame(
    val id: String,
    val name: String,
    val priceMajor: Double,
    val currency: String = "USD",
    val category: String,
    val coverColorHex: String
)