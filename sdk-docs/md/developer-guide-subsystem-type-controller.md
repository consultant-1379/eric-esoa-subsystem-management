# Subsystem Type Service

- [Read Subsystem Types](#read-subsystem-types)
- [Create Subsystem Type](#create-subsystem-type)
- [Delete Subsystem Type](#delete-subsystem-type)
- [Error Data Structure](#subsystem-type-error-data-structure)

## Read Subsystem Types

Subsystem Type Service manages the types of subsystems EO-SO can connect and
interwork with.
Each subsystem type is identified by an unique identifier and a descriptive type
string.
The list of connected subsystem types can be retrieved by means of SO REST i/f
as herein outlined.
This API is used to retrieves subsystem types. Category can be specified to obtain
primary, or custom types only.

**Path:** GET /subsystem-manager/v1/subsystem-types

**Query Parameters:**

- ``category`` (optional) |Allows the user to get subsystem types based on category.

**Request header:** Content-Type: application/json

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - OK**| Information about subsystem types was read successfully.| See Below|
|**401 - Unauthorized**| If the request does not contain|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-type-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-type-error-data-structure)|

**200 - OK Response:**

```json
[
    {
        "id": 1,
        "type": "DomainManager",
        "category": "Primary"
    },
    {
        "id": 2,
        "type": "NFVO",
        "category": "Primary"
    },
    {
        "id": 5,
        "type": "PhysicalDevice",
        "category": "Custom"
    },
    {
        "id": 6,
        "type": "CmGateway",
        "category": "Custom"
    },
    {
        "id": 7,
        "type": "InventorySystem",
        "category": "Custom"
    },
    {
        "id": 8,
        "type": "DomainOrchestrator",
        "category": "Primary"
    }
]
```

The following table lists and describes the parameters for the read
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the subsystem type.|-|
|type|string|required|The type of the subsystem.|-|
|category|enum|required|The category of the subsystem type.|Primary/Custom|

## Create Subsystem Type

This API allows to create a custom Subsystem Type.

**Path:** POST /subsystem-manager/v1/subsystem-types

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

The following is an example of an **postSubsystemType**

```json
{
  "type": "myCustomSubsystemType"
}
```

The following table lists and describes the parameters for the read
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|type|string|required|Name of the new Subsystem Type.|-|

**Responses:** This API call produces media type a according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**201 - Created**| The Subsystem Type has been created successfully.| The response body is empty.|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-type-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-type-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-type-error-data-structure)|

## Delete Subsystem Type

This API is used to Delete a Subsystem Type.

**Path:** DELETE /subsystem-manager/v1/subsystem-types/{id}

**Path - Parameters:**

- ``id`` (required) — Id of the custom Subsystem Type to delete

**Responses:** This API call produces media type a according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**201 - Created**| The Subsystem Type has been deleted successfully.| The response body is empty.|
|**204 - No Content**|Subsystem Type has been created | The response body is empty.|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-type-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-type-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-type-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-type-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-type-error-data-structure)|

## Subsystem Type Error Data Structure

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|userMessage|``String``|Required|A short, human readable summary of the problem type. It should not change from occurrence to occurrence of the problem, except for purposes of localization. If type is given and other than ``about:blank``, this attribute is provided|-|
|errorCode|``String``|Required|The error code for this occurrence of the problem|-|
|developerMessage|``String``|Required|A human-readable explanation specific to this occurrence of the problem|-|
|errorData|``array[String]``|Optional|A list of error message parameters|-|
