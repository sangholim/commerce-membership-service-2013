---
title: Membership API v1.0.0-edge
language_tabs:
  - http: HTTP
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="membership-api">Membership API v1.0.0-edge</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

Base URLs:

* <a href="{host}">{host}</a>

    * **host** -  Default: api.commerce.io

        * api.commerce.io

        * api.commerce.co.kr

# Authentication

- HTTP Authentication, scheme: bearer 

<h1 id="membership-api-internal">Internal</h1>

## chargeStoreCredit

<a id="opIdchargeStoreCredit"></a>

> Code samples

```http
POST {host}/internal/membership/store-credit/account/{customerId}/charge HTTP/1.1

Content-Type: application/json
Accept: application/json

```

`POST /internal/membership/store-credit/account/{customerId}/charge`

*적립금 차감*

> Body parameter

```json
{
  "orderId": "string",
  "amount": 0
}
```

<h3 id="chargestorecredit-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|customerId|path|string|true|none|
|body|body|[ChargeStoreCreditPayload_Internal](#schemachargestorecreditpayload_internal)|true|none|

> Example responses

> 200 Response

```json
{
  "id": "string",
  "orderId": "string",
  "customerId": "string",
  "type": "deposit",
  "amount": 0,
  "note": "string",
  "createdAt": "2019-08-24T14:15:22Z"
}
```

<h3 id="chargestorecredit-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[TransactionView_Internal](#schematransactionview_internal)|

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="membership-api-admin">Admin</h1>

## refundStoreCredit

<a id="opIdrefundStoreCredit"></a>

> Code samples

```http
POST {host}/admin/membership/store-credit/account/{customerId}/refund HTTP/1.1

Content-Type: application/json

```

`POST /admin/membership/store-credit/account/{customerId}/refund`

*관리자 적립금 반환*

관리자 권한으로 적립금을 반환합니다.

> Body parameter

```json
{
  "orderNumber": "string",
  "amount": 0,
  "note": "string"
}
```

<h3 id="refundstorecredit-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|customerId|path|string|true|고객 ID|
|body|body|[RefundPayload](#schemarefundpayload)|true|적립금 반환 데이터|

<h3 id="refundstorecredit-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|204|[No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)|No Content|None|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
aegis
</aside>

## depositStoreCredit

<a id="opIddepositStoreCredit"></a>

> Code samples

```http
POST {host}/admin/membership/store-credit/account/{customerId}/deposit HTTP/1.1

Content-Type: application/json

```

`POST /admin/membership/store-credit/account/{customerId}/deposit`

*관리자 적립금 지급*

관리자 권한으로 적립금을 지급합니다.

> Body parameter

```json
{
  "amount": 0,
  "note": "string"
}
```

<h3 id="depositstorecredit-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|customerId|path|string|true|고객 ID|
|body|body|[DepositPayload](#schemadepositpayload)|true|적립금 지급 데이터|

<h3 id="depositstorecredit-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|204|[No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)|No Content|None|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
aegis
</aside>

<h1 id="membership-api--">적립금 거래 내역</h1>

## getTransactions

<a id="opIdgetTransactions"></a>

> Code samples

```http
GET {host}/membership/store-credit/transactions HTTP/1.1

Accept: application/json

```

`GET /membership/store-credit/transactions`

*적립금 거래 내역 조회*

적립금 거래 내역 조회

<h3 id="gettransactions-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|type|query|string|false|거래 내역 구분|
|page|query|integer(int32)|false|페이지 인덱스|
|size|query|integer(int32)|false|페이지당 조회할 적립금 거래내역 개수|

#### Enumerated Values

|Parameter|Value|
|---|---|
|type|deposit|
|type|charge|
|type|expire|

> Example responses

> 200 Response

```json
[
  {
    "type": "deposit",
    "amount": 0,
    "note": "string",
    "createdAt": "2019-08-24T14:15:22Z"
  }
]
```

<h3 id="gettransactions-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="gettransactions-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[TransactionView_Public](#schematransactionview_public)]|false|none|none|
|» type|string|true|none|거래 구분|
|» amount|integer(int32)|true|none|거래 금액|
|» note|string|true|none|거래 요약|
|» createdAt|string(date-time)|true|none|최초 생성일|

#### Enumerated Values

|Property|Value|
|---|---|
|type|deposit|
|type|charge|
|type|expire|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
aegis
</aside>

<h1 id="membership-api--">적립금 계좌</h1>

## getStoreCreditAccount

<a id="opIdgetStoreCreditAccount"></a>

> Code samples

```http
GET {host}/membership/store-credit/account HTTP/1.1

Accept: application/json

```

`GET /membership/store-credit/account`

*적립금 계좌 조회*

> Example responses

> 200 Response

```json
{
  "customerId": "string",
  "balance": 0,
  "amountToExpire": 0
}
```

<h3 id="getstorecreditaccount-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[StoreCreditAccountView](#schemastorecreditaccountview)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
aegis
</aside>

<h1 id="membership-api--">회원 등급 정책</h1>

## getMembershipPolicies

<a id="opIdgetMembershipPolicies"></a>

> Code samples

```http
GET {host}/membership/policies HTTP/1.1

Accept: application/json

```

`GET /membership/policies`

*회원 등급 정책 목록*

회원 등급 정책 목록

> Example responses

> 200 Response

```json
[
  {
    "level": 0,
    "name": "string",
    "minimumCredit": 0,
    "maximumCredit": 0,
    "creditRewardRate": 0
  }
]
```

<h3 id="getmembershippolicies-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getmembershippolicies-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[MembershipPolicyView](#schemamembershippolicyview)]|false|none|[회원 등급 정책 응답 데이터]|
|» level|integer(int32)|true|none|등급 레벨 1-4|
|» name|string|true|none|등급명|
|» minimumCredit|integer(int32)|true|none|최소 누적 실적금액|
|» maximumCredit|integer(int32)|false|none|최대 누적 실적금액|
|» creditRewardRate|number(double)|true|none|구매 확정(실적금액)시 적립율|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
aegis
</aside>

<h1 id="membership-api--">회원 등급</h1>

## getMembership

<a id="opIdgetMembership"></a>

> Code samples

```http
GET {host}/membership HTTP/1.1

Accept: application/json

```

`GET /membership`

*회원 등급 조회*

회원 등급 조회

> Example responses

> 200 Response

```json
{
  "name": "string",
  "creditRewardRate": 0
}
```

<h3 id="getmembership-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[MembershipView](#schemamembershipview)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
aegis
</aside>

# Schemas

<h2 id="tocS_ChargeStoreCreditPayload_Internal">ChargeStoreCreditPayload_Internal</h2>
<!-- backwards compatibility -->
<a id="schemachargestorecreditpayload_internal"></a>
<a id="schema_ChargeStoreCreditPayload_Internal"></a>
<a id="tocSchargestorecreditpayload_internal"></a>
<a id="tocschargestorecreditpayload_internal"></a>

```json
{
  "orderId": "string",
  "amount": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|orderId|string|true|none|주문 ID|
|amount|integer(int32)|true|none|사용할 적립금액|

<h2 id="tocS_TransactionView_Internal">TransactionView_Internal</h2>
<!-- backwards compatibility -->
<a id="schematransactionview_internal"></a>
<a id="schema_TransactionView_Internal"></a>
<a id="tocStransactionview_internal"></a>
<a id="tocstransactionview_internal"></a>

```json
{
  "id": "string",
  "orderId": "string",
  "customerId": "string",
  "type": "deposit",
  "amount": 0,
  "note": "string",
  "createdAt": "2019-08-24T14:15:22Z"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string|false|none|고유 ID|
|orderId|string|false|none|적립금 지급에 사용된 주문 ID|
|customerId|string|false|none|고객 ID|
|type|string|true|none|거래 구분|
|amount|integer(int32)|true|none|거래 금액|
|note|string|true|none|거래 요약|
|createdAt|string(date-time)|true|none|최초 생성일|

#### Enumerated Values

|Property|Value|
|---|---|
|type|deposit|
|type|charge|
|type|expire|

<h2 id="tocS_RefundPayload">RefundPayload</h2>
<!-- backwards compatibility -->
<a id="schemarefundpayload"></a>
<a id="schema_RefundPayload"></a>
<a id="tocSrefundpayload"></a>
<a id="tocsrefundpayload"></a>

```json
{
  "orderNumber": "string",
  "amount": 0,
  "note": "string"
}

```

적립금 반환 필드 데이터

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|orderNumber|string|true|none|주문 번호|
|amount|integer(int32)|true|none|적립금|
|note|string|true|none|지급 내역|

<h2 id="tocS_DepositPayload">DepositPayload</h2>
<!-- backwards compatibility -->
<a id="schemadepositpayload"></a>
<a id="schema_DepositPayload"></a>
<a id="tocSdepositpayload"></a>
<a id="tocsdepositpayload"></a>

```json
{
  "amount": 0,
  "note": "string"
}

```

적립금 지급 요청 데이터

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|amount|integer(int32)|true|none|적립금|
|note|string|true|none|지급 내역|

<h2 id="tocS_TransactionView_Public">TransactionView_Public</h2>
<!-- backwards compatibility -->
<a id="schematransactionview_public"></a>
<a id="schema_TransactionView_Public"></a>
<a id="tocStransactionview_public"></a>
<a id="tocstransactionview_public"></a>

```json
{
  "type": "deposit",
  "amount": 0,
  "note": "string",
  "createdAt": "2019-08-24T14:15:22Z"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|type|string|true|none|거래 구분|
|amount|integer(int32)|true|none|거래 금액|
|note|string|true|none|거래 요약|
|createdAt|string(date-time)|true|none|최초 생성일|

#### Enumerated Values

|Property|Value|
|---|---|
|type|deposit|
|type|charge|
|type|expire|

<h2 id="tocS_StoreCreditAccountView">StoreCreditAccountView</h2>
<!-- backwards compatibility -->
<a id="schemastorecreditaccountview"></a>
<a id="schema_StoreCreditAccountView"></a>
<a id="tocSstorecreditaccountview"></a>
<a id="tocsstorecreditaccountview"></a>

```json
{
  "customerId": "string",
  "balance": 0,
  "amountToExpire": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|customerId|string|true|none|고객 ID|
|balance|integer(int32)|true|none|사용 가능한 총액|
|amountToExpire|integer(int32)|true|none|다음달 소멸 예정 금액|

<h2 id="tocS_MembershipPolicyView">MembershipPolicyView</h2>
<!-- backwards compatibility -->
<a id="schemamembershippolicyview"></a>
<a id="schema_MembershipPolicyView"></a>
<a id="tocSmembershippolicyview"></a>
<a id="tocsmembershippolicyview"></a>

```json
{
  "level": 0,
  "name": "string",
  "minimumCredit": 0,
  "maximumCredit": 0,
  "creditRewardRate": 0
}

```

회원 등급 정책 응답 데이터

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|level|integer(int32)|true|none|등급 레벨 1-4|
|name|string|true|none|등급명|
|minimumCredit|integer(int32)|true|none|최소 누적 실적금액|
|maximumCredit|integer(int32)|false|none|최대 누적 실적금액|
|creditRewardRate|number(double)|true|none|구매 확정(실적금액)시 적립율|

<h2 id="tocS_MembershipView">MembershipView</h2>
<!-- backwards compatibility -->
<a id="schemamembershipview"></a>
<a id="schema_MembershipView"></a>
<a id="tocSmembershipview"></a>
<a id="tocsmembershipview"></a>

```json
{
  "name": "string",
  "creditRewardRate": 0
}

```

회원 등급 요약 응답 데이터

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|true|none|등급 이름|
|creditRewardRate|number(double)|true|none|구매 확정(실적금액)시 적립율|

