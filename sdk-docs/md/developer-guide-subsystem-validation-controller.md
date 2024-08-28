# Subsystem Validation Service

- [Create Subsystem Validation Request](#create-subsystem-validation-request)
- [Read Subsystem_Validation_Report](#read-subsystem-validation-report)
- [Error Data Structure](#subsystem-validation-error-data-structure)

## Create Subsystem Validation Request

This API allows to create a Subsystem Validation request.
The validation involves the external system URL and the credentials
given as configuration data for not yet created or existing Connected
Systems.
Currently, this feature is available only for Domain Manager and the NFVO scenarios.
Information to be given are the connected system name and its type, the external
system URL, the adapter link name, the vendor and the connection properties list.
For Domain Manager scenario, the connection properties list has one single item
indicating username and password.
For NFVO scenario, the connection properties list may contain more than one item
and each item should provide a username, a password, a tenant and a connection name.
Once the validation request is issued, a request identifier is given back and can
be used to read the results report when ready at a later moment.

**Path:** POST /subsystem-manager/v1/check-connectivity

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

The following are examples of **postCheckConnectivity** using the stubs.
In case of validation against a real external system, the HTTPS protocol
may be used for external system URL specification.

```json
{
  "name": "enm",
  "type": "DomainManager",
  "url": "http://eric-eo-enm-stub",
  "adapterLink": "eric-eo-enm-adapter",
  "vendor": "Ericsson",
  "connectionProperties":[{
    "username": "administrator",
    "password": "TestPassw0rd"
  }]
}
```

```json
{
  "name": "do",
  "type": "DomainOrchestrator",
  "url": "http://eric-oss-do-stub/tmf641-so-1",
  "adapterLink": "eric-oss-domain-orch-adapter",
  "vendor": "Ericsson",
  "connectionProperties":[{
    "username": "administrator",
    "password": "TestPassw0rd",
    "tenant": "master",
    "encryptedKeys": [],
    "auth_url": "/auth/v1",
    "auth_body": "{}",
    "auth_headers": "{\"Content-Type\":\"application/json\",\"Accept\":\"*/*\",\"X-login\":\"{{username}}\",\"X-password\":\"{{password}}\",\"X-tenant\":\"{{tenant}}\"}",
    "auth_key": "JSESSIONID",
    "auth_type": "Cookie"
  }]
}
```

```json
{
  "name": "ecm",
  "type": "NFVO",
  "adapterLink": "eric-eo-ecmsol005-adapter",
  "url": "http://eric-eo-ecmsol005-stub",
  "vendor": "Ericsson",
  "connectionProperties":[ {
     "username": "ecmadmin",
     "password": "CloudAdmin123",
     "name": "connection1",
     "tenant": "master"
  }]
}
```

```json
{
  "name": "ecm",
  "type": "NFVO",
  "adapterLink": "eric-eo-ecmsol005-adapter",
  "url": "http://eric-eo-ecmsol005-stub",
  "vendor": "Ericsson",
  "connectionProperties":[ {
     "username": "ecmadmin",
     "password": "CloudAdmin123",
     "name": "connection1",
     "tenant": "master"
  },
  {
     "username": "ecmadmin",
     "password": "CloudAdmin123",
     "name": "connection2",
     "tenant": "mytenant"
  }]
}
```

```json
{
  "name": "ecm",
  "type": "NFVO",
  "adapterLink": "eric-eo-sol005-adapter",
  "url": "http://eric-eo-sol005-stub",
  "vendor": "Ericsson",
  "connectionProperties":[{
     "username": "ecmadmin",
     "password": "CloudAdmin123",
     "name": "connection1",
     "tenant": "master"
  }]
}
```

```json
{
  "name": "ecm",
  "type": "NFVO",
  "adapterLink": "eric-eo-sol005-adapter",
  "url": "http://eric-eo-sol005-stub",
  "vendor": "Ericsson",
  "connectionProperties":[{
     "username": "ecmadmin",
     "password": "CloudAdmin123",
     "name": "connection1",
     "tenant": "master"
  },
  {
     "username": "ecmadmin",
     "password": "CloudAdmin123",
     "name": "connection2",
     "tenant": "mytenant"
  }]
}
```

The following table lists and describes the parameters for the creation
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|name|string|optional| The name of the subsystem.|-|
|type|string|required|The subsystem type.|-|
|adapterLink|string|optional|The type of the adapter.|-|
|url|string|required|The url of the subsystem.|-|
|vendor|string|required|The name of the subsystem vendor.|-|
|connectionProperties|array|required|The list of connection properties of the subsystem.|-|

The connection property has the following structure:

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|username|string|required|The external system login username.|-|
|password|string|required|The external system login password.|-|
|tenant|string|optional|The external system login tenant.|-|
|name|string|optional| The name of the connection property.|-|

The tenant and the name are used within the NFVO scenario.

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**201 - Created**| The Subsystem has been created successfully.| The response body is empty.|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-validation-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-validation-error-data-structure)|
|**409 - Conflict**| Another request is in progress that prohibits the fulfilment of the current request, or the current resource state is inconsistent with the request.|[Error Response Data Structure](#subsystem-validation-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-validation-error-data-structure)|

**201 - Created Response:**

```json
{
    "requestId": "f2fda610-2411-4453-bfc6-c19e8fc99b5a"
}
```

The following table lists and describes the parameters for the creation
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|requestId|uuid|required|The identifier of the request.|-|

## Read Subsystem Validation Report

This API allows to retrieve the report of the Subsystem
Validation request originally created by the above outlined
POST API and identified by the request id.
A request status is included inside the report whose value
can be one of:

- InProgress
- Completed
- Failed

Clients should poll InProgress report results until found
Completed or Failed.

**Path:** GET /subsystem-manager/v1/check-connectivity/{requestId}

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#subsystem-validation-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#subsystem-validation-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#subsystem-validation-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#subsystem-validation-error-data-structure)|
|**503 - Service Unavailable**|If the API producer encounters an internal overload situation of itself or of a system it relies on, it responds with this response code, from the provisions in [IETF RFC 7231](https://datatracker.ietf.org/doc/html/rfc7231), a **"Retry-After"** HTTP header or an alternative to refuse the connection will be returned. The ``ErrorMessage`` structure may be omitted.|[Error Response Data Structure](#subsystem-validation-error-data-structure)|

**200 - OK Response:**

The following are examples of **getCheckConnectivity** responses.

```json
[
  {
    "requestStatus": "InProgress"
  }
]
```

```json
[
  {
    "requestStatus": "Completed",
    "name": "enm",
    "successfulResult": true,
    "externalSystemHTTPcode": 200
  }
]
```

```json
[
    {
      "requestStatus": "Completed",
      "name": "ecm",
      "connectionName": "connection1",
      "successfulResult": true,
      "externalSystemHTTPcode": 200
    }
]
```

```json
[
  {
    "requestStatus": "Completed",
    "name": "enm",
    "successfulResult": false,
    "externalSystemHTTPcode": 401,
    "warningMessageCode": "INVALID_USER_PASSWORD",
    "warningMessage": "Credentials validation failed as the username and/or password are/is not valid"
  }
]
```

```json
[
    {
      "requestStatus": "Completed",
      "name": "ecm",
      "connectionName": "connection1",
      "successfulResult": false,
      "externalSystemHTTPcode": 401,
      "warningMessageCode": "INVALID_TENANT_USER_PASSWORD",
      "warningMessage": "Credentials validation failed as the username and/or password and/or tenant name are/is not valid"
    }
]
```

```json
[
    {
      "requestStatus": "Completed",
      "name": "ecm",
      "successfulResult": true,
      "connectionName": "connection1"
    },
    {
      "requestStatus": "Completed",
      "name": "ecm",
      "connectionName": "connection2",
      "successfulResult": false,
      "externalSystemHTTPcode": 401,
      "warningMessageCode": "INVALID_TENANT_USER_PASSWORD",
      "warningMessage": "Credentials validation failed as the username and/or password and/or tenant name are/is not valid"
   }
]
```

```json
[
  {
    "requestStatus": "Completed",
    "name": "ecm",
    "connectionName": "connection1",
    "successfulResult": false,
    "externalSystemHTTPcode": 503,
    "warningMessageCode": "EXTERNAL_SYSTEM_NOT_REACHABLE",
    "warningMessage": "Connection failed as the external system is not reachable"
  },
  {
    "requestStatus": "Completed",
    "name": "ecm",
    "connectionName": "connection2",
    "successfulResult": false,
    "externalSystemHTTPcode": 503,
    "warningMessageCode": "EXTERNAL_SYSTEM_NOT_REACHABLE",
    "warningMessage": "Connection failed as the external system is not reachable"
  }
]
```

In case a business logic exception has occurred, preventing from establishing
a result for the URL and credentials validation, an error message is included
inside the result report. For example:

```json
[
  {
    "requestStatus": "Failed",
    "name": "enm",
    "successfulResult": false,
    "errorMessage": {
      "errorCode": "SSM-C-44",
      "userMessage": "The connection and credentials validation operations failed due to: 'exception in adapter business logic'"
    }
  }
]
```

The following table lists and describes the parameters for the
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|requestStatus|enum|required|The status of the validation request.| InProgress/Completed/Failed|
|name|string|optional|The name of the subsystem.|-|
|connectionName|string|optional|The name of the connection property.|-|
|successfulResult|boolean|required|The validation request success.|-|
|warningCode|string|optional|The warning message code.|-|
|warningMessage|string|optional|The warning message text.|-|
|errorMessage|object|optional|The error message.|-|

## Subsystem Validation Error Data Structure

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|userMessage|``String``|Required|A short, human readable summary of the problem type. It should not change from occurrence to occurrence of the problem, except for purposes of localization. If type is given and other than ``about:blank``, this attribute is provided|-|
|errorCode|``String``|Required|The error code for this occurrence of the problem|-|
|developerMessage|``String``|Required|A human-readable explanation specific to this occurrence of the problem|-|
|errorData|``array[String]``|Optional|A list of error message parameters|-|