package com.caldeirasoft.castly.domain.datasource

import com.caldeirasoft.castly.domain.util.Status

/**
 * A generic class that holds a value with its loading status.
 *
 */
data class PaginationResource(val status: Status, val message: String?) {
    companion object {
        fun success(): PaginationResource {
            return PaginationResource(Status.SUCCESS, null)
        }

        fun empty(): PaginationResource {
            return PaginationResource(Status.EMPTY, null)
        }

        fun error(msg: String): PaginationResource {
            return PaginationResource(Status.ERROR, msg)
        }

        fun loading(): PaginationResource {
            return PaginationResource(Status.LOADING, null)
        }
    }
}