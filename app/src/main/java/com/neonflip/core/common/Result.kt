package com.neonflip.core.common

/**
 * A generic wrapper for handling success, error, and loading states
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }

    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }

    suspend fun <R> suspendMap(transform: suspend (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(exception: Throwable, message: String? = null): Result<Nothing> = Error(exception, message)
        fun loading(): Result<Nothing> = Loading
    }
}

/**
 * Extension function to wrap suspending operations in Result
 */
suspend fun <T> resultOf(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: Exception) {
    Result.error(e, e.message)
}

/**
 * Extension function to wrap regular operations in Result
 */
fun <T> resultOfNow(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: Exception) {
    Result.error(e, e.message)
}
