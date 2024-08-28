# Tenant Mapping Service

- [Get List Of Tenant Mapping With Optional Query Parameters](#get-list-of-tenant-mapping-with-optional-query-parameters)
- [Create Tenant Mapping](#create-tenant-mapping)
- [Read Tenant Mapping By Name](#read-tenant-mapping-by-name)
- [Create And Update Tenant Mapping](#create-and-update-tenant-mapping)
- [Delete Tenant Mapping](#delete-tenant-mapping)
- [Error Data Structure](#tenant-mapping-error-data-structure)

## Get List Of Tenant Mapping With Optional Query Parameters

This API is used to retrieve the Tenants mappings in a paginated way to
specifically support GUI operations by means of query parameters.

**Path:** GET /subsystem-manager/v1/tenant-mappings

**Query Parameters:**

- ``offset`` (optional) |Specifies the starting element of the page
   of Tenants mappings.
- ``limit`` (optional) |The maximum number of Tenant mappings results
   to be returned, starting with offset (dictates size of result page).
- ``sortAttr`` (optional) |A Tenant attribute by which to sort the
   Tenants mappings.
- ``sortDir`` (optional) |The direction in which to sort the Tenants
   Mappings.
   Has no effect if sortAttr is not also specified. May be``ASCENDING``,
   ``DESCENDING``, or any fragment from the beginning of either
   (e.g. ASC or DESC); case-insensitive.
   Defaults to ``ASCENDING``.
- ``filters`` (optional) |Additional filter parameters for the Tenant
   Mappings resources to be returned.

**Request header:** Content-Type: application/json

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - OK**| Information about Tenant Mapping was read successfully.| See Below|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|

**200 - OK Response:**

```json
{
   "tenantName":"tenant-1",
   "subsystemId":10,
   "connectionProperties":[
      11,
      13
   ]
}
```

The following table lists and describes the parameters for the read
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|tenantName|string|required|The tenant name.|-|
|subsystemName|string|required|The subsystem name.|-|
|subsystemType|string|required|The subsystem type.|-|
|subsystemId|integer|required|The subsystem identifier.|-|
|connections|integer|required|The connections associated to the tenant.|-|
|vendor|string|required|The vendor name of the subsystem.|-|

## Create Tenant Mapping

This API allows you to create a tenant mapping between NFVO
and SO tenant.

**Path:** POST /subsystem-manager/v1/tenant-mappings

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

The following is an example of an **postSubsystem**

```json
{
   "tenantName":"EcmTenant",
   "subsystemId":81,
   "connectionProperties":[
      82
   ]
}
```

The following table lists and describes the parameters for the creation
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|tenantName|string|required|The tenant name.|-|
|subsystemId|integer|required|The subsystem identifier.|-|
|connectionProperties|array|required|The list of connection properties.|-|

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**201 - Created**| The Tenant Mapping has been created successfully.| The response body is empty.|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**409 - Conflict**| Another request is in progress that prohibits the fulfilment of the current request, or the current resource state is inconsistent with the request.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|

## Read Tenant Mapping By Name

This API is used to retrieve the tenant mapping given the tenant name and
subsystem identifier.

**Path:** GET  /subsystem-manager/v1/tenant-mappings/{tenantName}

**Path - Parameters:** tenantName (required) —  The name of the mapped tenant.

**Query Parameters:**

- ``subsystemId`` (required) |The subsystem identifier for the mapped tenant.

**Request header:** Content-Type: application/json

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**200 - OK**| Information about Tenant Mapping was read successfully.| See Below|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|

**200 - OK Response:**

```json
[
   {
      "id":15,
      "name":"tenant-1",
      "parentTenant":"ecm"
   }
]
```

The following table lists and describes the parameters for the read
response object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|id|integer|required|The connection properties identifier.|-|
|name|string|required|The connection properties name.|-|
|parentTenant|string|required|The name of the parent tenant assigned to the connection properties.|-|

## Create And Update Tenant Mapping

This API allows you to modify the list of connection properties in an existing
tenant mapping.

**Path:** PUT /subsystem-manager/v1/tenant-mappings/{tenantName}

**Path - Parameters:** tenantName (required) —  The name of the mapped tenant.

**Query Parameters:**

- ``subsystemId`` (required) |The subsystem identifier for the mapped tenant.

**Request header:** Content-Type: application/json

**Request Body:**
This API call consumes the following media types via the Content-Type
request header: ``application/json``

The following is an example of an **putTenantMappings**

```json
{
    "connectionProperties":[102, 107]
}
```

The following table lists and describes the parameters for the creation
request object.

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|connectionProperties|array|required|The connection properties associated to the tenant.|-|

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**204 - No Content**| The Tenant Mapping creation or update operation was successful.| The response body is empty.|
|**400 - Bad Request**|If the request is malformed or syntactically incorrect, for example, if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure, the API producer responds with this response code. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it responds with this response code. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code ("catch all error"), the API producer responds with this response code.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|

## Delete Tenant Mapping

Deletes the tenant mapping with the tenant name and subsystem Id.
In case the subsystemId is not specified, deletes all the tenants.

**Path:** DELETE /subsystem-manager/v1/tenant-mappings/{tenantName}

**Path - Parameters:** tenantName (required) —  The name of the mapped tenant.

**Query Parameters:**

- ``subsystemId`` (required) |The subsystem identifier for the mapped tenant.

**Responses:** This API call produces media type according to the
``Accept`` request header. The media type will be conveyed by the
Content-Type response header: ``application/json``

| Response Code | Description | Response Data Structure|
| ------ | ------ | ------ |
|**204 - No Content**| The subsystem delete operation was successful.| The response body is empty.|
|**401 - Unauthorized**| If the request does not contain an access token even though it is required, or if the request contains an authorization token that is invalid; for example: expired or revoked, the API producer should respond with this response. The details of the error is returned in the **WWW-Authenticate** HTTP  header, which is defined in [IETF RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) and [IETF RFC 7235](https://datatracker.ietf.org/doc/html/rfc7235). The ErrorMessage structure may be provided.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**403 - Forbidden**| If the API consumer is not allowed to complete a particular request to a particular resource, the API producer responds with this response code. The ``ErrorMessage`` structure will be provided. In the ``detail`` attribute, information about the source of the problem is included. The detail attribute may also include information about how to solve the problem.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|
|**404 - Not Found**|If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or it is not willing to disclose that one exists, it responds with this response code. The ``ErrorMessage`` structure may be provided. The ``detail`` attribute includes information about the source of the problem, for example, an incorrect resource URI variable.|[Error Response Data Structure](#tenant-mapping-error-data-structure)|

## Tenant Mapping Error Data Structure

| Name | Type | Required/Optional | Description | Enum |
| ------ | ------ | ------ | ------ | ------ |
|userMessage|``String``|Required|A short, human readable summary of the problem type. It should not change from occurrence to occurrence of the problem, except for purposes of localization. If type is given and other than ``about:blank``, this attribute is provided|-|
|errorCode|``String``|Required|The error code for this occurrence of the problem|-|
|developerMessage|``String``|Required|A human-readable explanation specific to this occurrence of the problem|-|
|errorData|``array[String]``|Optional|A list of error message parameters|-|
