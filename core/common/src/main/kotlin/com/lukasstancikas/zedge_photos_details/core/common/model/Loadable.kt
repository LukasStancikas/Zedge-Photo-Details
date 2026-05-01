package com.lukasstancikas.zedge_photos_details.core.common.model

sealed interface Loadable<out T> {
    data object Loading : Loadable<Nothing>
    data class Success<T>(val data: T) : Loadable<T>
    data class Error(val throwable: Throwable) : Loadable<Nothing>

    fun <R> map(transform: (T) -> R): Loadable<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(throwable)
            is Loading -> Loading
        }
    }

    companion object {
        fun <T, R> Loadable<List<T>>.mapAll(transform: (T) -> R): Loadable<List<R>> {
            return when (this) {
                is Success -> Success(data.map { transform(it) })
                is Error -> Error(throwable)
                Loading -> Loading
            }
        }
    }

}
