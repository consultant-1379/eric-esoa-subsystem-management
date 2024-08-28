# Subsystem Users Service

- [Create and Update Subsystem Users](#create-and-update-subsystem-users)
- [Delete Subsystem Users](#delete-subsystem-users)
- [Error Data Structure](#subsystem-users-error-data-structure)

## Create and Update Subsystem Users

This API is used to configure the subsystem users associated with a specific
instance of a subsystem and connection properties.
An identifier is given by this service to identify uniquely the subsystem user
being created.

**Path:** POST /subsystem-manager/v1/subsystems/{subsystemId}/connection-properties/{connectionPropertiesId}/subsystem-users

**Path - Parameters:**

- ``subsystemId`` (required) — The subsystem identifier
- ``connectionPropertiesId`` (required) — The connection properties identifier

**Request header:** Content-Type: application/json

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code                 | Description | Response Data Structure|
|-------------------------------| ------ | ------ |
| **201 - Created**             | The subsystem user has been created successfully.| See Below|
| **400 - Bad Request**         |If the request is malformed or syn|
| **401 - Unauthorized**        | If the request does not contain |
| **403 - Forbidden**           | If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-users-error-data-structure)|
| **404 - Not Found**           |If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-users-error-data-structure)|
| **503 - Service Unavailable** |If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-users-error-data-structure)|

**201 - Created Response:**

```json
{
   "id":6,
   "connectionPropsId":3
}
```

The following table lists and describes the parameters for the creation
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|optional|The identifier of the subsystem user.|-|
|connectionProperties|object|optional|The connection property info.|-|
|connectionPropsId|integer|optional|The identifier of the connection properties.|-|

## Delete Subsystem Users

This API is used to delete the subsystem user associated to given subsystem
and connection properties.
The subsystem user is identified by the identifier being allocated as a result
of the creation operation.

**Path:** DELETE /subsystem-manager/v1/subsystems/{subsystemId}/connection-properties/{connectionPropertiesId}/subsystem-users/{subsystemUserId}

**Path - Parameters:**

- ``subsystemId`` (required) — The subsystem identifier
- ``connectionPropertiesId`` (required) — The connection properties identifier
- ``subsystemUserId`` (required) - The subsystem user identifier

**Responses:** This API call produces media type a according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code                 | Description | Response Data Structure|
|-------------------------------| ------ | ------ |
| **204 - No Content**          | The subsystem user has been deleted successfully.| The response body is empty.|
| **400 - Bad Request**         |If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-users-error-data-structure)|
| **401 - Unauthorized**        | If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-users-error-data-structure)|
| **403 - Forbidden**           | If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-users-error-data-structure)|
| **404 - Not Found**           |If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-users-error-data-structure)|
| **503 - Service Unavailable** |If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-users-error-data-structure)|

## Subsystem Users Error Data Structure

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|userMessage|``String``|Required|A short, human readable summary of the problem type. It should not change from occurrence to occurrence of the problem, except for purposes of localization. If type is given and other than ``about:blank``, this attribute is provided|-|
|errorCode|``String``|Required|The error code for this occurrence of the problem|-|
|developerMessage|``String``|Required|A human-readable explanation specific to this occurrence of the problem|-|
|errorData|``array[String]``|Optional|A list of error message parameters|-|
