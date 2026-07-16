package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.source.BillPayment
import com.sethy.easypay.data.repository.WalletRepository
import javax.inject.Inject

class PayBillUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(
        billerCode: String,
        accountNumber: String,
        amountMajor: Double,
        note: String?
    ): Result<BillPayment> {
        require(amountMajor > 0) { "Bill amount must be positive" }
        return walletRepository.payBill(billerCode, accountNumber, amountMajor, note)
    }
}
