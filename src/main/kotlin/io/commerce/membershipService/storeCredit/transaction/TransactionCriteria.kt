package io.commerce.membershipService.storeCredit.transaction

import org.hibernate.validator.constraints.Range
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import javax.validation.constraints.Min

data class TransactionCriteria(
    /**
     * 거래 내역 구분
     */
    val type: TransactionType,

    /**
     * 페이지 인덱스
     */
    @field: Min(0)
    val page: Int = 0,

    /**
     * 페이지당 조회할 적립금 거래내역 개수
     */
    @field: Range(min = 5, max = 25)
    val size: Int = 25
) {
    fun toPageRequest(sort: Sort = Sort.unsorted()) = PageRequest.of(page, size, sort)
}
