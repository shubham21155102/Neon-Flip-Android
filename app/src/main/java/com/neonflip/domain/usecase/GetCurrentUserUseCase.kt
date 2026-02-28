package com.neonflip.domain.usecase

import com.neonflip.core.common.Result
import com.neonflip.domain.model.User
import com.neonflip.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for getting the current authenticated user
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return authRepository.getCurrentUser()
    }
}
