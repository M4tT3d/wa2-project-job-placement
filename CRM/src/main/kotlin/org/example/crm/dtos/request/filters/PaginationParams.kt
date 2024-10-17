package org.example.crm.dtos.request.filters

import jakarta.validation.constraints.Min
import org.example.crm.utils.PAGE_NUMBER
import org.example.crm.utils.PAGE_SIZE
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class PaginationParams(
    @field:Min(PAGE_NUMBER, message = "Page number must be greater than or equal to $PAGE_NUMBER")
    val pageNumber: Int = PAGE_NUMBER.toInt(),
    @field:Min(1, message = "Limit must be greater than or equal to 1")
    val limit: Int = PAGE_SIZE.toInt(),
) {
    fun toPageRequest(): PageRequest {
        return PageRequest.of(pageNumber - 1, limit)
    }

    fun toPageRequest(sorting: Sort): PageRequest {
        return PageRequest.of(pageNumber - 1, limit, sorting)
    }
}
