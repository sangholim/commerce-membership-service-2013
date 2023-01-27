package io.commerce.membershipService.membership.policy

/**
 * 다음 회원 등급 데이터 가져오기
 * @param index 리스트내에 회원등급 데이터 위치
 */
fun List<MembershipPolicy>.nextMembershipPolicy(index: Int) =
    this.getOrNull(index.plus(1))
