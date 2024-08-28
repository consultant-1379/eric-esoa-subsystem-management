# Subsystem Management Service

- [Get List Of Subsystems](#get-list-of-subsystems)
- [Create Subsystems](#create-subsystems)
- [Delete Multiple Subsystems](#delete-multiple-subsystems)
- [Read Subsystem](#read-subsystem)
- [Update Subsystem](#update-subsystem)
- [Delete Subsystem](#delete-subsystem)
- [Update Subsystem Information](#update-subsystem-information)
- [Error Data Structure](#subsystem-management-error-data-structure)

## Get List Of Subsystems

This API is used to read the whole set of subsystems filtered out by query
parameters or retrieves the Subsystems mappings in a paginated way to specifically
support GUI operations by means of query parameters.

**Path:** GET /subsystem-manager/v1/subsystems

**Query Parameters:**

- ``select`` (optional) |Subsystem identifier.
- ``offset`` (optional) |Specifies the starting element of the page of Subsystems.
- ``limit`` (optional) |The maximum number of Subsystems results to be returned,
   starting with offset (dictates size of result page).
- ``sortAttr`` (optional) |A Subsystem attribute by which to sort the Subsystems
   mappings.
- ``sortDir`` (optional) |The direction in which to sort the Subsystems.
   Has no effect if sortAttr is not also specified. May be ``ASCENDING``, ``DESCENDING``,
   or any fragment from the beginning of either (e.g. ASC or DESC); case-insensitive.
   Defaults to ``ASCENDING``.
- ``filters`` (optional) |Additional filter parameters for the Subsystems resources
   to be returned.
- ``tenantName`` (optional) |Tenant name.
- ``paramsMap`` (optional) |Array of Subsystem info.

**Request header:** Content-Type: application/json

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - OK**| Information about subsystems was read successfully.| See Below|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-management-error-data-structure)|

**200 - OK Response:**

```json
[
    {
        "id": 2,
        "subsystemTypeId": 1,
        "name": "enm",
        "url": "https://eric-eo-enm-stub",
        "connectionProperties": [
            {
                "id": 3,
                "subsystemId": 2,
                "username": "anything",
                "password": "anything",
                "encryptedKeys": [
                    "password"
                ],
                "subsystemUsers": []
            }
        ],
        "vendor": "Ericsson",
        "subsystemType": {
            "id": 1,
            "type": "DomainManager",
            "category": "Primary"
        }
    },
    {
        "id": 6,
        "subsystemTypeId": 2,
        "name": "ecm",
        "url": "https://eric-eo-ecm-stub",
        "connectionProperties": [
            {
                "id": 7,
                "subsystemId": 6,
                "name": "ECM",
                "tenant": "tenant1",
                "username": "ecmadmin",
                "password": "CloudAdmin123",
                "encryptedKeys": [
                    "password"
                ],
                "subsystemUsers": []
            }
        ],
        "vendor": "Ericsson",
        "subsystemType": {
            "id": 2,
            "type": "NFVO",
            "category": "Primary"
        },
        "adapterLink": "eric-eo-ecm-adapter"
    }
]
```

The following table lists and describes the parameters for the get list
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the subsystem.|-|
|connectionProperties|array|required|The list of connection properties of the subsystem.|-|
|subsystemType|object|required|The subsystem type.|-|
|name|string|required| The name of the subsystem.|-|
|healthCheckTime|string|required|The health check time of the subsystem.|-|
|url|string|required|The url of the subsystem.|-|
|operationalState|string|required|The operational state of the subsystem.|-|
|subsystemTypeId|string|required|The identifier of the subsystem type.|-|
|vendor|string|required|The name of the subsystem vendor.|-|
|adapterLink|string|optional|The type of the adapter.|-|

## Create Subsystems

This API allows you to on-board a Connected System. When the connected system is
of type NFVO, multiple connection properties can be added with predefined fields;
while when the connected system is of type Domain Manager, a single connection
property is available with predefined fields.

**Path:** POST /subsystem-manager/v1/subsystems

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

The following is an example of an **postSubsystem**

```json
{
   "name":"ecm",
   "subsystemType":{
      "id":2,
      "type":"NFVO"
   },
   "adapterLink":"eric-eo-ecm-adapter",
   "vendor":"Ericsson",
   "url":"http://eric-eo-ecm-stub/",
   "connectionProperties":[
      {
         "name":"connection1",
         "tenant":"EcmTenant",
         "username":"ecmadmin",
         "password":"CloudAdmin123",
         "encryptedKeys":[
            "password"
         ]
      }
   ]
}
```

The following table lists and describes the parameters for the creation
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|connectionProperties|array|required|The list of connection properties of the subsystem.|-|
|subsystemType|object|required|The subsystem type.|-|
|name|string|required| The name of the subsystem.|-|
|url|string|required|The url of the subsystem.|-|
|vendor|string|required|The name of the subsystem vendor.|-|
|adapterLink|string|optional|The type of the adapter.|-|

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**201 - Created**| The Subsystem has been created successfully.| The response body is empty.|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**409 - Conflict**| Another request is in progress that prohibits the fulfilment of the current request, or the current resource state is inconsistent with the request.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-management-error-data-structure)|

**201 - Created Response:**

```json
{
    "id": 6,
    "subsystemTypeId": 2,
    "name": "ecm",
    "url": "https://eric-eo-ecm-stub",
       "connectionProperties": [
        {
            "id": 7,
            "subsystemId": 6,
            "name": "ECM",
            "tenant": "tenant1",
            "username": "ecmadmin",
            "password": "CloudAdmin123",
            "encryptedKeys": [
                "password"
            ],
            "subsystemUsers": []
        }
    ],
    "vendor": "Ericsson",
    "subsystemType": {
        "id": 2,
        "type": "NFVO",
        "category": "Primary"
    },
    "adapterLink": "eric-eo-ecm-adapter"
}
```

The following table lists and describes the parameters for the creation
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the subsystem.|-|
|connectionProperties|array|required|The list of connection properties of the subsystem.|-|
|subsystemType|object|required|The subsystem type.|-|
|name|string|required| The name of the subsystem.|-|
|healthCheckTime|string|required|The health check time of the subsystem.|-|
|url|string|required|The url of the subsystem.|-|
|operationalState|string|required|The operational state of the subsystem.|-|
|subsystemTypeId|string|required|The identifier of the subsystem type.|-|
|vendor|string|required|The name of the subsystem vendor.|-|
|adapterLink|string|optional|The type of the adapter.|-|

## Delete Multiple Subsystems

This API is used to remove multiple Subsystems from Service Orchestration.

**Path:** DELETE /subsystem-manager/v1/subsystems

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

The following is an example of an **deleteSubsystems**

```json
[263, 325]
```

The following table lists and describes the parameters for the delete
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|subsystemList|array|required|The list of the subsystem identifiers.|-|

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**204 - No Content**| The subsystem multiple delete operation was successful.| The response body is empty.|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**409 - Conflict**| Another request is in progress that prohibits the fulfilment of the current request, or the current resource state is inconsistent with the request.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**500 - Internal Server Error**|If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code. The ``ErrorMessage`` structure will be provided, and will include in the ``detail`` attribute more information about the source of the problem.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-management-error-data-structure)|

## Read Subsystem

This API is used to read a subsystem information identified by its subsystem
identifier. The "select" keyword query can be used to return all items of type
specified in the selectfield name, for example, "id".

**Path:** GET /subsystem-manager/v1/subsystems/{subsystemId}

**Path - Parameters:** subsystemId (required) —  Unique identifier of the subsystem.

**Query Parameters:**

- ``select`` (optional) |Subsystem identifier.
- ``tenantName`` (optional) |Tenant name.

**Request header:** Content-Type: application/json

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - OK**| Information about subsystems was read successfully.| See Below|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-management-error-data-structure)|

**200 - OK Response:**

```json
{
    "id": 2,
    "subsystemTypeId": 1,
    "name": "enm",
    "url": "https://eric-eo-enm-stub",
    "connectionProperties": [
        {
            "id": 3,
            "subsystemId": 2,
            "username": "anything",
            "password": "anything",
            "encryptedKeys": [
                "password"
            ],
            "subsystemUsers": []
        }
    ],
    "vendor": "Ericsson",
    "subsystemType": {
        "id": 1,
        "type": "DomainManager",
        "category": "Primary"
    }
}
```

The following table lists and describes the parameters for the read
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the subsystem.|-|
|connectionProperties|array|required|The list of connection properties of the subsystem.|-|
|subsystemType|object|required|The subsystem type.|-|
|name|string|required| The name of the subsystem.|-|
|healthCheckTime|string|required|The health check time of the subsystem.|-|
|url|string|required|The url of the subsystem.|-|
|operationalState|string|required|The operational state of the subsystem.|-|
|subsystemTypeId|string|required|The identifier of the subsystem type.|-|
|vendor|string|required|The name of the subsystem vendor.|-|
|adapterLink|string|optional|The type of the adapter.|-|

## Update Subsystem

This API allows you to update the details for existing Subsystem for
ServiceOrchestration.
Used only to update the name, url, and connectionProperties of the subsystem
Include the required properties to update all connectionProperties in the
request body is available with predefined fields.

**Path:** PUT /subsystem-manager/v1/subsystems/{subsystemId}

**Path - Parameters:** subsystemId (required) —  Unique identifier of the subsystem.

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

The following is an example of an **putSubsystem**

```json
{
   "name":"ecm",
   "subsystemType":{
      "id":2,
      "type":"NFVO"
   },
   "adapterLink":"eric-eo-ecm-adapter",
   "vendor":"Ericsson",
   "url":"http://eric-eo-ecm-stub/",
   "connectionProperties":[
      {
         "name":"connection1",
         "tenant":"EcmTenant",
         "username":"ecmadmin",
         "password":"CloudAdmin123",
         "encryptedKeys":[
            "password"
         ]
      },
      {
         "name":"connection2",
         "tenant":"EcmTenant",
         "username":"ecmadmin2",
         "password":"CloudAdmin123",
         "encryptedKeys":[
            "password"
         ]
      }
   ],
   "id":267
}
```

The following table lists and describes the parameters for the update
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the subsystem.|-|
|connectionProperties|array|required|The list of connection properties of the subsystem.|-|
|subsystemType|object|required|The subsystem type.|-|
|name|string|required| The name of the subsystem.|-|
|url|string|required|The url of the subsystem.|-|
|vendor|string|required|The name of the subsystem vendor.|-|
|adapterLink|string|optional|The type of the adapter.|-|

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - OK**| The Subsystem has been created or updated successfully.| The response body is empty.|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**409 - Conflict**| Another request is in progress that prohibits the fulfilment of the current request, or the current resource state is inconsistent with the request.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-management-error-data-structure)|

**200 - OK:**

```json
{
    "id": 6,
    "subsystemTypeId": 2,
    "name": "ecm",
    "url": "https://eric-eo-ecm-stub",
       "connectionProperties": [
        {
            "id": 7,
            "subsystemId": 6,
            "name": "ECM",
            "tenant": "tenant1",
            "username": "ecmadmin",
            "password": "CloudAdmin123",
            "encryptedKeys": [
                "password"
            ],
            "subsystemUsers": []
        }
    ],
    "vendor": "Ericsson",
    "subsystemType": {
        "id": 2,
        "type": "NFVO",
        "category": "Primary"
    },
    "adapterLink": "eric-eo-ecm-adapter"
}
```

The following table lists and describes the parameters for the update
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the subsystem.|-|
|connectionProperties|array|required|The list of connection properties of the subsystem.|-|
|subsystemType|object|required|The subsystem type.|-|
|name|string|required| The name of the subsystem.|-|
|healthCheckTime|string|required|The health check time of the subsystem.|-|
|url|string|required|The url of the subsystem.|-|
|operationalState|string|required|The operational state of the subsystem.|-|
|subsystemTypeId|string|required|The identifier of the subsystem type.|-|
|vendor|string|required|The name of the subsystem vendor.|-|
|adapterLink|string|optional|The type of the adapter.|-|

## Delete Subsystem

This API is used to remove a Subsystems from Service Orchestration.

**Path:** DELETE /subsystem-manager/v1/subsystems/{subsystemId}

**Path - Parameters:** subsystemId (required) —  Unique identifier of the subsystem.

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**204 - No Content**| The subsystem delete operation was successful.| The response body is empty.|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**409 - Conflict**| Another request is in progress that prohibits the fulfilment of the current request, or the current resource state is inconsistent with the request.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-management-error-data-structure)|

## Update Subsystem Information

This API allows you to update the subsystem information.

**Path:** PATCH /subsystem-manager/v1/subsystems/{subsystemId}

**Path - Parameters:** subsystemId (required) —  Unique identifier of the subsystem.

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

The following is an example of an **patchSubsystem**

```json
[
    {
        "name": "enm",
        "url": "https://eric-eo-enm-stub",
        "connectionProperties": [
            {
                "id": 3,
                "subsystemId": 2,
                "username": "anything",
                "password": "anything",
                "encryptedKeys": [
                    "password"
                ],
                "subsystemUsers": []
            }
        ],
        "vendor": "Ericsson",
        "subsystemType": {
            "id": 1,
            "type": "DomainManager",
            "category": "Primary"
        }
    },
    {
        "name": "ecm",
        "url": "https://eric-eo-ecm-stub",
        "connectionProperties": [
            {
                "id": 7,
                "subsystemId": 6,
                "name": "ECM",
                "tenant": "tenant1",
                "username": "ecmadmin",
                "password": "CloudAdmin123",
                "encryptedKeys": [
                    "password"
                ],
                "subsystemUsers": []
            }
        ],
        "vendor": "Ericsson",
        "subsystemType": {
            "id": 2,
            "type": "NFVO",
            "category": "Primary"
        },
        "adapterLink": "eric-eo-ecm-adapter"
    }
]
```

The following table lists and describes the parameters for the update
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|connectionProperties|array|required|The list of connection properties of the subsystem.|-|
|subsystemType|object|required|The subsystem type.|-|
|name|string|required| The name of the subsystem.|-|
|url|string|required|The url of the subsystem.|-|
|vendor|string|required|The name of the subsystem vendor.|-|
|adapterLink|string|optional|The type of the adapter.|-|

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - OK**| The Subsystem has been updated successfully.| The response body is empty.|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**409 - Conflict**| Another request is in progress that prohibits the fulfilment of the current request, or the current resource state is inconsistent with the request.|[Error Response Data Structure](#subsystem-management-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-management-error-data-structure)|

**200 - OK:**

```json
{
    "id": 6,
    "subsystemTypeId": 2,
    "name": "ecm",
    "url": "https://eric-eo-ecm-stub",
       "connectionProperties": [
        {
            "id": 7,
            "subsystemId": 6,
            "name": "ECM",
            "tenant": "tenant1",
            "username": "ecmadmin",
            "password": "CloudAdmin123",
            "encryptedKeys": [
                "password"
            ],
            "subsystemUsers": []
        }
    ],
    "vendor": "Ericsson",
    "subsystemType": {
        "id": 2,
        "type": "NFVO",
        "category": "Primary"
    },
    "adapterLink": "eric-eo-ecm-adapter"
}
```

The following table lists and describes the parameters for the update
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the subsystem.|-|
|connectionProperties|array|required|The list of connection properties of the subsystem.|-|
|subsystemType|object|required|The subsystem type.|-|
|name|string|required| The name of the subsystem.|-|
|healthCheckTime|string|required|The health check time of the subsystem.|-|
|url|string|required|The url of the subsystem.|-|
|operationalState|string|required|The operational state of the subsystem.|-|
|subsystemTypeId|string|required|The identifier of the subsystem type.|-|
|vendor|string|required|The name of the subsystem vendor.|-|
|adapterLink|string|optional|The type of the adapter.|-|

## Subsystem Management Error Data Structure

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|userMessage|``String``|Required|A short, human readable summary of the problem type. It should not change from occurrence to occurrence of the problem, except for purposes of localization. If type is given and other than ``about:blank``, this attribute is provided|-|
|errorCode|``String``|Required|The error code for this occurrence of the problem|-|
|developerMessage|``String``|Required|A human-readable explanation specific to this occurrence of the problem|-|
|errorData|``array[String]``|Optional|A list of error message parameters|-|
