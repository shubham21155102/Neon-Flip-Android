package com.neonflip.domain.usecase

import com.neonflip.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for logging out the current user
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}
