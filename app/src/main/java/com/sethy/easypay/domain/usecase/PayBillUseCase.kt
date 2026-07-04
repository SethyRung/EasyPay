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
        val amountMinor = (amountMajor * 100).toLong()
        return walletRepository.payBill(billerCode, accountNumber, amountMinor, note)
    }
}