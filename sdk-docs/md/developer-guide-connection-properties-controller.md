# Connection Properties Service

- [Get List Of Connection Properties](#get-list-of-connection-properties)
- [Create Connection Properties](#create-connection-properties)
- [Read Connection Property](#read-connection-property)
- [Update Connection Properties](#update-connection-properties)
- [Delete Connection Properties](#delete-connection-properties)
- [Error Data Structure](#connection-properties-error-data-structure)

## Get List Of Connection Properties

This API allows to get the list of all the connection properties for a given
subsystem identifier.

**Path:** GET /subsystem-manager/v1/subsystems/{subsystemId}/connection-properties

**Path - Parameters:**

- ``subsystemId`` (required) — The subsystem id

**Request header:** Content-Type: application/json

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - OK**| Information about connection properties was read successfully..| See Below|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**500 - Internal Server Error**|If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code. The ``ErrorMessage`` structure will be provided, and will include in the ``detail`` attribute more information about the source of the problem.|[Error Response Data Structure](#connection-properties-error-data-structure)|

**200 - OK Response:**

```json
[
    {
        "id": 3,
        "subsystemId": 2,
        "username": "postgres",
        "password": "postgres",
        "encryptedKeys": [
            "password"
        ],
        "subsystemUsers": []
    },
    {
        "id": 5,
        "subsystemId": 2,
        "username": "postgres",
        "password": "postgres",
        "encryptedKeys": [
            "password"
        ],
        "subsystemUsers": []
    }
]
```

The following table lists and describes the parameters for the read
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the connection property.|-|
|subsystemId|integer|required|The identifier of the subsystem.|-|
|username|string|required|The username.|-|
|password|string|required|The password|-|
|encryptedKeys|``array[String]``|required|The list of the encrypted Keys|
|subsystemUsers|``array[String]``|required|The list of subsystem users|

## Create Connection Properties

This API allows to create a set of connection properties on the specified
subsystem identifier.

**Path:** POST /subsystem-manager/v1/subsystems/{subsystemId}/connection-properties

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

The following is an example of an **postSubsystemType**

```json
{
    "subsystemId": 2,
    "username": "postgres",
    "password": "postgres",
    "encryptedKeys": [
        "password"
    ],
    "subsystemUsers": []
}
```

The following table lists and describes the parameters for the creation
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|subsystemId|integer|required|The subsystem id.|-|
|username|string|required| The name of the username.|-|
|password|string|required|The password.|-|
|encryptedKeys|``array[String]``|required|The list of the encrypted Keys|
|subsystemUsers|``array[String]``|required|The list of subsystem users|

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - Created**| The subsystem user has been created successfully.| See Below|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**500 - Internal Server Error**|If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code. The ``ErrorMessage`` structure will be provided, and will include in the ``detail`` attribute more information about the source of the problem.|[Error Response Data Structure](#connection-properties-error-data-structure)|

**201 - Created Response:**

```json
{
    "id": 12,
    "subsystemId": 2,
    "username": "postgres",
    "password": "postgres",
    "encryptedKeys": [
        "password"
    ],
    "subsystemUsers": []
}
```

The following table lists and describes the parameters for the creation
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the connection property.|-|
|subsystemId|integer|required|The subsystem id.|-|
|username|string|required| The name of the username.|-|
|password|string|required|The password.|-|
|encryptedKeys|``array[String]``|required|The list of the encrypted Keys|
|subsystemUsers|``array[String]``|required|The list of subsystem users|

## Read Connection Property

Reads the connection properties associated to a given connection property
identifier.

**Path:** GET  /subsystem-manager/v1/subsystems/{subsystemId}/connection-properties/{connectionPropertiesId}

**Path - Parameters:**

- ``subsystemId`` (required) — The subsystem identifier
- ``connectionPropertiesId`` (required) — The connection properties identifier

**Request header:** Content-Type: application/json

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - OK**| Information about subsystems was read successfully.| See Below|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#connection-properties-error-data-structure)|

**200 - OK Response:**

```json
{
    "id": 3,
    "subsystemId": 2,
    "username": "postgres",
    "password": "postgres",
    "encryptedKeys": [
        "password"
    ],
    "subsystemUsers": []
}
```

The following table lists and describes the parameters for the read
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the connection property.|-|
|subsystemId|integer|required|The identifier of the subsystem.|-|
|username|string|required|The username.|-|
|password|string|required|The password|-|
|encryptedKeys|``array[String]``|required|The list of the encrypted Keys|
|subsystemUsers|``array[String]``|required|The list of subsystem users|

## Update Connection Properties

This API allows adding or update properties associated with a subsystem object.
The required properties must be included since this updates the
connectionProperties to the request body.
The user must ensure that there are no errors. To check this, the user must do a
GET request on the required connection property.

**Path:** POST /subsystem-manager/v1/subsystems/{subsystemId}/connection-properties/{connectionPropertiesId}

**Path - Parameters:**

- ``subsystemId`` (required) — The subsystem identifier
- ``connectionPropertiesId`` (required) — The connection properties identifier

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

The following is an example of an **postSubsystemType**

```json
{
    "subsystemId": 2,
    "username": "postgres",
    "password": "postgres",
    "encryptedKeys": [
        "password"
    ],
    "subsystemUsers": []
}
```

The following table lists and describes the parameters for the update
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|subsystemId|integer|required|The subsystem id.|-|
|username|string|required| The name of the username.|-|
|password|string|required|The password.|-|
|encryptedKeys|``array[String]``|required|The list of the encrypted Keys|
|subsystemUsers|``array[String]``|required|The list of subsystem users|

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - Created**| The subsystem user has been created successfully.| See Below|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**500 - Internal Server Error**|If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code. The ``ErrorMessage`` structure will be provided, and will include in the ``detail`` attribute more information about the source of the problem.|[Error Response Data Structure](#connection-properties-error-data-structure)|

**201 - Created Response:**

```json
{
    "id": 12,
    "subsystemId": 2,
    "username": "postgres",
    "password": "postgres",
    "encryptedKeys": [
        "password"
    ],
    "subsystemUsers": []
}
```

The following table lists and describes the parameters for the update
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The identifier of the connection property.|-|
|subsystemId|integer|required|The subsystem id.|-|
|username|string|required| The name of the username.|-|
|password|string|required|The password.|-|
|encryptedKeys|``array[String]``|required|The list of the encrypted Keys|
|subsystemUsers|``array[String]``|required|The list of subsystem users|

## Delete Connection Properties

This API allows to delete the connection properties identified by the
connectionPropertiesId, from the subsystem identified by the subsystemId.

**Path:** DELETE /subsystem-manager/v1/subsystems/{subsystemId}/connection-properties/{connectionPropertiesId}

**Path - Parameters:**

- ``subsystemId`` (required) — The subsystem identifier
- ``connectionPropertiesId`` (required) — The connection properties identifier

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**204 - No Content**| The subsystem multiple delete operation was successful.| The response body is empty.|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**409 - Conflict**| Another request is in progress that prohibits the fulfilment of the current request, or the current resource state is inconsistent with the request.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**500 - Internal Server Error**|If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code. The ``ErrorMessage`` structure will be provided, and will include in the ``detail`` attribute more information about the source of the problem.|[Error Response Data Structure](#connection-properties-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#connection-properties-error-data-structure)|

## Connection Properties Error Data Structure

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|userMessage|``String``|Required|A short, human readable summary of the problem type. It should not change from occurrence to occurrence of the problem, except for purposes of localization. If type is given and other than ``about:blank``, this attribute is provided|-|
|errorCode|``String``|Required|The error code for this occurrence of the problem|-|
|developerMessage|``String``|Required|A human-readable explanation specific to this occurrence of the problem|-|
|errorData|``array[String]``|Optional|A list of error message parameters|-|